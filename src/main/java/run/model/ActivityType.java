package run.model;

import java.io.Serializable;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection="activity")
public class ActivityType implements Serializable {
	@Id
	@Field("activityId")
	private String activityId;
	@JsonProperty(value="mets")				private double mets;
	@JsonProperty(value="mets_speed")		private Object metsSpeed;
	@JsonProperty(value="name")				private String name;
	@JsonProperty(value="short_name")		private String shortName;
	@JsonProperty(value="has_children")		private boolean hasChildren;
	@JsonProperty(value="import_only")		private boolean importOnly;
	@JsonProperty(value="location_aware")	private String locationAware;
	@JsonProperty(value="_links")			private Map<String,MapMyRunLink[]> links ;
	@JsonProperty(value="template")			private Object template;
	
	public ActivityType() {
	}

	public double getMets() {
		return mets;
	}

	public void setMets(double mets) {
		this.mets = mets;
	}

	public Object getMetsSpeed() {
		return metsSpeed;
	}

	public void setMetsSpeed(Object metsSpeed) {
		this.metsSpeed = metsSpeed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public boolean isImportOnly() {
		return importOnly;
	}

	public void setImportOnly(boolean importOnly) {
		this.importOnly = importOnly;
	}

	public String getLocationAware() {
		return locationAware;
	}

	public void setLocationAware(String locationAware) {
		this.locationAware = locationAware;
	}
	@AccessType(Type.PROPERTY)
	public String getActivityId() {
		return getSelf().getId();
	}
	public MapMyRunLink getSelf() {
		return links.get("self")[0];
	}
	public Map<String,MapMyRunLink[]> getLinks() {
		return links;
	}

	public void setLinks(Map<String,MapMyRunLink[]> links) {
		this.links = links;
	}

	public Object getTemplate() {
		return template;
	}

	public void setTemplate(Object template) {
		this.template = template;
	}

	@Override
	public String toString() {
		return "ActivityType [activityId=" + activityId + ", name=" + name + ", shortName=" + shortName + "]";
	}

	
}
