package eu.vmpay.places.utils.database;

import android.content.ContentValues;

/**
 * Created by andrew on 23.06.17.
 */

public abstract class DataModel
{
	public static final String ID = "id";

	protected String tableName;
	protected String[] columns;
	protected String whereClause = null;
	protected ContentValues contentValues;

	private long id;

	public String getTableName()
	{
		return tableName;
	}

	public String[] getColumns()
	{
		return columns;
	}

	public String getWhereClause()
	{
		return whereClause;
	}

	public DataModel setWhereClause(String whereClause)
	{
		this.whereClause = whereClause;
		return this;
	}

	public ContentValues getContentValues()
	{
		return contentValues;
	}

	public void setContentValues(ContentValues contentValues)
	{
		this.contentValues = contentValues;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public long getId()
	{
		return id;
	}

	public void setThisIdWhereClause()
	{
		this.whereClause = DataModel.ID + "=" + id;
	}

	public abstract ContentValues getInsert();
}
