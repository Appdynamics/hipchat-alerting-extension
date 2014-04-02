package com.appdynamics.extensions.hipchat;

/**
 * @author ashish mehta
 *
 */
public class OtherEvent extends Event{

	private String eventName;
	private String eventID;
	
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getEventID() {
		return eventID;
	}
	public void setEventID(String eventID) {
		this.eventID = eventID;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Event = [").append("P:").append(super.getPriority()).append(", ");
		sb.append("Severity:").append(super.getSeverity()).append(", ");
		sb.append("App Name:").append(super.getAppName()).append(", ");
		sb.append("Event Name:").append(this.eventName).append(", ");
		sb.append("URL:").append(this.getDeepLinkUrl()).append(this.getEventID()).append("]");

		return sb.toString();
	}

}
