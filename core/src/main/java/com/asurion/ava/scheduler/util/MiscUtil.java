package com.asurion.ava.scheduler.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiscUtil {
	
	public static String getResourceFile(String filePath) throws IOException {

        InputStream in = MiscUtil.class.getResourceAsStream(filePath);
        return IOUtils.toString(in, StandardCharsets.UTF_8.name());


    }

	public static String toJson(Object value) {
		try {
		    ObjectMapper om = new ObjectMapper();
		    om.setSerializationInclusion(Include.NON_NULL);
		    return om.writeValueAsString(value);
		} catch(JsonProcessingException e) {
		    throw new RuntimeException(
		            String.format("fail to convert %s to json", value.getClass().getName()), e);
		}
	}
	
	
    public static <T> T toPojo (String jsonInString, Class<?> clazz)  {
        if(jsonInString==null || jsonInString.isEmpty()) {
            // put empty json so it doesn't break
            jsonInString = "{}";
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode json;
    
            json = mapper.readTree(jsonInString);
            if(json!=null) {
                return toPojo(json.toString(), clazz, mapper);
            } else {
                throw new NullPointerException(String.format("Fail to read json %s, JsonNode is NULL", jsonInString));
            }
        } catch(Exception  e) {
            throw new RuntimeException(String.format("fail to build pojo %s from json %s", clazz, jsonInString));
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> T toPojo (String jsonInString, Class<?> clazz, ObjectMapper mapper) 
            throws JsonParseException, JsonMappingException, IOException  {

        T obj = null;

        obj = (T) mapper.readValue(jsonInString, clazz);

        return obj;
    }
    
    public static String getTimeInString() {
        return getTimeInStringUTC();
        //return getTimeInString(0);
     
    }
    
    public static String getTimeInString(long delayInSecond) {
        
        return getTimeInStringUTC(delayInSecond);
        
        /*
        long ts = System.currentTimeMillis();
        ts += (delayInSecond * 1_000);
        SimpleDateFormat time_formatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
        
        return time_formatter.format(ts);
        */
        
    }
    
    public static String getTimeInStringUTC() {
        return getTimeInString(0);
    }
    
    public static String getTimeInStringUTC(long secondsToAdd) {
        Instant ts = Instant.now();
        ts = ts.plusSeconds(secondsToAdd);
        return ts.toString();
    }
    
    public static List<InputStream> loadResources(
            final String name, final ClassLoader classLoader) throws IOException {
        final List<InputStream> list = new ArrayList<InputStream>();
        final Enumeration<URL> systemResources = 
                (classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader)
                .getResources(name);
        while (systemResources.hasMoreElements()) {
            
            System.out.println(systemResources.nextElement().toString());
            
            //list.add(systemResources.nextElement().openStream());
        }
        return list;
    }
    
    public static void main(String[] args) throws Exception{
        loadResources("task-scheduler-config-opensource.json", null);
    }
    
}
