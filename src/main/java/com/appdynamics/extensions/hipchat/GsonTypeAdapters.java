package com.appdynamics.extensions.hipchat;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonTypeAdapters {
        
    public static final JsonDeserializer<Date> dateFromUnixTimestamp = new JsonDeserializer<Date>() {

        public Date deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            Long timestamp = je.getAsLong();
            if(timestamp > 0) {
                Date result = new Date(timestamp * 1000);
                return result;
            } else {
                return null;
            }
        }
        
    };
    
}
