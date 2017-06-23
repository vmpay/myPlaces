package eu.vmpay.places;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import eu.vmpay.places.rest.IRestClient;
import eu.vmpay.places.rest.RestClient;
import eu.vmpay.places.utils.database.DatabaseAccess;
import eu.vmpay.places.utils.database.DatabaseSchema;
import eu.vmpay.places.utils.database.IDatabaseClient;
import eu.vmpay.places.utils.database.models.MyPlacesModel;

/**
 * Created by andrew on 23.06.17.
 */

public class AppController
{
	private final String TAG = "AppController";

	private static AppController instance = new AppController();
	private Context context;
	private boolean initialized = false;
	private String googleApiKey = "";

	public static AppController getInstance()
	{
		return instance;
	}

	private IRestClient restClient;
	private IDatabaseClient databaseClient;

	private List<MyPlacesModel> myPlacesModelList = new ArrayList<>();

	public void init(Context context)
	{
		this.context = context;

		if (!initialized)
		{
			try {
				ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
				Bundle bundle = ai.metaData;
				googleApiKey = bundle.getString("com.google.android.geo.API_KEY");
			} catch (PackageManager.NameNotFoundException e) {
				Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
			} catch (NullPointerException e) {
				Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
			}

			restClient = new RestClient(googleApiKey);
			databaseClient = new DatabaseAccess(DatabaseSchema.DATABASE_NAME, DatabaseSchema.DATABASE_VERSION, context, DatabaseSchema.TABLE_NAMES, DatabaseSchema.TABLE_CREATE_QUERIES);
			Fresco.initialize(context);
			initialized = true;

			myPlacesModelList.clear();
			myPlacesModelList.addAll(MyPlacesModel.buildFromContentValuesList(databaseClient.select(new MyPlacesModel())));
		}
	}

	public IRestClient getRestClient()
	{
		return restClient;
	}

	public IDatabaseClient getDatabaseClient()
	{
		return databaseClient;
	}

	public void updateResultList(List<MyPlacesModel> myPlacesModelList)
	{
		this.myPlacesModelList.clear();
		this.myPlacesModelList.addAll(myPlacesModelList);
	}

	public String getGoogleApiKey()
	{
		return googleApiKey;
	}

	public List<MyPlacesModel> getMyPlacesModelList()
	{
		return myPlacesModelList;
	}
}
