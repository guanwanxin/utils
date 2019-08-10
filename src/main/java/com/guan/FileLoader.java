package com.guan;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class FileLoader {
	public static InputStream loadFileAsString(String filePath)
			throws IOException, DocumentException {
		return Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(filePath);
	}

	public static Properties loadFileAsProperties(String filePath)
			throws IOException, DocumentException {
		Properties prop = new Properties();
		prop.load(loadFileAsString(filePath));
		return prop;
	}

	public static Document loadFileAsDocument(String filePath)
			throws IOException, DocumentException {
		SAXReader reader = new SAXReader();
		return reader.read(loadFileAsString(filePath));
	}
}
