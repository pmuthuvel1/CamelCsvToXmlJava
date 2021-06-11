FROM java:8
EXPOSE 8080
ADD /target/CamelCsvToXmlJava-0.0.1-SNAPSHOT-jar-with-dependencies.jar camelcsvtoxmljava.jar
ENTRYPOINT ["java","-jar","camelcsvtoxmljava.jar"]