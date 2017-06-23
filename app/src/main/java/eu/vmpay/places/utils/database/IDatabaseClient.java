package eu.vmpay.places.utils.database;

import android.content.ContentValues;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by andrew on 23.06.17.
 */

public interface IDatabaseClient
{
	void openDatabase();

	void closeDatabase();

	long insert(DataModel databaseModel);

	DbOperationStatus insertOrUpdate(DataModel databaseModel);

	long update(DataModel databaseModel);

	int delete(DataModel databaseModel);

	List<ContentValues> select(DataModel databaseModel);

	List<ContentValues> select(DataModel databaseModel, int limit);

	<T extends DataModel> Observable<DbOperationStatus> insertOrUpdateRxStyle(List<T> DataModelList);
}
