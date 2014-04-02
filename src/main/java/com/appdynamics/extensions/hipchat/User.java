package com.appdynamics.extensions.hipchat;

import java.util.Date;
import java.util.TimeZone;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")          private int id;
    @SerializedName("name")             private String name;
    @SerializedName("mention_name")     private String mentionName;
    @SerializedName("last_active")      private Date lastActive;
    @SerializedName("created")          private Date created;
    @SerializedName("email")            private String emailAddress;
    @SerializedName("title")            private String title;
    @SerializedName("photo_url")        private String photoUrl;
    @SerializedName("status")           private String status;
    @SerializedName("status_message")   private String statusMessage;
    @SerializedName("is_group_admin")   private boolean admin;
    @SerializedName("is_deleted")       private boolean deleted;
    @SerializedName("password")         private String password;
	private TimeZone timezone;
	
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
	public String getMentionName() {
		return mentionName;
	}
	public void setMentionName(String mentionName) {
		this.mentionName = mentionName;
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
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public TimeZone getTimezone() {
		return timezone;
	}
	public void setTimezone(TimeZone timezone) {
		this.timezone = timezone;
	}

}
