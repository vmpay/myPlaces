package eu.vmpay.places.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by andrew on 23.06.17.
 */

public class DatabaseAccess implements IDatabaseClient
{
	private static final String TAG = "DatabaseAccess";
	private final String databaseName;
	private int databaseVersion;

	private DbHelper dbHelper = null;
	private SQLiteDatabase database = null;
	private Context context;

	private final String[] tableNames;
	private final String[] tableCreateQueries;

	public DatabaseAccess(String dbName, int databaseVersion, Context context, String[] tableNames, String[] tableCreateQueries)
	{
		this.databaseName = dbName;
		this.context = context;
		this.databaseVersion = databaseVersion;
		this.tableNames = tableNames;
		this.tableCreateQueries = tableCreateQueries;
		openDatabase();
	}

	class DbHelper extends SQLiteOpenHelper
	{
		public DbHelper(Context context)
		{
			super(context, databaseName, null, databaseVersion);
		}

		@Override
		public void onCreate(SQLiteDatabase sqLiteDatabase)
		{
			if(tableCreateQueries == null) return;
			for(String tableCreateQuery : tableCreateQueries)
			{
				sqLiteDatabase.execSQL(tableCreateQuery);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
		{
			if(tableNames == null) return;
			for(String tableName : tableNames)
			{
				sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + tableName);
			}
			onCreate(sqLiteDatabase);
		}
	}

	@Override
	public void openDatabase()
	{
		closeDatabase();

		if(dbHelper == null)
		{
			dbHelper = new DbHelper(context);
			database = dbHelper.getWritableDatabase();
		}
	}

	@Override
	public void closeDatabase()
	{
		if(dbHelper != null)
		{
			dbHelper.close();
			dbHelper = null;
		}
	}

	@Override
	public long insert(DataModel databaseModel)
	{
		ContentValues contentValues = databaseModel.getInsert();
		long success = -1;
		try
		{
			success = database.insertWithOnConflict(databaseModel.getTableName(), null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
		} catch(RuntimeException e)
		{
			e.printStackTrace();
		}
		return success;
	}

	public DbOperationStatus insertOrUpdate(DataModel databaseModel)
	{
		DbOperationStatus type = DbOperationStatus.FAILURE;
		long success = insert(databaseModel);
		if(success == -1)
		{
			success = update(databaseModel);
		}
		else
		{
			databaseModel.setId(success);
			type = DbOperationStatus.INSERTED;
		}
		if(success != 0)
		{
			type = DbOperationStatus.UPDATED;
		}
		return type;
	}

	@Override
	public long update(DataModel databaseModel)
	{
		long success = -1;

		boolean isEmpty = false;
		if(TextUtils.isEmpty(databaseModel.getWhereClause()))
		{
			isEmpty = true;
			databaseModel.setThisIdWhereClause();
		}

		try
		{
			success = database.update(
					databaseModel.getTableName(),
					databaseModel.getInsert(),
					databaseModel.getWhereClause(),
					null);
		} catch(SQLException e)
		{
			e.printStackTrace();
		}

		if(isEmpty)
		{
			databaseModel.setWhereClause(null);
		}

		return success;
	}

	@Override
	public int delete(DataModel databaseModel)
	{
		int success = -1;
		try
		{
			success = database.delete(databaseModel.getTableName(), databaseModel.getWhereClause(), null);
		} catch(SQLException e)
		{
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public List<ContentValues> select(DataModel databaseModel)
	{
		return select(databaseModel, -1);
	}

	@Override
	public List<ContentValues> select(DataModel databaseModel, int limit)
	{
		Cursor cursor = null;
		List<ContentValues> results = null;
		try
		{
			cursor = database.query(databaseModel.getTableName(), databaseModel.getColumns(),
					databaseModel.getWhereClause(), null, null, null, null, limit < 0 ? null : Integer.toString(limit));
		} catch(SQLException e)
		{
			e.printStackTrace();
		} finally
		{
			results = new ArrayList<>();
			if(cursor != null)
			{
				for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
				{
					ContentValues contentValues = new ContentValues();
					for(int i = 0; i < databaseModel.getColumns().length; i++)
					{
						int columnIndex = cursor.getColumnIndex(databaseModel.getColumns()[i]);
						switch(cursor.getType(columnIndex))
						{
							case Cursor.FIELD_TYPE_STRING:
								String stringValue = cursor.getString(columnIndex);
								if(stringValue != null)
								{
									contentValues.put(databaseModel.getColumns()[i], stringValue);
								}
								break;
							case Cursor.FIELD_TYPE_BLOB:
								byte[] blobValue = cursor.getBlob(columnIndex);
								if(blobValue != null)
								{
									contentValues.put(databaseModel.getColumns()[i], blobValue);
								}
								break;
							case Cursor.FIELD_TYPE_INTEGER:
								Integer intValue = cursor.getInt(columnIndex);
								if(intValue != null)
								{
									contentValues.put(databaseModel.getColumns()[i], intValue);
								}
								break;
							case Cursor.FIELD_TYPE_FLOAT:
								Float floatValue = cursor.getFloat(columnIndex);
								if(floatValue != null)
								{
									contentValues.put(databaseModel.getColumns()[i], floatValue);
								}
								break;
							case Cursor.FIELD_TYPE_NULL:
								contentValues.putNull(databaseModel.getColumns()[i]);
								break;
						}
					}
					results.add(contentValues);
				}
				cursor.close();
			}
		}
		return results;
	}

	public <T extends DataModel> Observable<DbOperationStatus> insertOrUpdateRxStyle(final List<T> DataModelList)
	{
		Observable<DbOperationStatus> observable = Observable.create(new ObservableOnSubscribe<DbOperationStatus>()
		{
			@Override
			public void subscribe(@NonNull ObservableEmitter<DbOperationStatus> e) throws Exception
			{
				for(DataModel entry : DataModelList)
				{
					e.onNext(insertOrUpdate(entry));
				}
				e.onComplete();
			}
		})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
		return observable;
	}
}
