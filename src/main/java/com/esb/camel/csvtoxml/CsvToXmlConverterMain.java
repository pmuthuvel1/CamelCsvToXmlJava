package com.esb.camel.csvtoxml;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esb.camel.csvtoxml.util.LogHelper;


public class CsvToXmlConverterMain {

	private static final Logger log = LogManager.getLogger(CsvToXmlConverterMain.class.getName());
	private static Properties props = new Properties();

	static {
		try (InputStream input = new FileInputStream("config.properties")) {			
			props.load(input);
			log.info(props.getProperty("INPUT_DIR"));
			log.info(props.getProperty("OUTPUT_DIR"));
			log.info(props.getProperty("REST_URL"));

		} catch (IOException ex) {
			System.out.println("config.properties is missing current directory , Please add , exiting the program execution");
			System.exit(0);
		}
	}

	public static void main(String... strings) {

		try {
			String inputDir = props.getProperty("INPUT_DIR");
			String outputDir = props.getProperty("OUTPUT_DIR");			
			String restUrl = props.getProperty("REST_URL");
			
			CamelContext context = new DefaultCamelContext();
			
			context.addRoutes(new RouteBuilder() {
				public void configure() throws Exception {
					errorHandler(deadLetterChannel("mock:error"));
					System.out.print(inputDir);
					from("file:"+inputDir.trim()+"?noop=true")
					.log("Reading CSV file content")
					.setProperty("REST_URL", constant(restUrl.trim()))
					.setProperty("OUTPUT_DIR", constant(outputDir.trim()))					
					.process(new TransformCsvToXmlProcessor())
							.to("seda:end");
				}
			});
		
			context.start();
			Thread.sleep(4000);
			
			ConsumerTemplate consumerTemplate = context.createConsumerTemplate();
			String result = consumerTemplate.receiveBody("seda:end",String.class);
			log.info("result="+result);
			
			context.stop();
			
			System.out.println("result="+result);
		} catch (Exception e) {
			LogHelper.logStacktrace(e);
		}
	}
}
