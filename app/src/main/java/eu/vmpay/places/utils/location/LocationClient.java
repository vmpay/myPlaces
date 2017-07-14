package eu.vmpay.places.utils.location;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Andrew on 01/07/2017.
 */

public class LocationClient implements ILocationClient
{
	private static final int REQUEST_CHECK_SETTINGS = 298;
	private static final int REQUEST_CHECK_PERMISSION = 299;
	private final String TAG = "LocationClient";

	private FusedLocationProviderClient mFusedLocationClient;
	private Activity activity;

	public LocationClient(Activity activity)
	{
		mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
		this.activity = activity;
	}

	@Override
	public void getLastKnownLocation()
	{
		if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
		{
			// TODO: Consider calling
			ActivityCompat.requestPermissions(activity, new String[] {
					Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CHECK_PERMISSION);
			return;
		}
		mFusedLocationClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>()
		{
			@Override
			public void onSuccess(Location location)
			{
				if(location != null)
				{
					Log.d(TAG, "Location " + location.toString() + " Alt " + location.getLatitude() + " Lon " + location.getLongitude());
				}
				else
				{
					getCurrentLocation();
				}
			}
		});
	}

	@Override
	public Observable<Location> getLastKnownPosition()
	{
		return Observable.create(new ObservableOnSubscribe<Location>()
		{
			@Override
			public void subscribe(@io.reactivex.annotations.NonNull final ObservableEmitter<Location> e) throws Exception
			{
				if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
				{
					// TODO: Consider calling
					ActivityCompat.requestPermissions(activity, new String[] {
							Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CHECK_PERMISSION);
					return;
				}
				mFusedLocationClient.getLastLocation().addOnSuccessListener(activity, new OnSuccessListener<Location>()
				{
					@Override
					public void onSuccess(Location location)
					{
						if(location != null)
						{
							Log.d(TAG, "Location " + location.toString() + " Alt " + location.getLatitude() + " Lon " + location.getLongitude());
							e.onNext(location);
							e.onComplete();
						}
						else
						{
							final LocationRequest mLocationRequest = new LocationRequest();
							mLocationRequest.setInterval(10000);
							mLocationRequest.setFastestInterval(5000);
							mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
							LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
									.addLocationRequest(mLocationRequest);
							SettingsClient client = LocationServices.getSettingsClient(activity);
							Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
							task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>()
							{
								@Override
								public void onSuccess(LocationSettingsResponse locationSettingsResponse)
								{
									if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
									{
										// TODO: Consider calling
										return;
									}
									LocationCallback mLocationCallback = new LocationCallback()
									{
										@Override
										public void onLocationResult(LocationResult locationResult)
										{
											for(Location location : locationResult.getLocations())
											{
												Log.d(TAG, "onLocationResult Lat " + location.getLatitude() + " Lon " + location.getLongitude());
												e.onNext(location);
												e.onComplete();
												break;
											}
										}
									};
									mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
								}
							});
							task.addOnFailureListener(activity, new OnFailureListener()
							{
								@Override
								public void onFailure(@NonNull Exception onFailureListener)
								{
									int statusCode = ((ApiException) onFailureListener).getStatusCode();
									switch(statusCode)
									{
										case CommonStatusCodes.RESOLUTION_REQUIRED:
											// Location settings are not satisfied, but this can be fixed
											// by showing the user a dialog.
											try
											{
												// Show the dialog by calling startResolutionForResult(),
												// and check the result in onActivityResult().
												ResolvableApiException resolvable = (ResolvableApiException) onFailureListener;
												resolvable.startResolutionForResult(activity,
														REQUEST_CHECK_SETTINGS);
											} catch(IntentSender.SendIntentException sendEx)
											{
												// Ignore the error.
											}
											break;
										case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
											e.onError(onFailureListener);
											// Location settings are not satisfied. However, we have no way
											// to fix the settings so we won't show the dialog.
											break;
									}
								}
							});
						}
					}
				});
			}
		})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	private void getCurrentLocation()
	{
		final LocationRequest mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10000);
		mLocationRequest.setFastestInterval(5000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
				.addLocationRequest(mLocationRequest);
		SettingsClient client = LocationServices.getSettingsClient(activity);
		Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
		task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>()
		{
			@Override
			public void onSuccess(LocationSettingsResponse locationSettingsResponse)
			{
				if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
				{
					// TODO: Consider calling
					return;
				}
				LocationCallback mLocationCallback = new LocationCallback()
				{
					@Override
					public void onLocationResult(LocationResult locationResult)
					{
						for(Location location : locationResult.getLocations())
						{
							Log.d(TAG, "onLocationResult Lat " + location.getLatitude() + " Lon " + location.getLongitude());
						}
					}
				};
				mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
			}
		});
		task.addOnFailureListener(activity, new OnFailureListener()
		{
			@Override
			public void onFailure(@NonNull Exception e)
			{
				int statusCode = ((ApiException) e).getStatusCode();
				switch(statusCode)
				{
					case CommonStatusCodes.RESOLUTION_REQUIRED:
						// Location settings are not satisfied, but this can be fixed
						// by showing the user a dialog.
						try
						{
							// Show the dialog by calling startResolutionForResult(),
							// and check the result in onActivityResult().
							ResolvableApiException resolvable = (ResolvableApiException) e;
							resolvable.startResolutionForResult(activity,
									REQUEST_CHECK_SETTINGS);
						} catch(IntentSender.SendIntentException sendEx)
						{
							// Ignore the error.
						}
						break;
					case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
						// Location settings are not satisfied. However, we have no way
						// to fix the settings so we won't show the dialog.
						break;
				}
			}
		});
	}
}
