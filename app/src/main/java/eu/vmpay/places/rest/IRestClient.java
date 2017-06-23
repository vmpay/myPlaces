package eu.vmpay.places.rest;

import eu.vmpay.places.rest.models.Places;
import io.reactivex.Observable;

/**
 * Created by andrew on 23.06.17.
 */

public interface IRestClient
{
	Observable<Places> enqueuePlaces();
}
