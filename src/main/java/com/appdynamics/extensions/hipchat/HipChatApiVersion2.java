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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class HipChatApiVersion2 extends HipChatApi {

    private static Logger logger = Logger.getLogger(HipChatApiVersion2.class);

    @Override
    public void processApiCall() {
        logger.info("HipChat v2 API call");

        this.sendMessage(this.getHipChatMessageObj());

        logger.info("Successfully published message to HipChat");
    }

    public List<NameValuePair> getHipChatMessageObj() {
        if (logger.isDebugEnabled())
            logger.info("Sending the message with object : " + this.getMessage() + " to " + hipchatConfigObj.getHost());

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
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