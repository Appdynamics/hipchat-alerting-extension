package com.appdynamics.extensions.hipchat;

/**
 * @author ashish mehta
 *
 */
public class Message {
	
	private String roomName;
	private Integer roomId = -1;
	private String from;
	private String message;
	private String messageFormat;
	private boolean notify;
	private String color;
	
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public Integer getRoomId() {
		return roomId;
	}
	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessageFormat() {
		return messageFormat;
	}
	public void setMessageFormat(String messageFormat) {
		this.messageFormat = messageFormat;
	}
	public boolean isNotify() {
		return notify;
	}
	public void setNotify(boolean notify) {
		this.notify = notify;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Message=[").append("roomName:").append(this.roomName).append("&");
		sb.append("Room Id:").append(this.roomId).append("&");
		sb.append("From:").append(this.from).append("&");
		sb.append("MessageFormat:").append(this.messageFormat).append("&");
		sb.append("Color:").append(this.color).append("&");
		sb.append("Notify:").append(this.notify).append("]");
		
		return sb.toString();
	}
	
}
