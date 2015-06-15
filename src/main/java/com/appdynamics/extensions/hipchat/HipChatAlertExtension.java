package com.appdynamics.extensions.hipchat;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * @author ashish mehta
 *
 */
public class HipChatAlertExtension {

	private static Logger logger = Logger.getLogger(HipChatAlertExtension.class);

	public static HipChatAlertExtension instance = null;

	public static enum Method { GET, POST };
	public static enum Action { Create, Delete, History, Message, Topic, Show };
	public static enum MessageFormat { text, html };
	public static enum MessageColor { yellow, red, green, purple, gray, random };
	private static HipChat hipchat;
	private static Message message;
	private static Event event;

	public static void main( String[] args) {
		String details= HipChatAlertExtension.class.getPackage().getImplementationTitle();
		String msg = "Using Extension Version ["+details+"]";
		logger.info(msg);

		if(args == null || args.length == 0){
			logger.error("No arguments passed to the extension, exiting the program.");
			return;
		}
		processEvent(args);
	}

	/**
	 * Processes the event provided by the controller to generate alert
	 * Involves:
	 * Parsing the event
	 * Computing the alerting message
	 * Sending the alert message to hipchat room
	 * 
	 * @param args
	 */
	private static void processEvent(String [] args){
		try{

			boolean propReadSuccess = readConfigProperties();
			if(propReadSuccess){
				String alertMsg = "";
				try {
					parseEventParams(args);
					alertMsg = createAlertMessage();
					logger.debug("Computed alerting Message as = " + alertMsg);
				} catch (Exception exp) {
					logger.error("Failed to parse arguments, exiting the program, ex: " + exp);
					return;
				}

				HipChatAlertExtension.message.setMessage(alertMsg);

				HipChatAlertExtension hipchatA = new HipChatAlertExtension(); 
				instance = hipchatA;

				boolean roomIdSet = false;
				if(HipChatAlertExtension.message.getRoomId() == null || HipChatAlertExtension.message.getRoomId() == -1){
					Room room = instance.getRoomDetailsForGivenName(HipChatAlertExtension.message.getRoomName());
					if(room != null){
						HipChatAlertExtension.message.setRoomId(room.getId());
						roomIdSet = true;
					}
				}else{
					roomIdSet = true;
				}

				boolean success = false;
				if(roomIdSet){
					String resp = instance.sendMessage(HipChatAlertExtension.message);
					if(resp != null){				
						success = true;
					}
					if(!success){
						logger.error("HipChat API Call failed, could not send the alert");
					}

				}else{
					logger.error("HipChat API failed, reason = Room id could not be found for the given room name : " + HipChatAlertExtension.message.getRoomName());
					return;
				}
			}
		}catch(Exception exp){
			logger.error("Exception in main()", exp);
		}
	}

