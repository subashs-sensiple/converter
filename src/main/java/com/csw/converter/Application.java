package com.csw.converter;

import java.util.Scanner;

public class Application {

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		System.out.println("Enter input json file path:");
		String jsonFilePath = input.nextLine();
		System.out.println("Enter output xml file path:");
		String xmlFilePath = input.nextLine();
		try {
			System.out.println("Convertion process started....");
			ConverterFactory converterFactory = new ConverterFactory();
			XMLJSONCoverterI xmlJsonConverter = converterFactory.getConverterInstance("XML_TO_JSON");
			xmlJsonConverter.createXMLJSONConverter(jsonFilePath, xmlFilePath);
			System.out.println("Success!!! Json file converted to xml.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Oops!!! File convertion failed.");
		}
	}

}
