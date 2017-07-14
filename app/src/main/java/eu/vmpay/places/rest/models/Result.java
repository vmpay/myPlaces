
package eu.vmpay.places.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result
{

	@SerializedName("geometry")
	@Expose
	public Geometry geometry;
	@SerializedName("icon")
	@Expose
	public String icon;
	@SerializedName("id")
	@Expose
	public String id;
	@SerializedName("name")
	@Expose
	public String name;
	@SerializedName("place_id")
	@Expose
	public String placeId;
	@SerializedName("reference")
	@Expose
	public String reference;
	@SerializedName("scope")
	@Expose
	public String scope;
	@SerializedName("types")
	@Expose
	public List<String> types = null;
	@SerializedName("vicinity")
	@Expose
	public String vicinity;
	@SerializedName("rating")
	@Expose
	public Double rating;
	@SerializedName("opening_hours")
	@Expose
	public OpeningHours openingHours;
	@SerializedName("photos")
	@Expose
	public List<Photo> photos = null;

	public Geometry getGeometry()
	{
		return geometry;
	}

	public String getIcon()
	{
		return icon;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getVicinity()
	{
		return vicinity;
	}

	public String getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return "Result{" +
				"geometry=" + geometry +
				", icon='" + icon + '\'' +
				", id='" + id + '\'' +
				", name='" + name + '\'' +
				", scope='" + scope + '\'' +
				", types=" + types +
				", vicinity='" + vicinity + '\'' +
				", rating=" + rating +
				'}';
	}
}
