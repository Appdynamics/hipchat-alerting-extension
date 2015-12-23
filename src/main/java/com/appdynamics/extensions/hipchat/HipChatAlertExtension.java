package com.appdynamics.extensions.hipchat;


import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.alerts.customevents.EventBuilder;
import com.appdynamics.extensions.alerts.customevents.HealthRuleViolationEvent;
import com.appdynamics.extensions.alerts.customevents.OtherEvent;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author ashish mehta
 */
public class HipChatAlertExtension {

    private static Logger logger = Logger.getLogger(HipChatAlertExtension.class);

    public enum MessageFormat {text, html}

    private static HipChat hipchat;
    private static Message message;
    private static Event event;

    public static void main(String[] args) {

        String details = HipChatAlertExtension.class.getPackage().getImplementationTitle();
        String msg = "Using Extension Version [" + details + "]";
        logger.info(msg);

        if (args == null || args.length == 0) {
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
    private static void processEvent(String[] args) {
        try {

            boolean propReadSuccess = readConfigProperties();
            if (propReadSuccess) {
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

                HipChatApi hipChatApi = HipChatApi.getHipChatApiInstance(hipchat);
                hipChatApi.setMessage(message);
                hipChatApi.processApiCall();

            }
        } catch (Exception exp) {
            logger.error("Exception in main()", exp);
        }
    }

    /**
     * Reads config.properties file to get the required key-value pairs
     */
    private static boolean readConfigProperties() {
        Properties prop = new Properties();
        InputStream input = null;

        try {
            input = new FileInputStream("config.properties");
            prop.load(input);

            String authToken = prop.getProperty("authToken");
            if (authToken == null || (authToken.trim().length() < 1)) {
                logger.error("An authentication token is required for making calls to the HipChat server. Aborting Program.");
                return false;
            }

            String roomName = prop.getProperty("roomName");
            if (roomName == null || (roomName.trim().length() == 0)) {
                logger.error("Room name cannot be null or empty. Aborting Program.");
                return false;
            }

            String scheme = prop.getProperty("scheme");
            if (scheme == null || (scheme.trim().length() == 0))
                scheme = "https";

            String color = prop.getProperty("color");
            if (color == null || (color.trim().length() == 0))
                color = "red";

            String notify = prop.getProperty("notify");
            if (notify == null || (notify.trim().length() == 0))
                notify = "true";

            String host = prop.getProperty("host");

            hipchat = new HipChat();
            hipchat.setAuthToken(authToken);
            hipchat.setScheme(scheme);
            if (host != null && !host.isEmpty()) {
                hipchat.setHost(host);
            }

            Message msg = new Message();
            msg.setRoomName(roomName);
            msg.setMessageFormat(MessageFormat.text.toString());
            msg.setColor(color);
            msg.setNotify(Boolean.parseBoolean(notify));

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
        StringBuilder sb = new StringBuilder();
        if ((event instanceof HealthRuleViolationEvent)) {
            HealthRuleViolationEvent healthRuleViolationEvent = (HealthRuleViolationEvent) event;
            sb.append("Health rule violation = [").append("P:").append(event.getPriority()).append(", ");
            sb.append("Severity:").append(event.getSeverity()).append(", ");
            sb.append("App Name:").append(event.getAppName()).append(", ");
            sb.append("Health rule name:").append(healthRuleViolationEvent.getHealthRuleName()).append(", ");
            sb.append("Affected Entity Type:").append(healthRuleViolationEvent.getAffectedEntityType()).append(", ");
            sb.append("Affected Entity Name:").append(healthRuleViolationEvent.getAffectedEntityName()).append(", ");
            sb.append("Summary message:").append(healthRuleViolationEvent.getSummaryMessage()).append(", ");
            sb.append("URL:").append(event.getDeepLinkUrl()).append(healthRuleViolationEvent.getIncidentID()).append("]");
        } else {
            OtherEvent oe = (OtherEvent) event;
            sb.append("Event = [").append("P:").append(oe.getPriority()).append(", ");
            sb.append("Severity:").append(oe.getSeverity()).append(", ");
            sb.append("App Name:").append(oe.getAppName()).append(", ");
            sb.append("Event Name:").append(oe.getEventNotificationName()).append(", ");
            sb.append("URL:").append(oe.getDeepLinkUrl()).append(oe.getEventNotificationId()).append("]");
        }
        return sb.toString();
    }


    /**
     * Parses argument to create event object
     *
     * @param args
     */
    private static void parseEventParams(String[] args) throws Exception {
        event = new EventBuilder().build(args);
    }


}
