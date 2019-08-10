package com.guan;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class JacksonUtils {
	@SuppressWarnings("deprecation")
	public static String writeValueAsString(Object o) {
		ObjectMapper mapper = new ObjectMapper();
		String result = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		mapper.getSerializationConfig().setDateFormat(formatter);
		try {
			result = mapper.writeValueAsString(o);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	public static String writeValueAsStringWithDateFormat(Object o,
			DateFormat formatter) {
		ObjectMapper mapper = new ObjectMapper();
		String result = "";
		mapper.getSerializationConfig().setDateFormat(formatter);
		try {
			result = mapper.writeValueAsString(o);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static <T> T readValue(String json, Class<T> valueType)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return (T) mapper.readValue(json, valueType);
	}
}
