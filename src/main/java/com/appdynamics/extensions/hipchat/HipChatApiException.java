package com.appdynamics.extensions.hipchat;

/**
 * @author Satish Muddam
 */
public class HipChatApiException extends RuntimeException {

    public HipChatApiException (String errorCode, String errorType, String errorMsg) {
        super("HipChat API call returned error [" + String.format("error code=%s|type=%s|message=%s", errorCode, errorType, errorMsg) + "]");
    }

    public HipChatApiException(String message) {
        super(message);
    }

    public HipChatApiException(String message, Throwable ex) {
        super(message, ex);
    }
}
