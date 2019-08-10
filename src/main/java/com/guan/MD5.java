package com.guan;

import java.security.MessageDigest;

public class MD5 {
	public static byte[] getMD5Mac(byte[] bySourceByte) {
		byte[] byDisByte;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.reset();
			md.update(bySourceByte);
			byDisByte = md.digest();
		} catch (java.security.NoSuchAlgorithmException n) {
			return null;
		}
		return byDisByte;
	}

	public static String bintoascii(byte[] bySourceByte) {
		String result = new String();
		int len = bySourceByte.length;
		for (int i = 0; i < len; i++) {
			byte tb = bySourceByte[i];
			char tmp = (char) (tb >>> 4 & 0xF);
			char high;
			if (tmp >= '\n') {
				high = (char) ('a' + tmp - 10);
			} else
				high = (char) ('0' + tmp);
			result = result + high;
			tmp = (char) (tb & 0xF);
			char low;
			if (tmp >= '\n') {
				low = (char) ('a' + tmp - 10);
			} else
				low = (char) ('0' + tmp);
			result = result + low;
		}
		return result;
	}

	public static String getMD5ofStr(String inbuf) {
		if ((inbuf == null) || ("".equals(inbuf)))
			return "";
		return bintoascii(getMD5Mac(inbuf.getBytes()));
	}

	public static String getMD5ofStr(String inbuf, String charset) {
		if ((inbuf == null) || ("".equals(inbuf)))
			return "";
		try {
			return bintoascii(getMD5Mac(inbuf.getBytes(charset)));
		} catch (java.io.UnsupportedEncodingException e) {
		}
		return "";
	}

	public static String getSign(String inbuf) {
		return getMD5ofStr(inbuf).toLowerCase();
	}

	public static void main(String[] args)
			throws java.io.UnsupportedEncodingException {
		new MD5();
		String sign = getMD5ofStr("京东机票abc_123jingdong_tongcheng_airplane",
				"GBK").toLowerCase();
		System.out.println(sign);
	}
}
