package com.guan;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class DomUtils {
	private static Log log = LogFactory.getLog(DomUtils.class);

	private static Map<String, Map<String, Object>> cashMap = new HashMap<String, Map<String, Object>>();

	public static Map<String, Object> getMapByXml(String filepath)
			throws Exception {
		if (cashMap.get(filepath) != null) {
			return (Map<String, Object>) cashMap.get(filepath);
		}
		SAXReader sr = new SAXReader();
		Document doc = null;

		InputStream input = FileLoader.loadFileAsString(filepath);
		try {
			log.info(input != null ? "配置文件读取成功" : "配置文件读取失败");
			doc = sr.read(input);
			log.info(doc != null ? "document对象读取成功" : "document对象读取失败");

			Element rootElement = doc.getRootElement();
			Map<String, Object> map = new HashMap<String, Object>();
			getSubEleent(rootElement, map, rootElement.getName());

			cashMap.put(filepath, map);
			return map;
		} catch (DocumentException e) {
			e.printStackTrace();
			log.info("置文件失败");
			log.info(e.getMessage());
			throw new RuntimeException("配置文件失败");
		}
	}

	private static void getSubEleent(Element element, Map<String, Object> map,
			String keyName) {
		List<?> list = element.elements();
		Map<String, Object> subMap = new HashMap<String, Object>();

		for (int i = 0; i < list.size(); i++) {
			Element e = (Element) list.get(i);
			List<?> el = e.elements();
			if ((el != null) && (el.size() > 0)) {
				getSubEleent(e, subMap, e.getName());
			} else {
				subMap.put(e.getName(), e.getTextTrim());
			}
		}
		map.put(keyName, subMap);
	}

	@SuppressWarnings("unchecked")
	public static void ssss(Map<String, Object> map) {
		for (String str : map.keySet()) {
			boolean flag = false;
			try {
				flag = true;
			} catch (Exception e) {
			}

			if (flag) {
				System.out.println(str + "------" + (String) map.get(str));
			} else {
				ssss((Map<String, Object>) map.get(str));
			}
		}
	}

	public static void main(String[] args) {
		try {
			Map<String, Object> map = getMapByXml("orderdetail/mercomfig.xml");
			ssss(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
