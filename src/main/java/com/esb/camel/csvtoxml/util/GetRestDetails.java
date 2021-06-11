package com.esb.camel.csvtoxml.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GetRestDetails {

	public static void main(String[] args) {

		HttpsURLConnection countriesUrlConnection = null;
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL("https://restcountries.eu/rest/v2/all");
			countriesUrlConnection = (HttpsURLConnection) url.openConnection();

			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(countriesUrlConnection.getInputStream()))) {
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			}
			countriesUrlConnection.disconnect();

			//System.out.println(sb.toString());
			
			JSONParser parser = new JSONParser();
			JSONArray jsonArray = (JSONArray) parser.parse(sb.toString());
			for(int x=0;x<jsonArray.size();x++) {
				JSONObject jObj = (JSONObject)jsonArray.get(x);
				JSONArray lanLatArray = (JSONArray) jObj.get("latlng");
				Double lang = null;
				Double lat = null;
				if(null!=lanLatArray && lanLatArray.size()>1) {
				 lang = (Double)lanLatArray.get(0);
				 lat = (Double)lanLatArray.get(1);
				}
				//System.out.println(jObj.get("name")+","+jObj.get("capital")+","+jObj.get("population")+","+jObj.get("latlng"));
				//System.out.println(jObj.get("name")+","+jObj.get("capital")+","+ (Long.parseLong(jObj.get("population").toString())%1000000)+" min"+",lang="+lang+",lat="+lat);
				System.out.println(jObj.get("name")+","+jObj.get("capital")+","+jObj.get("population"));
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
	}
}
