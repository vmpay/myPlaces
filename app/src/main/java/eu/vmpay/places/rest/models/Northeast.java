
package eu.vmpay.places.rest.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Northeast
{

	@SerializedName("lat")
	@Expose
	public Double lat;
	@SerializedName("lng")
	@Expose
	public Double lng;

}
