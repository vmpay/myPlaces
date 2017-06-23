package eu.vmpay.places.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import eu.vmpay.places.R;
import eu.vmpay.places.activities.PlaceDetailActivity;
import eu.vmpay.places.fragments.PlaceDetailFragment;
import eu.vmpay.places.utils.database.models.MyPlacesModel;

/**
 * Created by andrew on 23.06.17.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder>
{
	private final String TAG = "PlaceAdapter";

	private List<MyPlacesModel> myPlacesModelList;
	private boolean mTwoPane;
	private FragmentManager fragmentManager;

	public PlaceAdapter(List<MyPlacesModel> myPlacesModelList, boolean mTwoPane, FragmentManager fragmentManager)
	{
		this.myPlacesModelList = myPlacesModelList;
		this.mTwoPane = mTwoPane;
		this.fragmentManager = fragmentManager;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.place_item_layout, parent, false);
		TextView name = (TextView) view.findViewById(R.id.tvName);
		TextView address = (TextView) view.findViewById(R.id.tvAddress);
		SimpleDraweeView icon = (SimpleDraweeView) view.findViewById(R.id.ivImage);
		ViewHolder viewHolder = new ViewHolder(view, name, address, icon);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, final int position)
	{
		holder.item = myPlacesModelList.get(position);
		holder.name.setText(holder.item.getName());
		holder.address.setText(holder.item.getAddress());
		holder.icon.setImageURI(holder.item.getImage());
		holder.rootView.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(mTwoPane)
				{
					Bundle arguments = new Bundle();
					arguments.putInt(PlaceDetailFragment.ARG_ITEM_ID, position);
					PlaceDetailFragment fragment = new PlaceDetailFragment();
					fragment.setArguments(arguments);
					fragmentManager.beginTransaction()
							.replace(R.id.place_detail_container, fragment)
							.commit();
				}
				else
				{
					Context context = v.getContext();
					Intent intent = new Intent(context, PlaceDetailActivity.class);
					intent.putExtra(PlaceDetailFragment.ARG_ITEM_ID, position);

					context.startActivity(intent);
				}
			}
		});
	}

	@Override
	public int getItemCount()
	{
		return myPlacesModelList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder
	{
		public View rootView;
		public TextView name;
		public TextView address;
		public SimpleDraweeView icon;
		public MyPlacesModel item;

		public ViewHolder(View rootView, TextView name, TextView address, SimpleDraweeView icon)
		{
			super(rootView);
			this.rootView = rootView;
			this.name = name;
			this.address = address;
			this.icon = icon;
		}
	}

	public void setTwoPane(boolean mTwoPane)
	{
		this.mTwoPane = mTwoPane;
	}
}
