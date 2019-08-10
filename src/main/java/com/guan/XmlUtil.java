package com.guan;

import java.util.*;

public class XmlUtil {

	public static String getForm(String url, Map<String, String> sPara, String title) throws Exception {
		List<String> keys = new ArrayList<String>(sPara.keySet());
		StringBuffer sbHtml = new StringBuffer();
		sbHtml.append("<title>" + title + "</title>");
		sbHtml.append("<form id=\"paysubmit\" action=\"" + url + "\" method=\"post\">");

		for (int i = 0; i < keys.size(); i++) {
			String name = (String) keys.get(i);
			String value = (String) sPara.get(name);
			sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
		}

		sbHtml.append("</form>");
		sbHtml.append("<script>document.forms['paysubmit'].submit();</script>");
		System.out.println("组装后的form表单为：" + sbHtml.toString());
		return sbHtml.toString();
	}

}
