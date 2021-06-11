package com.esb.camel.csvtoxml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.esb.camel.csvtoxml.jaxb.Cities;
import com.esb.camel.csvtoxml.jaxb.City;
import com.esb.camel.csvtoxml.util.LogHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TransformCsvToXmlProcessor implements Processor {

	private static final Logger log = LogManager.getLogger(TransformCsvToXmlProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		try {
			String csvFileEntries = exchange.getIn().getBody(String.class);
			String exchangeFileName = exchange.FILE_NAME ;
			System.out.println("stated processing file "+exchangeFileName);			
			log.info("process input CSV=\n" + csvFileEntries);
			
			String restUrl = (String) exchange.getProperty("REST_URL");
			String outputDir = (String) exchange.getProperty("OUTPUT_DIR");
			
			
			log.info("STEP 1: load the country , city ,langitude and latitude from " + restUrl);
			HashMap<String, String> cityLangLatMap = getCityLanLat(restUrl);
			String currentTime = getCurrentTime();
			outputDir = outputDir+"\\"+currentTime;
			File file = new File(outputDir);
		    boolean bool = file.mkdir();

			log.info("cityLangLatMap = " + cityLangLatMap);
			log.info("STEP 2: Start processing csv file " + exchangeFileName + " entries.");

			log.info("Started processing File " + exchange.FILE_NAME);
			
			String[] lineSeparator = csvFileEntries.split(System.getProperty("line.separator"));
			String country = "";
			String cityName = "";
			String population = "";

			log.info("STEP 3: Prepare the XML files entries from JAXB classes and above steps results.");
			HashMap<String, ArrayList<City>> countryCityMap = new HashMap<String, ArrayList<City>>();
			int noOfLines = 0;
			for (String lineData : lineSeparator) {
				noOfLines++;
				String[] ccpArray = lineData.split(",");
				if (ccpArray.length > 2) {
					country = ccpArray[0];
					cityName = ccpArray[1];
					population = ccpArray[2];

					City city = new City();
					city.setName(cityName);
					city.setPopulation(population);
					if (cityLangLatMap.containsKey(cityName.trim().toLowerCase())) {
						String cityLangLat = cityLangLatMap.get(cityName.trim().toLowerCase());
						String[] langLatArray = cityLangLat.split("~");
						city.setLongitude(langLatArray[0]);
						city.setLatitude(langLatArray[1]);
					}

					if (countryCityMap.containsKey(country)) {
						countryCityMap.get(country).add(city);
					} else {
						ArrayList<City> cityList = new ArrayList<City>();
						cityList.add(city);
						countryCityMap.put(country, cityList);
					}

				} else {
					log.info("invlaid csv entry  " + lineData);
				}
			}

			log.info("STEP 4: Create xml file into " + outputDir + " directory.");
			
			final String outputDirFinal = outputDir ;
			
			countryCityMap.forEach((k, v) -> {

				String fileName = k;
				Cities cities = new Cities();
				ArrayList<City> citiesList = v;
				cities.setCityList(citiesList);
				String xml= "";
				try {
					xml = getXmlFromJaxb(cities);
				} catch (Exception e) {
					e.printStackTrace();
				}
				xml = xml.substring(xml.indexOf(">") + 1);// remove the xml header line
				fileName = fileName +".xml";
				String filenameWithPath =  outputDirFinal +"\\"+ fileName ;
				
				writeXmlFile(filenameWithPath, xml);
				log.info(fileName + " created Successfully with \n " + xml);
			});
			log.info("TransformCsvToXmlProcessor process completed with " + noOfLines + " lines processed successfully");
			exchange.getOut().setBody("TransformCsvToXmlProcessor process completed with " + noOfLines + " lines processed successfully");
			log.info("TransformCsvToXmlProcessor CSV File "+exchangeFileName+" processing completed !!! ");
		} catch (Exception ex) {
			log.error("error in TransformCsvToXmlProcessor process  Method ");
			LogHelper.logStacktrace(ex);
		}
	}

	/**
	 * writeXmlFile
	 * @param filePath
	 * @param filename
	 * @param fileBody
	 */
	private void writeXmlFile(final String filenameWithPath, String fileBody) {
		try (FileWriter myWriter = new FileWriter(filenameWithPath)) {
			myWriter.write(fileBody);

		} catch (Exception ex) {
			LogHelper.logStacktrace(ex);
			log.error("error writeXmlFile "+filenameWithPath+ " " + ex.getMessage());
		}
	}

	/**
	 * getCityLanLat
	 * @param restUrl
	 * @return
	 */
	private HashMap<String, String> getCityLanLat(String restUrl) {

		HashMap<String, String> cityLangLatMap = new HashMap<>();
		HttpsURLConnection countriesUrlConnection = null;
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL(restUrl);// "https://restcountries.eu/rest/v2/all"
			countriesUrlConnection = (HttpsURLConnection) url.openConnection();

			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(countriesUrlConnection.getInputStream()))) {
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			}
			countriesUrlConnection.disconnect();

			log.info("parse the result and get langitude and Latitude "+sb.toString());
			JSONParser parser = new JSONParser();
			JSONArray jsonArray = (JSONArray) parser.parse(sb.toString());
			String country = "";
			String cityName = "";
			long population = 0L;
			Double lang = null;
			Double lat = null;
			for (int x = 0; x < jsonArray.size(); x++) {
				JSONObject jObj = (JSONObject) jsonArray.get(x);

				country =  (String) jObj.get("name");
				cityName = (String) jObj.get("capital");
				population = (long) jObj.get("population");
				log.info(jObj.get("name") + "," + jObj.get("capital") + ","
						+ (population % 1000000) + " min");
				
				JSONArray lanLatArray = (JSONArray) jObj.get("latlng");
				
				if (null != lanLatArray && lanLatArray.size() > 1) {
					lang = (Double) lanLatArray.get(0);
					lat = (Double) lanLatArray.get(1);
					cityLangLatMap.put(cityName.trim().toLowerCase(), lang + "~" + lat);
				}
			}
		} catch (Exception ex) {
			LogHelper.logStacktrace(ex);
		} finally {
			if (null != countriesUrlConnection) {
				try {
					countriesUrlConnection.disconnect();
				} catch (Exception ex) {
				}
			}
		}

		return cityLangLatMap;
	}

	/**
	 * getXmlFromJaxb
	 * @param cities
	 * @return
	 * @throws Exception
	 */
	public String getXmlFromJaxb(Cities cities) throws Exception {
		String xmlString = "";
		try {
			JAXBContext context = JAXBContext.newInstance(Cities.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			StringWriter sw = new StringWriter();
			marshaller.marshal(cities, sw);
			xmlString = sw.toString();

		} catch (Exception e) {			
			LogHelper.logStacktrace(e);
		}

		return xmlString;
	}
	
	/**
	 * getCurrentTime
	 * @return
	 */
	private String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-YYYY_HH24_mm_ss");
		String strDate= formatter.format(date);
		return strDate;
	}
}