	/**
	 * Reads config.properties file to get the required key-value pairs
	 */
	private static boolean readConfigProperties(){
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream("config.properties");
			prop.load(input);

			String authToken = prop.getProperty("authToken");
			if(authToken == null || (authToken.trim().length() < 1)) {
				logger.error("An authentication token is required for making calls to the HipChat server. Aborting Program.");
				return false;
			}

			String fromName = prop.getProperty("fromName");			
			if(fromName != null) {
				if(fromName.trim().length() > 15){
					logger.debug(fromName + " more than 15 characters, so triming it to first 15 chars");
					fromName = fromName.substring(0, 15);
				}
			}

			String roomName = prop.getProperty("roomName");
			if(fromName == null || (fromName.trim().length() == 0)) {
				logger.error("Room name cannot be null or empty. Aborting Program.");
				return false;
			}

			String scheme = prop.getProperty("scheme");
			if(scheme == null || (scheme.trim().length() == 0))
				scheme = "https";
			
			String color = prop.getProperty("color");
			if(color == null || (color.trim().length() == 0))
				color = "red";
			
			String notify = prop.getProperty("notify");
			if(notify == null || (notify.trim().length() == 0))
				notify = "true";

			String host = prop.getProperty("host");
			
			hipchat = new HipChat();
			hipchat.setAuthToken(authToken);
			hipchat.setScheme(scheme);
			if(host != null && ! host.isEmpty()) {
				hipchat.setHost(host);
			}

			Message msg = new Message();
			msg.setRoomName(roomName);
			msg.setFrom(fromName);
			msg.setMessageFormat(MessageFormat.text.toString());
			msg.setColor(color);
			msg.setNotify(Boolean.parseBoolean(notify));

			String roomId = prop.getProperty("roomId");
			if(roomId != null && (roomId.trim().length() > 0))
				msg.setRoomId(Integer.parseInt(roomId));

			message = msg;
			logger.info("Message object created = " + msg);
			return true;

		} catch (Exception exp) {
			logger.error("Exception occured while reading the properties from config.properties", exp);
			return false;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception exp) {
					logger.error("Exception occured while closing the input stream for config.properties", exp);
					return false;
				}
			}
		}
	}

	/**
	 * Creates the alerting message string from the event object
	 */
	private static String createAlertMessage() {
		String status = "";

		if ((event instanceof HealthRuleViolationEvent)) {
			HealthRuleViolationEvent hrve = (HealthRuleViolationEvent)event;
			status = hrve.toString();
		} else {
			OtherEvent oe = (OtherEvent)event;
			status = oe.toString();
		}
		return status;
	}


	/**
	 * Parses argument to create event object
	 * 
	 * @param args
	 */
	private static void parseEventParams(String[] args) throws Exception {
		args = stripQuotes(args);

		if (args[(args.length - 1)].startsWith("http")) {
			OtherEvent oe = new OtherEvent();
			oe.setAppName(args[0]);
			oe.setPriority(args[3]);
			oe.setSeverity(args[4]);
			oe.setTag(args[5]);
			oe.setEventName(args[6]);
			oe.setEventID(args[7]);
			oe.setDeepLinkUrl(args[(args.length - 1)]);
			event = oe;
		} else {
			HealthRuleViolationEvent hrv = new HealthRuleViolationEvent();
			hrv.setAppName(args[0]);
			hrv.setPriority(args[3]);
			hrv.setSeverity(args[4]);
			hrv.setTag(args[5]);
			hrv.setHealthRuleName(args[6]);
			hrv.setHealthRuleID(args[7]);
			hrv.setSummaryMessage(args[(args.length - 4)]);
			hrv.setIncidentID(args[(args.length - 3)]);
			hrv.setDeepLinkUrl(args[(args.length - 2)]);
			hrv.setEventType(args[(args.length - 1)]);
			event = hrv;
		}
	}

	/**
	 * HipChat API call for the URI mentioned as path
	 *
	 * @param path = HipChat REST URI
	 * @param nameValuePairs = key-value params
	 * @param method = GET or POST
	 * 
	 * @return HipChat API response
	 */
	private String getAPIResponse(String path, List<NameValuePair> nameValuePairs, Method method){

		try {
			String queryStr = getQueryPath(path);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = null;

			if(method.equals(Method.GET) ){
				HttpGet httpGet = 	new HttpGet(getUrl(queryStr));
				response = client.execute(httpGet);
			}else{
				HttpPost post = new HttpPost(getUrl(queryStr));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				response = client.execute(post);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String line = "";
			String resp = null;
			while ((line = br.readLine()) != null) {
				resp = line;
				break;
			}

			if(resp != null && resp.trim().length() > 0){
				JsonParser jsonParser = new JsonParser();
				JsonElement errorEle = jsonParser.parse(resp).getAsJsonObject().get("error");

				if(errorEle != null){
					String errorCode = errorEle.getAsJsonObject().get("code").getAsString();
					String errorType = errorEle.getAsJsonObject().get("type").getAsString();
					String errorMsg = errorEle.getAsJsonObject().get("message").getAsString();

					logger.error("HipChat API call returned error [" + String.format("error code=%s|type=%s|message=%s", errorCode, errorType, errorMsg) + "]");
					return null;
				}else{
					logger.debug("HipChat API call returned response:" + resp);
				}
			}
			return resp;
		} catch (MalformedURLException ex) {
			logger.error("MalformedURLException occured : " + ex);
			return null;
		} catch (IOException ex) {
			logger.error("IOException occured : " + ex);
			return null;
		} catch (Exception ex) {
			logger.error("Exception occured : " + ex);
			return null;
		}  
	}

	public String sendMessage(Message msg) {
		logger.debug("Sending the message with object : " + msg+ " to "+hipchat.getHost());

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("auth_token", hipchat.getAuthToken()));
		nameValuePairs.add(new BasicNameValuePair("format", "json"));
		nameValuePairs.add(new BasicNameValuePair("room_id", String.valueOf(msg.getRoomId())));
		nameValuePairs.add(new BasicNameValuePair("from", msg.getFrom()));
		nameValuePairs.add(new BasicNameValuePair("message", msg.getMessage()));
		nameValuePairs.add(new BasicNameValuePair("message_format", msg.getMessageFormat()));
		nameValuePairs.add(new BasicNameValuePair("notify", (msg.isNotify() ? "1" : "0")));
		nameValuePairs.add(new BasicNameValuePair("color", msg.getColor()));

		return instance.getAPIResponse("rooms/message", nameValuePairs, Method.POST);
	}

	/**
	 * HipChat API call to get the rooms list
	 * Involves:
	 * Calling rooms/list HipChat Rest API call
	 * Parsing the JSON response using gson to form List<Room> Object
	 * 
	 * @return List<Room> object
	 */
	public List<Room> getRoomsList(){

		List<Room> rooms = new ArrayList<Room>();
		
		String response = getAPIResponse("rooms/list", null, Method.GET);		
		logger.debug("Room List = " + response);

		if(response != null && (response.trim().length() > 0)){
			Gson gson = new GsonBuilder()
			.registerTypeAdapter(Date.class, GsonTypeAdapters.dateFromUnixTimestamp )
			.create();

			JsonParser jsonParser = new JsonParser();
			JsonArray json = jsonParser.parse(response).getAsJsonObject().get("rooms").getAsJsonArray();
			for(JsonElement ele : json){
				Room room = gson.fromJson(ele, Room.class);
				rooms.add(room);
			}
		}
		return rooms;
	}


	/**
	 * Get the Room object for the given room name
	 * 
	 * @return Room object
	 */
	private Room getRoomDetailsForGivenName(String roomName){
		logger.debug("Getting Room id for the room : " + roomName);

		List<Room> rooms = instance.getRoomsList();
		for(Room room : rooms){
			if(room.getName().equalsIgnoreCase(roomName))
				return room;
		}

		logger.error("No HipChat room found for the room name :  " + roomName);
		return null;
	}

	private String getQueryPath(String path) {
		if(hipchat.getAuthToken() == null && (hipchat.getAuthToken().trim().length() < 0)) {
			throw new IllegalStateException("An authentication token is required for making calls to the HipChat server");
		}

		if(!path.toLowerCase().contains("auth_token")) {
			if(path.contains("?")) {
				path += "&auth_token=" + hipchat.getAuthToken();
			} else {
				path += "?auth_token=" + hipchat.getAuthToken();
			}
		}

		if(!path.toLowerCase().contains("format")) {
			if(path.contains("?")) {
				path += "&format=json";
			} else {
				path += "?format=json";
			}
		}

		return path;
	}

	private static String[] stripQuotes(String[] args) {
		StringBuilder sb = new StringBuilder();
		String[] stripped = new String[args.length];
		
		for (int i = 0; i < args.length; i++) {
			sb.append("args[" + i + "]=" + args[i] + ", ");
			stripped[i] = args[i].replaceAll("^\"|\"$", "");
		}
		
		logger.debug(sb.toString());
		return stripped;
	}

	private String getUrl(String path){
		return String.format("%s/%s", getServerAddress(), path);
	}

	private String getServerAddress() {
		return String.format("%s://%s/%s", hipchat.getScheme(), hipchat.getHost(), hipchat.getVersion());
	}

}
