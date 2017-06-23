package eu.vmpay.places.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;

import eu.vmpay.places.R;
import eu.vmpay.places.fragments.PlaceDetailFragment;

/**
 * Created by andrew on 23.06.17.
 */
public class PlaceDetailActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_detail);
		Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null)
		{
			actionBar.setDisplayHomeAsUpEnabled(true);
		}

		if(savedInstanceState == null)
		{
			Bundle arguments = new Bundle();
			arguments.putInt(PlaceDetailFragment.ARG_ITEM_ID,
					getIntent().getIntExtra(PlaceDetailFragment.ARG_ITEM_ID, 0));
			PlaceDetailFragment fragment = new PlaceDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.place_detail_container, fragment)
					.commit();
		}
	}
}
