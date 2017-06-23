package eu.vmpay.places.rest;

import eu.vmpay.places.rest.models.Places;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by andrew on 23.06.17.
 */

public interface GooglePlacesService
{
	@GET("maps/api/place/nearbysearch/json")
	Call<Places> getPlaces(@Query("location") String location, @Query("radius") double radius, @Query("key") String key);
}
