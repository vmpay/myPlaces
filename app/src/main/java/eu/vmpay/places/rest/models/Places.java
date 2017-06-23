
package eu.vmpay.places.rest.models;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Places
{

	@SerializedName("html_attributions")
	@Expose
	public List<Object> htmlAttributions = null;
	@SerializedName("next_page_token")
	@Expose
	public String nextPageToken;
	@SerializedName("results")
	@Expose
	public List<Result> results = null;
	@SerializedName("status")
	@Expose
	public String status;

	public List<Result> getResults()
	{
		return results;
	}
}
