package com.pahanaedu.util;

import java.io.BufferedReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Util {

    private static final ObjectMapper objectMapper = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    
    public static String readRequestBody(HttpServletRequest req)  {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim());
            }
        } catch (Exception e) {
        	return null;
		}
        return sb.toString();
    }
 
    public static <T> T parseJsonBody(HttpServletRequest req, Class<T> clazz) {
        String body = readRequestBody(req);

        if (body == null || body.isEmpty() || body.equals("{}")) {
        	return null;
        }

        try {
			return objectMapper.readValue(body, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
     

    public static boolean anyNullOrEmpty(Object... values) {
        for (Object val : values) {
            if (val == null) return true;

            if (val instanceof String && ((String) val).isBlank()) {
                return true;
            }
 
            if (val instanceof List) {
                List<?> list = (List<?>) val;
                if (list.isEmpty()) return true;
            }

            if (val.getClass().isArray()) { 
                if (java.lang.reflect.Array.getLength(val) == 0) return true;
            }
 
        }
        return false;
    }

}
