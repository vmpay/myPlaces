package eu.vmpay.places;

import android.app.Activity;
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
import eu.vmpay.places.utils.location.ILocationClient;
import eu.vmpay.places.utils.location.LocationClient;

/**
 * Created by andrew on 23.06.17.
 */

public class AppController
{
	private static AppController instance = new AppController();
	private final String TAG = "AppController";
	private Context context;
	private boolean initialized = false;
	private String googleApiKey = "";
	private IRestClient restClient;
	private IDatabaseClient databaseClient;
	private ILocationClient locationClient;
	private List<MyPlacesModel> myPlacesModelList = new ArrayList<>();

	public static AppController getInstance()
	{
		return instance;
	}

	public void init(Activity activity)
	{
		this.context = activity;

		if (!initialized)
		{
			try {
				ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
				Bundle bundle = ai.metaData;
				googleApiKey = bundle.getString("com.google.android.geo.API_KEY");
			} catch (PackageManager.NameNotFoundException e) {
				Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
			} catch (NullPointerException e) {
				Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
			}

			locationClient = new LocationClient(activity);
			restClient = new RestClient(googleApiKey);
			databaseClient = new DatabaseAccess(DatabaseSchema.DATABASE_NAME, DatabaseSchema.DATABASE_VERSION, activity, DatabaseSchema.TABLE_NAMES, DatabaseSchema.TABLE_CREATE_QUERIES);
			Fresco.initialize(activity);
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

	public ILocationClient getLocationClient()
	{
		return locationClient;
	}
}
