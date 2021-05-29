package com.csw.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLJSONConverterImpl implements XMLJSONCoverterI {

	private final String ARRAY = "array";
	private final String OBJECT = "object";
	private final String STRING = "string";
	private final String NUMBER = "number";
	private final String BOOLEAN = "boolean";
	private final String NULL = "null";
	private final String NAME = "name";

	@Override
	public void createXMLJSONConverter(String jsonFilePath, String xmlFilePath) throws Exception {

		boolean isValidInputFilePath = isValidFilePath(jsonFilePath, false);
		if (!isValidInputFilePath) {
			throw new FileNotFoundException("Input file is not found!!! Please provide a valid file path.");
		}

		boolean isValidOutputFilePath = isValidFilePath(xmlFilePath, true);
		if (!isValidOutputFilePath) {
			throw new FileNotFoundException("Output directory is not found!!! Please provide a valid file path.");
		}

		JSONParser jsonParser = new JSONParser();
		try {
			FileReader reader = new FileReader(jsonFilePath);
			Object obj = jsonParser.parse(reader);
			frameXML(obj, xmlFilePath);
			reader.close();
		} catch (Exception e) {
			throw e;
		}

	}

	private boolean isValidFilePath(String filePath, boolean dirCheck) {

		boolean isValid = false;

		if (null != filePath && !filePath.isEmpty()) {

			File file = new File(filePath);
			if (!dirCheck) {
				if (file.exists() && !file.isDirectory()) {
					isValid = true;
				}
			} else {
				file = file.getParentFile();
				if (file.exists() && file.isDirectory()) {
					isValid = true;
				}
			}
		}

		return isValid;
	}

	private void frameXML(Object obj, String filePath) throws ParserConfigurationException, TransformerException {

		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

		Document document = documentBuilder.newDocument();

		if (obj instanceof JSONObject) {
			Element root = document.createElement(OBJECT);
			document.appendChild(root);
			frameObject(obj, document, root);
		} else if (obj instanceof JSONArray) {
			Element root = document.createElement(ARRAY);
			document.appendChild(root);
			frameObjectArray(obj, document, root);
		} else {
			framePlainText(obj, document, null);
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource domSource = new DOMSource(document);
		StreamResult streamResult = new StreamResult(new File(filePath));
		transformer.transform(domSource, streamResult);
	}

	private void frameObjectArray(Object obj, Document document, Element root) {
		JSONArray jsonArray = (JSONArray) obj;
		for (Object childObject : jsonArray) {
			if (childObject instanceof JSONArray) {
				Element array = document.createElement(ARRAY);
				root.appendChild(array);
				frameObjectArray(childObject, document, array);
			} else if (childObject instanceof JSONObject) {
				Element object = document.createElement(OBJECT);
				root.appendChild(object);
				frameObject(childObject, document, object);
			} else {
				framePlainText(childObject, document, root);
			}
		}

	}

	private void frameObject(Object obj, Document document, Element root) {

		JSONObject jsonObject = (JSONObject) obj;
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			Object property = jsonObject.get(key);

			if (property instanceof JSONArray) {

				Element arrayElement = document.createElement(ARRAY);
				root.appendChild(arrayElement);

				Attr attr = document.createAttribute(NAME);
				attr.setValue(key);
				arrayElement.setAttributeNode(attr);

				frameObjectArray(property, document, arrayElement);

			} else if (property instanceof JSONObject) {

				Element arrayElement = document.createElement(OBJECT);
				root.appendChild(arrayElement);

				Attr attr = document.createAttribute(NAME);
				attr.setValue(key);
				arrayElement.setAttributeNode(attr);

				frameObject(property, document, arrayElement);

			} else {
				String fieldType = findFieldType(property);
				if (NULL.equals(fieldType)) {
					Element arrayElement = document.createElement(fieldType);
					root.appendChild(arrayElement);

					Attr attr = document.createAttribute(NAME);
					attr.setValue(key);
					arrayElement.setAttributeNode(attr);

				} else {

					Element arrayElement = document.createElement(fieldType);
					arrayElement.appendChild(document.createTextNode(String.valueOf(property)));
					root.appendChild(arrayElement);

					Attr attr = document.createAttribute(NAME);
					attr.setValue(key);
					arrayElement.setAttributeNode(attr);

				}
			}

		}

	}

	private void framePlainText(Object obj, Document document, Element root) {

		String fieldType = findFieldType(obj);
		if (NULL.equals(fieldType)) {
			Element arrayElement = document.createElement(fieldType);
			if (null != root) {
				root.appendChild(arrayElement);
			} else {
				document.appendChild(arrayElement);
			}
		} else {
			Element arrayElement = document.createElement(fieldType);
			arrayElement.appendChild(document.createTextNode(String.valueOf(obj)));
			if (null != root) {
				root.appendChild(arrayElement);
			} else {
				document.appendChild(arrayElement);
			}
		}
	}

	private String findFieldType(Object obj) {
		String fieldType = null;
		if (obj instanceof Long || obj instanceof Integer || obj instanceof Float || obj instanceof Double) {
			fieldType = NUMBER;
		} else if (obj instanceof Boolean) {
			fieldType = BOOLEAN;
		} else if (null == obj || NULL.equalsIgnoreCase(String.valueOf(obj))) {
			fieldType = NULL;
		} else {
			fieldType = STRING;
		}

		return fieldType;
	}

}
