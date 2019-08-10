package com.guan;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MatcherData {
	private static Log log = LogFactory.getLog(MatcherData.class);

	public static Matcher getMatcher(String source, String regex, boolean flag) {
		if ((null == source) || (regex == null))
			return null;
		Pattern p = null;
		if (flag) {
			p = Pattern.compile(regex, 32);
		} else {
			p = Pattern.compile(regex);
		}
		return p.matcher(source);
	}

	public static String repace(String content) {
		if (StringUtils.isEmpty(content))
			return content;
		String regex = "[\\.\\?\\(\\)\\[\\]\\{\\}\\:\\|\\/\\-\\%\\$\\+\\^\"'\\*]";
		Matcher m = getMatcher(content, regex, true);
		StringBuffer sb = new StringBuffer("");
		while (m.find()) {
			m.appendReplacement(sb, "\\\\" + m.group());
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static Map<String, String> stringToMap(String params)
			throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		Matcher m = getMatcher(params, "^(.*?)\\=(.*?)(&(.*?)\\=(.*?))*", false);
		if (StringUtils.isEmpty(params)) {
			log.error("http接口请求方法调用参数为空！");
			throw new RuntimeException("QUERY_IS_NULL");
		}

		if (!m.matches()) {
			throw new Exception("参数格式不正确！");
		}
		String[] pss = params.split("&");
		for (String p : pss) {
			String[] kv = p.split("=");
			if ((kv.length == 2) && (StringUtils.isNotEmpty(kv[0]))
					&& (StringUtils.isNotEmpty(kv[1]))) {
				log.info("键：" + URLDecoder.decode(kv[0], "UTF-8") + "，值:"
						+ URLDecoder.decode(kv[1], "UTF-8"));
				param.put(URLDecoder.decode(kv[0], "UTF-8"),
						URLDecoder.decode(kv[1], "UTF-8"));
			} else {
				throw new Exception("数据格式不正确！" + kv[0]);
			}
		}
		log.debug("MatcherData:" + param);
		return param;
	}

	public static String mapToString(Map<String, String> urlMap)
			throws UnsupportedEncodingException {
		List<String> arr = new ArrayList<String>();
		String reString = "";
		for (String s : urlMap.keySet()) {
			if (urlMap.get(s) != null) {
				arr.add(s + "="
						+ URLEncoder.encode((String) urlMap.get(s), "UTF-8"));
			} else {
				arr.add(s + "=");
			}
		}
		reString = StringUtils.join(arr, "&");
		log.debug("序列化结果:" + reString);
		return reString;
	}

	public static boolean accountMacther(String account) {
		String reg = "^(([0-9](\\d)*(\\.\\d[\\d]?)?)|(0\\.\\d[\\d]?))[dD]?$";
		Matcher matcher = getMatcher(account, reg, false);
		return matcher.matches();
	}

	public static boolean subAccountMacther(String account) {
		String reg = "^-?(([0-9](\\d)*(\\.\\d[\\d]?)?)|(0\\.\\d[\\d]?))[dD]?$";
		Matcher matcher = getMatcher(account, reg, false);
		return matcher.matches();
	}

	public static boolean isValidDate(String sDate) {
		Pattern p = Pattern
				.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))+$");

		return p.matcher(sDate).matches();
	}
}
