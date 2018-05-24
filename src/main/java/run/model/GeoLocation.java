package run.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GeoLocation implements Serializable {
	@Transient @JsonIgnore @JsonProperty("status") private String status;
	@Transient @JsonIgnore @JsonProperty("results") private List<Map<String,Object>> results;
	@Transient @JsonIgnore @JsonProperty("formatted_address") private String formattedAddress;
	@Transient @JsonIgnore @JsonProperty("geometry") private Map geometry;
	@Transient @JsonIgnore @JsonProperty("place_id") private String placeId;
	@Transient @JsonIgnore @JsonProperty("types") private String[] types;
	public GeoLocation() {}

	@JsonIgnore
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	@JsonIgnore
	public List<Map<String,Object>> getResults() {
		return results;
	}

	public void setResults(List<Map<String,Object>> results) {
		this.results = results;
	}
	@JsonIgnore
	@AccessType(Type.PROPERTY)
	public Map<String,Object> getCountry() {
		return (Map<String,Object>) ((List)getResults().get(0).get("address_components")).get(1);
	}
	@JsonIgnore
	@AccessType(Type.PROPERTY)
	public Map<String,Object> getState() {
		return (Map<String,Object>) ((List)getResults().get(0).get("address_components")).get(0);
	}
	@Field("countryCode")
	@AccessType(Type.PROPERTY)
	public String getCountryCode() {
		return (String)getCountry().get("short_name");
	}
	@Field("countryName")
	@AccessType(Type.PROPERTY)
	public String getCountryName() {
		return (String)getCountry().get("long_name");
	}
	@Field("stateName")
	@AccessType(Type.PROPERTY)
	public String getStateName() {
		return (String)getState().get("long_name");
	}
	@Field("stateCode")
	@AccessType(Type.PROPERTY)
	public String getStateCode() {
		return (String)getState().get("short_name");
	}

	@Override
	public String toString() {
		return "GeoLocation [status=" + status + ", results=" + results + ", getState()=" + getState()
				+ ", getCountryCode()=" + getCountryCode() + ", getCountryName()=" + getCountryName()
				+ ", getStateName()=" + getStateName() + ", getStateCode()=" + getStateCode() + "]";
	}


}
