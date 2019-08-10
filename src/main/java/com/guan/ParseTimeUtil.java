package com.guan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParseTimeUtil {
	public static final String DATE_FORMAT_YYYYMMDD = "yyyy-MM-dd";
	public static final String DATE_FORMAT_TIME_R = "yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_TIME_T = "dd-MM-yyyy HH:mm:ss";
	public static final String DB_TIME_PATTERN = "yyyyMMddHHmmss";
	public static final String YYYYMMDD = "yyyy-MM-dd";
	public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

	public static Date parseDate(String dateStr, String pattern)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.parse(dateStr);
	}

	public static String formatDate(Object object, String argFormat) {
		if (object == null) {
			return "";
		}
		Date date = (Date) object;
		SimpleDateFormat sdfFrom = null;
		String strResult = null;
		try {
			sdfFrom = new SimpleDateFormat(argFormat);
			strResult = sdfFrom.format(date).toString();
		} catch (Exception e) {
			strResult = "";
		} finally {
			sdfFrom = null;
		}

		return strResult;
	}
}
