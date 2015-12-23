package com.appdynamics.extensions.hipchat;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class HipChatApiVersion2 extends HipChatApi{

	private static Logger logger = Logger.getLogger(HipChatApiVersion2.class);

	@Override
	public void processApiCall() {
		logger.info("HipChat v2 API call");

		this.sendMessage(this.getHipChatMessageObj());

		logger.info("Successfully published message to HipChat");
	}

	public List<NameValuePair> getHipChatMessageObj() {
		if(logger.isDebugEnabled())
			logger.info("Sending the message with object : " + this.getMessage() + " to " + hipchatConfigObj.getHost());

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("auth_token", hipchatConfigObj.getAuthToken()));
		nameValuePairs.add(new BasicNameValuePair("message", this.getMessage().getMessage()));
		nameValuePairs.add(new BasicNameValuePair("message_format", this.getMessage().getMessageFormat()));
		nameValuePairs.add(new BasicNameValuePair("notify", (Boolean.toString(this.getMessage().isNotify()))));
		nameValuePairs.add(new BasicNameValuePair("color", this.getMessage().getColor()));

		return nameValuePairs;
	}
	
	@Override
	protected void sendMessage(List<NameValuePair> nameValuePairs) {
		
		String apiCall = "room/" + this.getMessage().getRoomName() + "/notification";
		this.getPOSTAPIResponse(apiCall, nameValuePairs);
	}
}