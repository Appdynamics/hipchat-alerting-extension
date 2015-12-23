package com.appdynamics.extensions.hipchat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public abstract class HipChatApi {

    private static Logger logger = Logger.getLogger(HipChatApi.class);

    public static HipChatApi hipChatApiInstance = null;

    protected static HipChat hipchatConfigObj;
    protected Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public abstract void processApiCall();

    protected abstract void sendMessage(List<NameValuePair> nameValuePairs);

    protected abstract List<NameValuePair> getHipChatMessageObj();

    public static HipChatApi getHipChatApiInstance(HipChat hipChatConfig) {

        if (hipChatApiInstance != null)
            return hipChatApiInstance;

        hipchatConfigObj = hipChatConfig;
        HipChatApi hipchatApiInstance = new HipChatApiVersion2();
        return hipchatApiInstance;
    }


    /**
     * HipChat API call for the URI mentioned as path
     *
     * @param path           = HipChat REST URI
     * @param nameValuePairs = key-value params
     * @return HipChat API response
     */
    protected HipChatSuccessResponse getPOSTAPIResponse(String path, List<NameValuePair> nameValuePairs) {

        try {
            String queryStr = getQueryString();
            HttpClient client = HttpClientBuilder.create().build();

            HttpPost post = new HttpPost(getUrl(path, queryStr));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(post);

            HipChatSuccessResponse successResponse = getResponseAndthrowIfError(response);

            return successResponse;
        } catch (MalformedURLException ex) {
            logger.error("MalformedURLException occured : " + ex);
            throw new HipChatApiException("MalformedURLException occurred", ex);
        } catch (IOException ex) {
            logger.error("IOException occured : " + ex);
            throw new HipChatApiException("IOException occurred", ex);
        } catch (Exception ex) {
            logger.error("Exception occured : " + ex);
            throw new HipChatApiException("Exception occurred", ex);
        }
    }

    private HipChatSuccessResponse getResponseAndthrowIfError(HttpResponse response) throws IOException {

        int statusCode = response.getStatusLine().getStatusCode();

        HttpEntity entity = response.getEntity();

        String resp = null;

        if (entity != null) {
            resp = EntityUtils.toString(entity);
        }

        if (statusCode != 200 && statusCode != 204) {

            JsonParser jsonParser = new JsonParser();
            JsonElement errorEle = jsonParser.parse(resp).getAsJsonObject().get("error");

            if (errorEle != null) {
                String errorCode = errorEle.getAsJsonObject().get("code").getAsString();
                String errorType = errorEle.getAsJsonObject().get("type").getAsString();
                String errorMsg = errorEle.getAsJsonObject().get("message").getAsString();

                logger.error("HipChat API call returned error [" + String.format("error code=%s|type=%s|message=%s", errorCode, errorType, errorMsg) + "]");
                throw new HipChatApiException(errorCode, errorType, errorMsg);
            } else {
                logger.debug("HipChat API call returned invalid response:" + resp);
                throw new HipChatApiException("HipChat API call returned invalid response:" + resp);
            }
        } else {
            return new HipChatSuccessResponse(statusCode, resp);
        }

    }

    protected String getQueryString() {
        if (hipchatConfigObj.getAuthToken() == null && (hipchatConfigObj.getAuthToken().trim().length() < 0)) {
            throw new IllegalStateException("An authentication token is required for making calls to the HipChat server");
        }

        StringBuilder sb = new StringBuilder("auth_token=" + hipchatConfigObj.getAuthToken()).append("&format=json");
        return sb.toString();
    }

    private String getUrl(String path, String queryString) throws URISyntaxException {

        URI uri = new URI(hipchatConfigObj.getScheme(), null, hipchatConfigObj.getHost(), -1, "/" + hipchatConfigObj.getVersion() + "/" + path, queryString, null);
        return uri.toASCIIString();
    }
}
