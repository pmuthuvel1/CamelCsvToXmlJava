FROM openjdk:8
WORKDIR /test
ADD config.properties config.properties
ADD log4j2.xml log4j2.xml
ADD data2.csv data2.csv
ADD data.csv data.csv
ADD input input
ADD output output
ADD logs logs
ADD data.csv input/data.csv
ADD data2.csv input/data2.csv
ADD /target/CamelCsvToXmlJava-jar-with-dependencies.jar camelcsvtoxmljava.jar
ENTRYPOINT ["java","-Dlog4j.configurationFile=log4j2.xml","-jar","camelcsvtoxmlja
va.jar"]
