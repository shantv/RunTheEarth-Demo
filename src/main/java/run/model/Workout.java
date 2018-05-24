package run.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import run.config.View;

@Document(collection="workouts")
public class Workout implements Serializable {
	@Id
	@JsonView(View.Summary.class)
	private String workoutId;
	@JsonIgnore
	private String userId;
	@JsonProperty(value="start_datetime")			private Date startTime;
	@JsonProperty(value="name")						private String name;
	@JsonProperty(value="updated_datetime")			private Date updatedTime;
	@JsonProperty(value="created_datetime")			private Date createdTime;
	@JsonProperty(value="notes")					private String notes;
	@JsonProperty(value="reference_key")			private String referenceKey;
	@JsonProperty(value="start_locale_timezone")	private String locale;
	@JsonProperty(value="source")					private String source;
	@JsonProperty(value="source_manufacturer")		private String sourceMfg;
	@JsonProperty(value="_links")					private Map<String, MapMyRunLink[]> links;
	@JsonProperty(value="has_time_series")			private boolean hasTimeSeries;
	@JsonProperty(value="is_verified")				private boolean isVerified;
	@JsonProperty(value="aggregates")				private Map<String,Object> aggregates;
	@JsonView(View.Summary.class)
	private GeoLocation location;
	
	public Workout() {}

	@JsonView(View.Summary.class)
	@JsonProperty("activityId")
	@AccessType(Type.PROPERTY)
	public String getActivityId() {
		return getActivityType().getId();
	}
	@JsonView(View.Summary.class)
	@JsonProperty("routeId")
	@AccessType(Type.PROPERTY)
	public String getRouteId() {
		return getRoute().getId();
	}
	@AccessType(Type.PROPERTY)
	public String getWorkoutId() {
		return getSelf().getId();
	}
	public MapMyRunLink getSelf() {
		return links.get("self")[0];
	}
	@AccessType(Type.PROPERTY)
	public String getUserId() {
		return getUserDetail().getId();
	}
	public MapMyRunLink getActivityType() {
		if (!links.containsKey("activity_type")) return new MapMyRunLink();
		return links.get("activity_type")[0];
	}
	public MapMyRunLink getRoute() {
		if (!links.containsKey("route")) return new MapMyRunLink();
		return links.get("route")[0];
	}
	public MapMyRunLink getUserDetail() {
		return links.get("user")[0];
	}

	public GeoLocation getLocation() {
		return location;
	}

	public void setLocation(GeoLocation location) {
		this.location = location;
	}

	public String getSourceMfg() {
		return sourceMfg;
	}

	public void setSourceMfg(String sourceMfg) {
		this.sourceMfg = sourceMfg;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getReferenceKey() {
		return referenceKey;
	}

	public void setReferenceKey(String referenceKey) {
		this.referenceKey = referenceKey;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Map<String, MapMyRunLink[]> getLinks() {
		return links;
	}

	public void setLinks(Map<String, MapMyRunLink[]> links) {
		this.links = links;
	}

	public boolean isHasTimeSeries() {
		return hasTimeSeries;
	}

	public void setHasTimeSeries(boolean hasTimeSeries) {
		this.hasTimeSeries = hasTimeSeries;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Map<String, Object> getAggregates() {
		return aggregates;
	}

	public void setAggregates(Map<String, Object> aggregates) {
		this.aggregates = aggregates;
	}

	@Override
	public String toString() {
		return "Workout [startTime=" + startTime + ", name=" + name + ", updatedTime=" + updatedTime + ", createdTime="
				+ createdTime + ", notes=" + notes + ", referenceKey=" + referenceKey + ", locale=" + locale
				+ ", source=" + source + ", links=" + links + ", hasTimeSeries=" + hasTimeSeries + ", isVerified="
				+ isVerified + ", aggregates=" + aggregates + "]";
	}
	
}

