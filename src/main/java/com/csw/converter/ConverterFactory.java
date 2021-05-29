package com.csw.converter;

public class ConverterFactory {

	public XMLJSONConverterImpl getConverterInstance(String type) {

		if (type == null) {
			return null;
		}

		if (type.equalsIgnoreCase("XML_TO_JSON")) {
			return new XMLJSONConverterImpl();
		}

		return null;
	}
}
