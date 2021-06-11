package com.esb.camel.csvtoxml.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {

	private static final Logger log = LogManager.getLogger(LogHelper.class);
	
	public static void logStacktrace(Throwable e) {
		try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
			e.printStackTrace(pw);
			String sStackTrace = sw.toString(); // stack trace as a string
			System.out.println(sStackTrace);
		} catch (Exception ex) {

		}
	}

}
