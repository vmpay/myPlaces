
package eu.vmpay.places.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location
{

	@SerializedName("lat")
	@Expose
	public Double lat;
	@SerializedName("lng")
	@Expose
	public Double lng;

	public Double getLat()
	{
		return lat;
	}

	public Double getLng()
	{
		return lng;
	}
}
