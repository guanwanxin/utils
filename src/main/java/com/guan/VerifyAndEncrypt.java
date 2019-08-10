package com.guan;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VerifyAndEncrypt {
	private static Log log = LogFactory.getLog(VerifyAndEncrypt.class);

	public static void signValidate(String urlString, String secretKey)
			throws Exception {
		log.info("验签地址：" + urlString + "  商户秘钥 ：" + secretKey);

		if (VerifyYw(urlString, secretKey)) {
			log.info("验签通过！");
		} else {
			log.info("验签失败，数据可能被篡改！");
			throw new RuntimeException(ErrorCode.VERIFY_FAILURE.toString());
		}
	}

	public static boolean VerifyYw(String url, String secretKey)
			throws Exception {
		Map<String, String> urlMap = MatcherData.stringToMap(url);
		String sign = (String) urlMap.get("sign");
		String sign_type = (String) urlMap.get("sign_type");
		if ((sign == null) || (sign_type == null)) {
			throw new RuntimeException(ErrorCode.NO_SIGN_MSG.toString());
		}
		urlMap.remove("sign");
		urlMap.remove("sign_type");
		String urlString = urlSortStringWithoutEncoder(urlMap);
		if (sign_type.equals("MD5")) {
			String md5String = DigestUtils.md5Hex(new String(urlString
					+ secretKey).getBytes("UTF-8"));
			log.debug("加密结果：" + md5String);
			if (md5String.equals(sign)) {
				log.debug("验签通过！");
				return true;
			}
			log.info("请求签名：" + sign + "参数加密" + md5String);
		}

		return false;
	}

	public static boolean Verify(String url) throws Exception {
		Map<String, String> urlMap = MatcherData.stringToMap(url);
		String sign = (String) urlMap.get("sign");
		String sign_type = (String) urlMap.get("sign_type");
		if ((sign == null) || (sign_type == null)) {
			throw new RuntimeException(ErrorCode.NO_SIGN_MSG.toString());
		}
		urlMap.remove("sign");
		urlMap.remove("sign_type");
		String urlString = urlSortStringWithoutEncoder(urlMap);
		if (sign_type.equals("MD5")) {
			String md5String = DigestUtils.md5Hex(urlString);
			log.debug("加密结果：" + md5String);
			if (md5String.equals(sign)) {
				log.debug("验签通过！");
				return true;
			}
		}
		return false;
	}

	public static boolean Verify(String url, String bankName) throws Exception {
		Map<String, String> urlMap = MatcherData.stringToMap(url);
		String sign = (String) urlMap.get("sign");
		String sign_type = (String) urlMap.get("sign_type");
		if ((sign == null) || (sign_type == null)) {
			throw new RuntimeException(ErrorCode.NO_SIGN_MSG.toString());
		}
		urlMap.remove("sign");
		if ("alipay".equalsIgnoreCase(bankName)) {
			urlMap.remove("sign_type");
		}
		String urlString = urlSortStringWithoutEncoder(urlMap);
		if (sign_type.equals("MD5")) {
			String md5String = DigestUtils.md5Hex(urlString);
			log.debug("加密结果：" + md5String);
			if (md5String.equals(sign)) {
				log.debug("验签通过！");
				return true;
			}
		}
		return false;
	}

	public static boolean Verify(String url, String key, String charset)
			throws Exception {
		Map<String, String> urlMap = MatcherData.stringToMap(url);
		String sign = (String) urlMap.get("sign");
		String sign_type = (String) urlMap.get("sign_type");
		if ((sign == null) || (sign_type == null)) {
			throw new RuntimeException(ErrorCode.NO_SIGN_MSG.toString());
		}
		urlMap.remove("sign");
		urlMap.remove("sign_type");
		String urlString = urlSortStringWithoutEncoder(urlMap);
		if (sign_type.equals("MD5")) {
			String md5String = sign(urlString, key, charset);
			log.debug("原来sign：" + sign);
			log.debug("加密结果：" + md5String);
			if (md5String.equals(sign)) {
				log.debug("验签通过！");
				return true;
			}
		}
		return false;
	}

	public static String Encrypt(Map<String, String> urlMap)
			throws UnsupportedEncodingException {
		String encryptString = urlSortStringWithoutEncoder(urlMap);
		String md5String = DigestUtils.md5Hex(encryptString);
		log.debug("加密结果为：" + md5String);
		urlMap.put("sign", md5String);
		urlMap.put("sign_type", "MD5");
		String encryptResult = urlSortString(urlMap);
		log.debug("Encrypt结果：" + encryptResult);
		return encryptResult;
	}

	public static String EncryptYw(Map<String, String> urlMap, String secretKey)
			throws UnsupportedEncodingException {
		String encryptString = urlSortStringWithoutEncoder(urlMap);
		String md5String = DigestUtils.md5Hex(encryptString + secretKey);
		log.debug("加密结果为：" + md5String);
		urlMap.put("sign", md5String);
		urlMap.put("sign_type", "MD5");
		String encryptResult = urlSortString(urlMap);
		log.debug("Encrypt结果：" + encryptResult);
		return encryptResult;
	}

	public static String urlSortString(Map<String, String> urlMap)
			throws UnsupportedEncodingException {
		urlMap = sortHashMap(urlMap);
		List<String> arr = new ArrayList<String>();
		String reString = "";
		for (String s : urlMap.keySet()) {
			if ((urlMap.get(s) != null) && (!"".equals(urlMap.get(s)))) {
				arr.add(s + "="
						+ URLEncoder.encode((String) urlMap.get(s), "UTF-8"));
			}
		}
		reString = StringUtils.join(arr, "&");
		log.debug("序列化结果:" + reString);
		return reString;
	}

	public static String urlSortStringWithoutEncoder(Map<String, String> urlMap)
			throws UnsupportedEncodingException {
		urlMap = sortHashMap(urlMap);
		List<String> arr = new ArrayList<String>();
		String reString = "";
		for (String s : urlMap.keySet()) {
			if ((urlMap.get(s) != null) && (!"".equals(urlMap.get(s)))) {
				arr.add(s + "=" + (String) urlMap.get(s));
			}
		}
		reString = StringUtils.join(arr, "&");
		log.debug("urlSortStringWithoutEncoder序列化结果:" + reString);
		return reString;
	}

	public static Map<String, String> sortHashMap(Map<String, String> hashMap) {
		TreeMap<String, String> sorted_map = new TreeMap<String, String>();
		sorted_map.putAll(hashMap);

		return sorted_map;
	}

	public static String sign(String text, String key, String input_charset) {
		text = text + key;
		System.out.println(text);
		return DigestUtils.md5Hex(getContentBytes(text, input_charset));
	}

	public static String sign(String text, String key) {
		text = text + "&key=" + key;
		log.debug("签名key为：" + key);
		log.debug("准备md5加密的值为：" + text);
		String result = DigestUtils.md5Hex(text);
		log.debug("大小写转换前：" + result);
		log.debug("大小写转换后：" + result.toUpperCase());
		return result.toUpperCase();
	}

	private static byte[] getContentBytes(String content, String charset) {
		if ((charset == null) || ("".equals(charset))) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:"
					+ charset);
		}
	}

	public static String alipaySign(String partner, String privateKey) {
		String url = "";
		try {
			Map<String, String> sortedMap = new HashMap<String, String>();
			sortedMap.put("_input_charset", "utf-8");
			sortedMap.put("service", "sign_protocol_with_partner");
			sortedMap.put("partner", partner);
			String re = urlSortStringWithoutEncoder(sortedMap);
			String sign = sign(re, privateKey, "utf-8");
			url = "https://mapi.alipay.com/gateway.do?" + re + "&sign_type=MD5"
					+ "&sign=" + sign;

			System.out.println(url);
		} catch (Exception e) {
		}

		return url;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		alipaySign("2088211626430498", "q1le696hn0o4mu3l7h9jj3u7vq7twlnl");
		alipaySign("2088011652918650", "q1le696hn0o4mu3l7h9jj3u7vq7twlnl");
		alipaySign("2088101849484445", "q1le696hn0o4mu3l7h9jj3u7vq7twlnl");
	}
}
