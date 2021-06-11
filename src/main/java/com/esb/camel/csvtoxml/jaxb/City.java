package com.esb.camel.csvtoxml.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "city")
@XmlType(propOrder = { "population", "longitude", "latitude" })
public class City {

	
	private String name ;
	
	private String population ;
	
	private String longitude;
	
	private String latitude;
	
	@XmlAttribute(name = "name",required = true)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name = "population",required = true)
	public String getPopulation() {
		return population;
	}
	public void setPopulation(String population) {
		this.population = population;
	}
	@XmlElement(name = "longitude", required = false )
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	@XmlElement(name = "latitude" , required = false )
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
}
