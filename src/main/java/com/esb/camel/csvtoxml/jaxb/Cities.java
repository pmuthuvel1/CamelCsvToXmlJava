package com.esb.camel.csvtoxml.jaxb;

import java.util.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cities")
public class Cities {

	
	private List<City> cityList;

	@XmlElement(name = "city", required = true)
	public List<City> getCityList() {
		return cityList;
	}

	public void setCityList(List<City> cityList) {
		this.cityList = cityList;
	}

}
