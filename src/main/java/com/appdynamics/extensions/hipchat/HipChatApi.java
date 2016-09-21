/**
 * Copyright 2016 AppDynamics, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appdynamics.extensions.hipchat;

import com.appdynamics.extensions.http.Http4ClientBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HipChatApi {

    private static Logger logger = Logger.getLogger(HipChatApi.class);

    public static HipChatApi hipChatApiInstance = null;

    protected static HipChat hipchatConfigObj;
    protected Message message;

    private CloseableHttpClient httpClient;
    private HttpClientContext httpContext;

    public HipChatApi() {
        setupHttpClient();
    }

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

    private void setupHttpClient() {

        Map map = createHttpConfigMap();


        //Workaround to ignore the certificate mismatch issue.
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unable to create SSL context", e);
            throw new RuntimeException("Unable to create SSL context", e);
        } catch (KeyManagementException e) {
            logger.error("Unable to create SSL context", e);
            throw new RuntimeException("Unable to create SSL context", e);
        } catch (KeyStoreException e) {
            logger.error("Unable to create SSL context", e);
            throw new RuntimeException("Unable to create SSL context", e);
        }
        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, (X509HostnameVerifier) hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        HttpClientBuilder builder = Http4ClientBuilder.getBuilder(map);
        builder.setConnectionManager(connMgr);


        httpContext = HttpClientContext.create();

        HttpClientBuilder httpClientBuilder = builder.setSSLSocketFactory(sslSocketFactory);

        httpClient = httpClientBuilder.build();
    }

    private Map<String, String> createHttpConfigMap() {
        Map map = new HashMap();

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        map.put("servers", list);
        HashMap<String, String> server = new HashMap<String, String>();
        String uri = hipchatConfigObj.getScheme() + "://" + hipchatConfigObj.getHost();

        server.put("uri", uri);

        list.add(server);

        return map;
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

            HttpPost post = new HttpPost(getUrl(path, queryStr));
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpClient.execute(post, httpContext);

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
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("Error while closing the HttpClient", e);
                }
            }
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
