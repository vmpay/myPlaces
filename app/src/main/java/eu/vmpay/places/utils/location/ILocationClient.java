package eu.vmpay.places.utils.location;

import android.location.Location;

import io.reactivex.Observable;

/**
 * Created by Andrew on 01/07/2017.
 */

public interface ILocationClient
{
	void getLastKnownLocation();

	Observable<Location> getLastKnownPosition();
}
