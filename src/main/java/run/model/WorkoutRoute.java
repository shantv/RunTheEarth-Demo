package run.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import run.config.View;


@Document(collection="routes")
public class WorkoutRoute implements Serializable {
	@Id
	@JsonView(View.Summary.class)
	private String routeId;
	@JsonProperty(value="total_descent")		private double totalDescent;
	@JsonProperty(value="city")					private String city;
	@JsonProperty(value="data_source")			private String dataSource;
	@JsonProperty(value="description")			private String description;
	@JsonProperty(value="updated_datetime")		private Date updateDate;
	@JsonProperty(value="created_datetime")		private Date createdDate;
	@JsonProperty(value="country")				private String country;
	@JsonProperty(value="start_point_type")		private String startPointType;
	@JsonProperty(value="starting_location")	private Object startLocation;
	@JsonProperty(value="distance")				private double distance;
	@JsonView(View.Summary.class)
	@JsonProperty(value="name")					private String name;
	@JsonProperty(value="climbs")				private Object[] climbs;
	@JsonProperty(value="state")				private String state;
	@JsonProperty(value="max_elevation")		private double maxElevation;
	@JsonProperty(value="images")				private Object[] images;
	@JsonView(View.Summary.class)
	@JsonProperty(value="_links")				private Map<String,MapMyRunLink[]> links;
	@JsonView(View.Summary.class)				
	@JsonProperty(value="points")				private List<RouteLocation> points;
	@JsonProperty(value="total_ascent")			private double totalAscent;
	@JsonView(View.Summary.class)
	private String activityIcon;
	
	public WorkoutRoute() {}
	
	public double getTotalDescent() {
		return totalDescent;
	}

	public String getActivityIcon() {
		return activityIcon;
	}

	public void setActivityIcon(String activityIcon) {
		this.activityIcon = activityIcon;
	}

	public void setTotalDescent(double totalDescent) {
		this.totalDescent = totalDescent;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStartPointType() {
		return startPointType;
	}

	public void setStartPointType(String startPointType) {
		this.startPointType = startPointType;
	}

	public Object getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Object startLocation) {
		this.startLocation = startLocation;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getClimbs() {
		return climbs;
	}

	public void setClimbs(Object[] climbs) {
		this.climbs = climbs;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public double getMaxElevation() {
		return maxElevation;
	}

	public void setMaxElevation(double maxElevation) {
		this.maxElevation = maxElevation;
	}

	public Object[] getImages() {
		return images;
	}

	public void setImages(Object[] images) {
		this.images = images;
	}

	@JsonView(View.Summary.class)
	@JsonProperty("activityId")
	@AccessType(Type.PROPERTY)
	public String getActivityId() {
		return getActivityType().getId();
	}
	@AccessType(Type.PROPERTY)
	public String getRouteId() {
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
		if (!links.containsKey("activity_types")) return new MapMyRunLink();
		return links.get("activity_types")[0];
	}
	public MapMyRunLink getUserDetail() {
		return links.get("user")[0];
	}
	public Map<String, MapMyRunLink[]> getLinks() {
		return links;
	}

	public void setLinks(Map<String, MapMyRunLink[]> links) {
		this.links = links;
	}

	public List<RouteLocation> getPoints() {
		return points;
	}

	public void setPoints(List<RouteLocation> points) {
		this.points = points;
	}

	public double getTotalAscent() {
		return totalAscent;
	}

	public void setTotalAscent(double totalAscent) {
		this.totalAscent = totalAscent;
	}

	@Override
	public String toString() {
		return "WorkoutRoute [routeId=" + routeId + ", totalDescent=" + totalDescent + ", city=" + city
				+ ", dataSource=" + dataSource + ", description=" + description + ", updateDate=" + updateDate
				+ ", createdDate=" + createdDate + ", country=" + country + ", startPointType=" + startPointType
				+ ", startLocation=" + startLocation + ", distance=" + distance + ", name=" + name + ", climbs="
				+ Arrays.toString(climbs) + ", state=" + state + ", maxElevation=" + maxElevation + ", images="
				+ Arrays.toString(images) + ", links=" + links + ", points=" + points + ", totalAscent=" + totalAscent
				+ "]";
	}

	
}

