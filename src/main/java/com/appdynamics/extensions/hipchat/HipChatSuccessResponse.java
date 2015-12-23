package com.appdynamics.extensions.hipchat;

/**
 * @author Satish Muddam
 */
public class HipChatSuccessResponse {

    private int statusCode;
    private String response;


    public HipChatSuccessResponse(int statusCode, String response) {
        this.statusCode = statusCode;
        this.response = response;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public String getResponse() {
        return response;
    }
}
