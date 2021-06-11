package com.esb.camel.csvtoxml.util;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import com.esb.camel.csvtoxml.jaxb.Cities;
import com.esb.camel.csvtoxml.jaxb.City;

public class JaxbTest {

	public static void main(String[] args) {
		
		Cities cities = new Cities();
		ArrayList<City> al = new ArrayList();
		City city1 = new City();
		city1.setName("Berlin");
		city1.setPopulation("3.768m");
		city1.setLongitude("23.8");
		city1.setLatitude("34.0");
		al.add(city1);
		
		City city2 = new City();
		city2.setName("Hamburg");
		city2.setPopulation("2.768m");
		al.add(city2);
		
		City city3 = new City();
		city3.setName("Munich");
		city3.setPopulation("4.768m");
		al.add(city3);
		
		City city4 = new City();
		city4.setName("Bern");
		city4.setPopulation("3.768m");
		al.add(city4);
		
		cities.setCityList(al);
		
		
        try {
        	JAXBContext context = JAXBContext.newInstance(Cities.class);
            Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			// Write to System.out
	        m.marshal(cities, System.out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
