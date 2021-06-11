package com.esb.camel.csvtoxml.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import org.apache.camel.json.simple.JsonObject;
//import org.apache.camel.json.simple.JsonArray;
//import org.apache.camel.json.simple.parser.JSONParser;
import com.esb.camel.csvtoxml.CamelConstants;

public class GetCountriesDetailsFromWeb {

	public GetCountriesDetailsFromWeb() {
	}
	
	
	private String getCountriesDetails() {
		HttpsURLConnection countriesUrlConnection = null;
		String result = null;
		try {
			StringBuilder sb = new StringBuilder();

			URL url = new URL(CamelConstants.REST_URL);
			countriesUrlConnection = (HttpsURLConnection) url.openConnection();

			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(countriesUrlConnection.getInputStream()))) {
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			}
			countriesUrlConnection.disconnect();

			result = sb.toString();
			
			JSONParser parser = new JSONParser();
			JSONArray jsonArray = (JSONArray) parser.parse(result);
			for(int x=0;x<jsonArray.size();x++) {
				JSONObject jObj = (JSONObject)jsonArray.get(x);
				System.out.print(jObj.get("name"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (null != countriesUrlConnection) {
				try {
					countriesUrlConnection.disconnect();
				} catch (Exception ex) {
				}
			}
		}
		


		return result;
	}
	
	
}
