package eu.vmpay.places.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import eu.vmpay.places.AppController;
import eu.vmpay.places.R;
import eu.vmpay.places.utils.database.models.MyPlacesModel;

/**
 * Created by andrew on 23.06.17.
 */
public class PlaceDetailFragment extends Fragment implements OnMapReadyCallback
{
	public static final String ARG_ITEM_ID = "item_id";

	private MyPlacesModel mItem;
	private GoogleMap mMap;

	public PlaceDetailFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if(getArguments().containsKey(ARG_ITEM_ID))
		{
			mItem = AppController.getInstance().getMyPlacesModelList().get(getArguments().getInt(ARG_ITEM_ID));
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.place_detail, container, false);

		if(mItem != null)
		{
			((TextView) rootView.findViewById(R.id.place_detail)).setText(mItem.getName() + "\n" + mItem.getAddress());
		}

		SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		return rootView;
	}

	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		Log.d("detalFragment", "onMapReady");
		mMap = googleMap;

		LatLng location = new LatLng(mItem.getPositionLatitude(), mItem.getPositionLongitude());
		mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
		mMap.addMarker(new MarkerOptions().position(location).title(mItem.getName()));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(location));


	}
}
