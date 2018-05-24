package run.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import org.springframework.data.annotation.AccessType;
import org.springframework.data.annotation.AccessType.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection="friends")
public class Friend implements Serializable {
	@Id
	private String friendId;
	@JsonProperty(value="status")			private String status;
	@JsonProperty(value="created_datetime")	private Date createdDate;
	@JsonProperty(value="messages")			private String messages;
	@JsonProperty(value="_links")			private Map<String, MapMyRunLink[]> links;
	private String friendUserId;
	private String userId;
	
	public Friend() {}


	public void setFriendUserId(String friendUserId) {
		this.friendUserId = friendUserId;
	}

	@AccessType(Type.PROPERTY)
	public String getFriendId() {
		return getSelf().getId();
	}
	@AccessType(Type.PROPERTY)
	public String getFriendUserId() {
		return getFriendUser().getId();
	}
	@AccessType(Type.PROPERTY)
	public String getUserId() {
		return getUser().getId();
	}
	public MapMyRunLink getSelf() {
		return links.get("self")[0];
	}
	public MapMyRunLink getFriendUser() {
		return links.get("to_user")[0];
	}
	public MapMyRunLink getUser() {
		return links.get("from_user")[0];
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	public Map<String, MapMyRunLink[]> getLinks() {
		return links;
	}

	public void setLinks(Map<String, MapMyRunLink[]> links) {
		this.links = links;
	}


	@Override
	public String toString() {
		return "Friend [friendId=" + friendId + ", status=" + status + ", createdDate=" + createdDate + ", messages="
				+ messages + ", links=" + links + ", friendUserId=" + friendUserId + ", userId=" + userId + "]";
	}

	
}
