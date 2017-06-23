package eu.vmpay.places.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import eu.vmpay.places.AppController;
import eu.vmpay.places.R;

import eu.vmpay.places.rest.IRestClient;
import eu.vmpay.places.rest.models.Places;
import eu.vmpay.places.rest.models.Result;
import eu.vmpay.places.utils.PlaceAdapter;
import eu.vmpay.places.utils.database.DbOperationStatus;
import eu.vmpay.places.utils.database.IDatabaseClient;
import eu.vmpay.places.utils.database.models.MyPlacesModel;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew on 23.06.17.
 */
public class PlaceListActivity extends AppCompatActivity
{
	private final String TAG = "PlaceListActivity";

	private boolean mTwoPane;
	private AppController appController;
	private IRestClient restClient;
	private IDatabaseClient databaseClient;
	private CompositeDisposable compositeDisposable;
	private PlaceAdapter placeAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_list);

		Log.d(TAG, "PlaceListActivity onCreate()");

		appController = AppController.getInstance();
		appController.init(this);
		restClient = appController.getRestClient();
		databaseClient = appController.getDatabaseClient();
		compositeDisposable = new CompositeDisposable();

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		toolbar.setTitle(getTitle());

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				refresh(view);
			}
		});

		View recyclerView = findViewById(R.id.place_list);
		assert recyclerView != null;
		setupRecyclerView((RecyclerView) recyclerView);

		if(findViewById(R.id.place_detail_container) != null)
		{
			mTwoPane = true;
		}
		placeAdapter.setTwoPane(mTwoPane);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "PlaceListActivity onDestroy()");
		compositeDisposable.dispose();
	}

	private void setupRecyclerView(@NonNull RecyclerView recyclerView)
	{
		placeAdapter = new PlaceAdapter(appController.getMyPlacesModelList(), mTwoPane, getSupportFragmentManager());
		recyclerView.setAdapter(placeAdapter);
	}

	private void refresh(final View view)
	{
		Log.d(TAG, "Refresh! Menu");
		view.setEnabled(false);
		Observable<Places> placesObserver = restClient.enqueuePlaces();
		compositeDisposable.add(placesObserver.subscribeWith(new DisposableObserver<Places>()
		{
			@Override
			public void onNext(@io.reactivex.annotations.NonNull Places places)
			{
				Log.d(TAG, "===================================JSON===================================");
				for(Result entry : places.getResults())
				{
					Log.d(TAG, entry.toString());
				}
				List<MyPlacesModel> myPlacesModelList = new ArrayList<>();
				for(Result entry : places.getResults())
				{
					MyPlacesModel myPlacesModel = new MyPlacesModel(entry);
					myPlacesModel.setWhereClause(MyPlacesModel.MyPlacesNames.PLACE_ID + "='" + myPlacesModel.getPlaceId() + "'");
					myPlacesModelList.add(myPlacesModel);
				}
				compositeDisposable.add(databaseClient.insertOrUpdateRxStyle(myPlacesModelList).subscribeWith(new DisposableObserver<DbOperationStatus>()
				{
					@Override
					public void onNext(@io.reactivex.annotations.NonNull DbOperationStatus dbOperationStatus)
					{
						Log.d(TAG, "insertOrUpdateRxStyle result " + dbOperationStatus.name());
					}

					@Override
					public void onError(@io.reactivex.annotations.NonNull Throwable e)
					{
						Log.d(TAG, "===================================DB ERROR===================================");
					}

					@Override
					public void onComplete()
					{
						Log.d(TAG, "===================================DB COMPLETED===================================");
					}
				}));
				appController.updateResultList(myPlacesModelList);
				placeAdapter.notifyDataSetChanged();
			}

			@Override
			public void onError(@io.reactivex.annotations.NonNull Throwable e)
			{
				Log.d(TAG, "===================================JSON ERROR===================================");
				Log.d(TAG, e.toString());
				view.setEnabled(true);
				Snackbar.make(view, "Refresh failed", Snackbar.LENGTH_SHORT)
						.setAction("Action", null).show();
			}

			@Override
			public void onComplete()
			{
				Log.d(TAG, "===================================JSON COMPLETED===================================");
				view.setEnabled(true);
				Snackbar.make(view, "Refresh completed", Snackbar.LENGTH_SHORT)
						.setAction("Action", null).show();
			}
		}));
	}
}
