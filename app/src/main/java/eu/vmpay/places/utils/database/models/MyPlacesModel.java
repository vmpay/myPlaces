package eu.vmpay.places.utils.database.models;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import eu.vmpay.places.rest.models.Result;
import eu.vmpay.places.utils.database.DataModel;

/**
 * Created by andrew on 23/06/2017.
 */

public class MyPlacesModel extends DataModel
{
	private String placeId;
	private String name;
	private String address;
	private double positionLatitude, positionLongitude;
	private String image;

	public static class MyPlacesNames
	{
		public final static String ID = DataModel.ID;
		public final static String PLACE_ID = "place_id";
		public final static String NAME = "name";
		public final static String ADDRESS = "address";
		public final static String POSITION_LATITUDE = "latitude";
		public final static String POSITION_LONGITUDE = "longitude";
		public final static String IMAGE = "image";

		public final static String TABLENAME = "my_devices_table";
		public final static String CREATE_TABLE = "CREATE TABLE " + TABLENAME +
				" (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				PLACE_ID + " TEXT UNIQUE NOT NULL, " +
				NAME + " TEXT NOT NULL, " +
				ADDRESS + " TEXT NOT NULL, " +
				POSITION_LATITUDE + " TEXT NOT NULL, " +
				POSITION_LONGITUDE + " TEXT NOT NULL, " +
				IMAGE + " TEXT" +
				");";
	}

	public MyPlacesModel()
	{
		tableName = MyPlacesNames.TABLENAME;
		columns = new String[] {
				MyPlacesNames.ID,
				MyPlacesNames.PLACE_ID,
				MyPlacesNames.NAME,
				MyPlacesNames.ADDRESS,
				MyPlacesNames.POSITION_LATITUDE,
				MyPlacesNames.POSITION_LONGITUDE,
				MyPlacesNames.IMAGE,
		};
	}

	public MyPlacesModel(Result result)
	{
		this();
		placeId = result.getId();
		name = result.getName();
		address = result.getVicinity();
		positionLatitude = result.getGeometry().getLocation().getLat();
		positionLongitude = result.getGeometry().getLocation().getLng();
		image = result.getIcon();
	}

	@NonNull
	public static List<MyPlacesModel> buildFromContentValuesList(List<ContentValues> contentValuesList)
	{
		List<MyPlacesModel> myPlacesModels = new ArrayList<>();
		if(contentValuesList != null)
		{
			for(ContentValues contentValues : contentValuesList)
			{
				myPlacesModels.add(buildFromContentValues(contentValues));
			}
		}
		return myPlacesModels;
	}

	public static MyPlacesModel buildFromContentValues(ContentValues contentValues)
	{
		MyPlacesModel myPlacesModel = new MyPlacesModel();

		myPlacesModel.setId(contentValues.getAsLong(MyPlacesNames.ID));
		myPlacesModel.setPlaceId(contentValues.getAsString(MyPlacesNames.PLACE_ID));
		myPlacesModel.setName(contentValues.getAsString(MyPlacesNames.NAME));
		myPlacesModel.setAddress(contentValues.getAsString(MyPlacesNames.ADDRESS));
		myPlacesModel.setPositionLatitude(contentValues.getAsDouble(MyPlacesNames.POSITION_LATITUDE));
		myPlacesModel.setPositionLongitude(contentValues.getAsDouble(MyPlacesNames.POSITION_LONGITUDE));
		myPlacesModel.setImage(contentValues.getAsString(MyPlacesNames.IMAGE));

		return myPlacesModel;
	}

	@Override
	public ContentValues getInsert()
	{
		contentValues = new ContentValues();
		contentValues.put(MyPlacesNames.PLACE_ID, placeId);
		contentValues.put(MyPlacesNames.NAME, name);
		contentValues.put(MyPlacesNames.ADDRESS, address);
		contentValues.put(MyPlacesNames.POSITION_LATITUDE, positionLatitude);
		contentValues.put(MyPlacesNames.POSITION_LONGITUDE, positionLongitude);
		contentValues.put(MyPlacesNames.IMAGE, image);

		return contentValues;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public void setPositionLatitude(double positionLatitude)
	{
		this.positionLatitude = positionLatitude;
	}

	public void setPositionLongitude(double positionLongitude)
	{
		this.positionLongitude = positionLongitude;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

	public void setPlaceId(String placeId)
	{
		this.placeId = placeId;
	}

	public String getName()
	{
		return name;
	}

	public String getAddress()
	{
		return address;
	}

	public double getPositionLatitude()
	{
		return positionLatitude;
	}

	public double getPositionLongitude()
	{
		return positionLongitude;
	}

	public String getImage()
	{
		return image;
	}

	public String getPlaceId()
	{
		return placeId;
	}
}
