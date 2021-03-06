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

/**
 * @author Satish Muddam
 */
public class HipChatApiException extends RuntimeException {

    public HipChatApiException(String errorCode, String errorType, String errorMsg) {
        super("HipChat API call returned error [" + String.format("error code=%s|type=%s|message=%s", errorCode, errorType, errorMsg) + "]");
    }

    public HipChatApiException(String message) {
        super(message);
    }

    public HipChatApiException(String message, Throwable ex) {
        super(message, ex);
    }
}
