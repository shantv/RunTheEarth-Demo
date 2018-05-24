package run.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection="user")
public class UserDetail implements Serializable {
	@Id
	private String userId;
	private String authToken;
	@JsonProperty(value="last_name")					private String lastName;
	@JsonProperty(value="weight")						private double weight;
	@JsonProperty(value="communication")				private Map<String,String> communications;
	@JsonProperty(value="height")						private double height;
	@JsonProperty(value="hobbies")						private String hobbies;
	@JsonProperty(value="date_joined")					private Date dateJoined;
	@JsonProperty(value="first_name")					private String firstName;
	@JsonProperty(value="display_name")					private String displayName;
	@JsonProperty(value="introduction")					private String introduction;
	@JsonProperty(value="display_measurement_system")	private String displayMeasurementSystem;
	@JsonProperty(value="last_login")					private Date lastLogin;
	@JsonProperty(value="goal_statement")				private String goalStatement;
	@JsonProperty(value="_links")						private Map<String,MapMyRunLink[]> links;
	@JsonProperty(value="email")						private String email;
	@JsonProperty(value="location")						private Map<String,Object> location;
	@JsonProperty(value="username")						private String username;
	@JsonProperty(value="sharing")						private Map<String,Object> sharing;
	@JsonProperty(value="last_initial")					private String lastInitial;
	@JsonProperty(value="preferred_language")			private String preferredLanguage;
	@JsonProperty(value="gender")						private String gender;
	@JsonProperty(value="time_zone")					private String timeZone;
	@JsonProperty(value="birthdate")					private String birthdate;
	@JsonProperty(value="profile_statement")			private String profileStatement;
	
	public UserDetail() {}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	@AccessType(Type.PROPERTY)
	public String getUserId() {
		return getSelf().getId();
	}
	public MapMyRunLink getSelf() {
		return links.get("self")[0];
	}

	public Map<String, MapMyRunLink[]> getLinks() {
		return links;
	}

	public void setLinks(Map<String, MapMyRunLink[]> links) {
		this.links = links;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Map<String, String> getCommunications() {
		return communications;
	}

	public void setCommunications(Map<String, String> communications) {
		this.communications = communications;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public String getHobbies() {
		return hobbies;
	}

	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}

	public Date getDateJoined() {
		return dateJoined;
	}

	public void setDateJoined(Date dateJoined) {
		this.dateJoined = dateJoined;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getDisplayMeasurementSystem() {
		return displayMeasurementSystem;
	}

	public void setDisplayMeasurementSystem(String displayMeasurementSystem) {
		this.displayMeasurementSystem = displayMeasurementSystem;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getGoalStatement() {
		return goalStatement;
	}

	public void setGoalStatement(String goalStatement) {
		this.goalStatement = goalStatement;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Map<String, Object> getLocation() {
		return location;
	}

	public void setLocation(Map<String, Object> location) {
		this.location = location;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Map<String, Object> getSharing() {
		return sharing;
	}

	public void setSharing(Map<String, Object> sharing) {
		this.sharing = sharing;
	}

	public String getLastInitial() {
		return lastInitial;
	}

	public void setLastInitial(String lastInitial) {
		this.lastInitial = lastInitial;
	}

	public String getPreferredLanguage() {
		return preferredLanguage;
	}

	public void setPreferredLanguage(String preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getProfileStatement() {
		return profileStatement;
	}

	public void setProfileStatement(String profileStatement) {
		this.profileStatement = profileStatement;
	}

	@Override
	public String toString() {
		return "MapMyRunUser [lastName=" + lastName + ", weight=" + weight + ", communications=" + communications
				+ ", height=" + height + ", hobbies=" + hobbies + ", userId=" + getUserId() + ", dateJoined=" + dateJoined
				+ ", firstName=" + firstName + ", displayName=" + displayName + ", introduction=" + introduction
				+ ", displayMeasurementSystem=" + displayMeasurementSystem + ", lastLogin=" + lastLogin
				+ ", goalStatement=" + goalStatement + ", links=" + links + ", email=" + email + ", location="
				+ location + ", username=" + username + ", sharing=" + sharing + ", lastInitial=" + lastInitial
				+ ", preferredLanguage=" + preferredLanguage + ", gender=" + gender + ", timeZone=" + timeZone
				+ ", birthdate=" + birthdate + ", profileStatement=" + profileStatement + "]";
	}
	
}
