package eu.vmpay.places.rest;

import eu.vmpay.places.rest.models.Places;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Cancellable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by andrew on 23/06/2017.
 */

public class RestClient implements IRestClient
{
	private final String TAG = "RestClient";

	private Retrofit retrofit;
	private Observable<Places> placesObservable;
	private String apiKey;

	public RestClient(String apiKey)
	{
		this.apiKey = apiKey;
		retrofit = new Retrofit.Builder()
				.baseUrl("https://maps.googleapis.com/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
	}

	public Observable<Places> enqueuePlaces(final double lat, final double lon)
	{
		placesObservable = Observable.create(new ObservableOnSubscribe<Places>()
		{
			@Override
			public void subscribe(@NonNull final ObservableEmitter<Places> e) throws Exception
			{
				GooglePlacesService googlePlacesService = retrofit.create(GooglePlacesService.class);
				final Call<Places> call = googlePlacesService.getPlaces(lat + "," + lon, 1500, apiKey);
				e.setCancellable(new Cancellable()
				{
					@Override
					public void cancel() throws Exception
					{
						if (!call.isCanceled())
						{
							call.cancel();
						}
					}
				});
				call.enqueue(new Callback<Places>()
				{
					@Override
					public void onResponse(Call<Places> call, Response<Places> response)
					{
						e.onNext(response.body());
						e.onComplete();
					}

					@Override
					public void onFailure(Call<Places> call, Throwable t)
					{
						if (!e.isDisposed())
						{
							e.onError(t);
						}
					}
				});
			}
		})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
		return placesObservable;
	}

	public Observable<Places> enqueuePlaces()
	{
		return enqueuePlaces(52, 21);
	}

}
