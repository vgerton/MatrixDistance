package org.matrixdistance.client;


import org.codehaus.jackson.map.ObjectMapper;
import org.matrixdistance.domain.Matrix;
import org.matrixdistance.fileprocessing.FileProcessing;
import org.matrixdistance.url.UrlBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleMapsClient {

    private static String URL = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=Brest,Belarus&destinations=Minsk,Belarus&mode=driving&language=ru-RU&sensor=false";

    private static RestTemplate restTemplate;

    public static void main(String [] args) throws IOException {

        restTemplate = new RestTemplate();
//        String result = restTemplate.getForObject(URL, String.class);
//
//        System.out.println(result);
//
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            Matrix matrix = mapper.readValue(result, Matrix.class);
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            System.out.println(e);
//        }

        FileProcessing fileProcessingInput = new FileProcessing("countries.txt", true);
        List<String> countries = fileProcessingInput.getCountries();

//        System.out.println(countries.size());
//        for (int i=0; i<countries.size(); i++)
//            System.out.println(countries.get(i));

        createCsvFile2(countries);

    }

    public static void createCsvFile(List<String> countries) throws IOException {
        UrlBuilder urlBuilder = new UrlBuilder();
        FileProcessing fileProcessingOutput = new FileProcessing("result.csv", false);
        fileProcessingOutput.writeMatrixHeader(countries);
        for (int i=0; i<countries.size()-1; i++) {
            String originCountry = countries.get(i);
            List<String> destinationCountries = new ArrayList<String>();
            for (int j=i+1; j<countries.size(); j++) {
                destinationCountries.add(countries.get(j));
            }
            String url = urlBuilder.buildUrl(originCountry, destinationCountries);
            String result = restTemplate.getForObject(url, String.class);
            fileProcessingOutput.writeLine(i, countries.get(i), result);
            System.out.println(result);
        }

        fileProcessingOutput.closeResource();
    }

    public static void createCsvFile2(List<String> countries) throws IOException {
        UrlBuilder urlBuilder = new UrlBuilder();
        FileProcessing fileProcessingOutput = new FileProcessing("result.csv", false);
        for (int i=0; i<countries.size()-1; i++) {
            String originCountry = countries.get(i);
            for (int j=i+1; j<countries.size(); j++) {
                String destinationCountry = countries.get(j);
                String url = urlBuilder.buildUrl(originCountry, destinationCountry);
                String jsonResult = restTemplate.getForObject(url, String.class);
                System.out.println(jsonResult);
                fileProcessingOutput.writeLine(originCountry, destinationCountry, jsonResult);
            }
        }

        fileProcessingOutput.closeResource();
    }
}
