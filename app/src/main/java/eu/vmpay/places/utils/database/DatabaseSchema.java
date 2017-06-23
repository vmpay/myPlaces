package eu.vmpay.places.utils.database;

import eu.vmpay.places.utils.database.models.MyPlacesModel;

/**
 * Created by andrew on 23/06/2017.
 */

public class DatabaseSchema
{
	public static final String DATABASE_NAME = "placesdatabase";
	public static final int DATABASE_VERSION = 1;

	public static final String[] TABLE_NAMES = new String[]
			{
					MyPlacesModel.MyPlacesNames.TABLENAME,
			};

	public static String[] TABLE_CREATE_QUERIES = new String[]
			{
					MyPlacesModel.MyPlacesNames.CREATE_TABLE,
			};
}
