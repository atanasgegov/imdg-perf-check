package com.akg.imdgperfcheck.util;

import java.util.List;

import com.akg.imdgperfcheck.dto.WineDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonParser {

	private static final String JSON_INDEX = "{ \"index\": {} }";
	
	public static String convertToESJson( List<WineDTO> winesRecords ) {
		
		StringBuilder json = new StringBuilder();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		winesRecords.stream().forEach( c -> {
			try {
				json.append( JSON_INDEX );
				json.append( System.lineSeparator() );
				json.append( ow.writeValueAsString(c).replaceAll( System.lineSeparator(), "") );
				json.append( System.lineSeparator() );
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		});
		
		return json.toString();
	}
	
	public static String getValueFromJsonDocument( String json, String jsonPointerExpression ) {
		ObjectReader reader = new ObjectMapper().reader();
		JsonNode root;
		try {
			root = reader.readTree(json);
			return root.at(jsonPointerExpression).asText();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
}
