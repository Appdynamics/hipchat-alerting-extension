package com.appdynamics.extensions.hipchat;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Room {

    @SerializedName("room_id")          private int id;
    @SerializedName("name")             private String name;
    @SerializedName("topic")            private String topic;
    @SerializedName("last_active")      private Date lastActive;
    @SerializedName("created")          private Date created;
    @SerializedName("owner_user_id")    private int ownerId;
    @SerializedName("is_archived")      private Boolean archivedRoom;
    @SerializedName("is_private")       private Boolean privateRoom;
    @SerializedName("xmpp_jid")         private String xmppJId;
    @SerializedName("guest_access_url") private String guestAccessUrl;
    @SerializedName("member_user_ids")  private List<Integer> memberIds;
    @SerializedName("participants")     private List<User> participants;
	private Boolean allowGuests;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public Date getLastActive() {
		return lastActive;
	}
	public void setLastActive(Date lastActive) {
		this.lastActive = lastActive;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public int getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	public Boolean getArchivedRoom() {
		return archivedRoom;
	}
	public void setArchivedRoom(Boolean archivedRoom) {
		this.archivedRoom = archivedRoom;
	}
	public Boolean getPrivateRoom() {
		return privateRoom;
	}
	public void setPrivateRoom(Boolean privateRoom) {
		this.privateRoom = privateRoom;
	}
	public String getXmppJId() {
		return xmppJId;
	}
	public void setXmppJId(String xmppJId) {
		this.xmppJId = xmppJId;
	}
	public String getGuestAccessUrl() {
		return guestAccessUrl;
	}
	public void setGuestAccessUrl(String guestAccessUrl) {
		this.guestAccessUrl = guestAccessUrl;
	}
	public List<Integer> getMemberIds() {
		return memberIds;
	}
	public void setMemberIds(List<Integer> memberIds) {
		this.memberIds = memberIds;
	}
	public List<User> getParticipants() {
		return participants;
	}
	public void setParticipants(List<User> participants) {
		this.participants = participants;
	}
	public Boolean getAllowGuests() {
		return allowGuests;
	}
	public void setAllowGuests(Boolean allowGuests) {
		this.allowGuests = allowGuests;
	}

}
