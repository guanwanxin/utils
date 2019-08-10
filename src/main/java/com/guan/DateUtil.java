package com.guan;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateUtil {
	public static final String yyMMdd = "yy-MM-dd";
	public static final String yyyyMMdd = "yyyy-MM-dd";
	public static final String HHmmss = "HH:mm:ss";
	public static final String HHmm = "HH:mm";
	public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	public static final String yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
	public static final String yyMMddHHmmss = "yy-MM-dd HH:mm:ss";
	public static final String JAVA_MIN_SHORT_DATE_STR = "1970-01-01";
	public static final String JAVA_MIN_LONG_DATE_STR = "1970-01-01 00:00:00:00";
	public static final Timestamp JAVA_MIN_TIMESTAMP = convertStrToTimestamp("1970-01-01 00:00:00:00");

	public static Timestamp convertStrToTimestamp(String dateStr) {
		return convertStrToTimestamp(dateStr, false);
	}

	public static Timestamp convertStrToTimestampZero(String dateStr) {
		return convertStrToTimestamp(dateStr, true);
	}

	public static String nDayAfter() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		cal.set(cal.get(1), cal.get(2), cal.get(5) + 2, 23, 59, 59);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String mydate = simpleDateFormat.format(cal.getTime());
		return mydate;
	}

	public static String tomorrowDayAfter() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		cal.set(cal.get(1), cal.get(2), cal.get(5) + 1, 23, 59, 59);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String mydate = simpleDateFormat.format(cal.getTime());
		return mydate;
	}

	public static String dafTomorrowDayAfter() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		cal.set(cal.get(1), cal.get(2), cal.get(5) + 3, 23, 59, 59);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String mydate = simpleDateFormat.format(cal.getTime());
		return mydate;
	}

	public static String currentDate() {
		return getCurrDateStr("yyyy-MM-dd HH:mm:ss");
	}

	private static Timestamp convertStrToTimestamp(String dateStr,
			boolean addZeroTime) {
		if (dateStr == null) {
			return null;
		}

		String dStr = dateStr.trim();
		if (dStr.indexOf(" ") == -1) {
			if (addZeroTime) {
				dStr = dStr + " 00:00:00:00";
			} else {
				dStr = dStr + " " + getCurrDateStr("HH:mm:ss");
			}
		}

		java.util.Date utilDate = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		try {
			utilDate = simpleDateFormat.parse(dStr);
		} catch (Exception ex) {
			throw new RuntimeException("DateUtil.convertStrToTimestamp(): "
					+ ex.getMessage());
		}

		return new Timestamp(utilDate.getTime());
	}

	public static Timestamp getCurrTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static String getCurrDateStr(String dateFormat) {
		return convertDateToStr(new java.util.Date(), dateFormat);
	}

	public static String convertDateToStr(java.util.Date date, String dateFormat) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}

	public static String convertStrToWeek(String dateStr, String dateFormat) {
		if ((dateStr == null) || (dateStr.equals(""))) {
			return null;
		}
		return convertDateToStr(convertStrToDate(dateStr, dateFormat), "E");
	}

	public static java.util.Date convertStrToDate(String dateStr,
			String dateFormat) {
		if ((dateStr == null) || (dateStr.equals(""))) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		try {
			return sdf.parse(dateStr);
		} catch (Exception e) {
			throw new RuntimeException("DateUtil.convertStrToDate():"
					+ e.getMessage());
		}
	}

	public static java.sql.Date convertStrToSqlDate(String s) {
		return convertToSqlDate(convertStrToDate(s, "yyyy-MM-dd"));
	}

	public static java.sql.Date convertToSqlDate(java.util.Date date) {
		if (date == null) {
			return null;
		}

		String dateStr = convertDateToStr(date, "yyyy-MM-dd");
		return java.sql.Date.valueOf(dateStr);
	}

	public static java.sql.Date convertToSqlDate() {
		java.util.Date date = new java.util.Date();
		String dateStr = convertDateToStr(date, "yyyy-MM-dd");
		return java.sql.Date.valueOf(dateStr);
	}

	public static double dateDiff(String datepart, java.util.Date startdate,
			java.util.Date enddate) {
		if ((datepart == null) || (datepart.equals(""))) {
			throw new IllegalArgumentException("DateUtil.dateDiff()方法非法参数值："
					+ datepart);
		}

		double distance = (enddate.getTime() - startdate.getTime()) / 86400000L;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(enddate.getTime() - startdate.getTime());
		if (datepart.equals("yy")) {
			distance /= 365.0D;
		} else if (datepart.equals("MM")) {
			distance /= 30.0D;
		} else if (datepart.equals("dd")) {
			distance = (enddate.getTime() - startdate.getTime()) / 86400000L;
		} else if ("hh".equals(datepart))
            distance = (enddate.getTime() - startdate.getTime()); 
		else if (datepart.equals("ss")) {
			distance = (enddate.getTime() - startdate.getTime()) / 1000L;
		} else if (datepart.equals("mm")) {
			distance = (enddate.getTime() - startdate.getTime()) / 1000L / 60.0D;
		} else {
			throw new IllegalArgumentException("DateUtil.dateDiff()方法非法参数值："
					+ datepart);
		}
		return distance;
	}

	public static long dateDiffForDay(java.util.Date startdate,
			java.util.Date enddate) {
		startdate = convertStrToDate(convertDateToStr(startdate, "yyyy-MM-dd"),
				"yyyy-MM-dd");
		enddate = convertStrToDate(convertDateToStr(enddate, "yyyy-MM-dd"),
				"yyyy-MM-dd");
		long distance = (enddate.getTime() - startdate.getTime()) / 86400000L;
		return Math.abs(distance);
	}

	public static double dateDiff(String datepart, Timestamp startdate,
			Timestamp enddate) {
		if ((datepart == null) || (datepart.equals(""))) {
			throw new IllegalArgumentException("DateUtil.dateDiff()方法非法参数值："
					+ datepart);
		}

		double distance = (enddate.getTime() - startdate.getTime()) / 86400000L;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(enddate.getTime() - startdate.getTime());
		if (datepart.equals("yy")) {
			distance /= 365.0D;
		} else if (datepart.equals("MM")) {
			distance /= 30.0D;
		} else if (datepart.equals("dd")) {
			distance = (enddate.getTime() - startdate.getTime()) / 86400000L;
		} else if ("hh".equals(datepart)) {
			distance = (enddate.getTime() - startdate.getTime()) * 1.0D / 3600000.0D;
		} else if (datepart.equals("ss")) {
			distance = (enddate.getTime() - startdate.getTime()) / 1000L;
		} else if (datepart.equals("mm")) {
			distance = (enddate.getTime() - startdate.getTime()) / 1000L / 60.0D;
		} else {
			throw new IllegalArgumentException("DateUtil.dateDiff()方法非法参数值："
					+ datepart);
		}
		return distance;
	}

	public static int getCurrentWeekOfYear(java.util.Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int currentWeekOfYear = cal.get(3);
		return currentWeekOfYear;
	}

	public static java.util.Date addDate(String datepart, int number,
			java.util.Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (datepart.equals("yy")) {
			cal.add(1, number);
		} else if (datepart.equals("MM")) {
			cal.add(2, number);
		} else if (datepart.equals("dd")) {
			cal.add(5, number);
		} else if (datepart.equals("hh")) {
			cal.add(11, number);
		} else if (datepart.equals("mm")) {
			cal.add(12, number);
		} else {
			throw new IllegalArgumentException("DateUtil.addDate()方法非法参数值："
					+ datepart);
		}

		return cal.getTime();
	}

	public static java.util.Date todayEnd() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		cal.set(cal.get(1), cal.get(2), cal.get(5), 23, 59, 59);
		return cal.getTime();
	}

	public static java.util.Date yestodayEnd() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		cal.set(cal.get(1), cal.get(2), cal.get(5) - 1, 23, 59, 59);
		return cal.getTime();
	}

	public static java.util.Date yestodayBegin() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		cal.set(cal.get(1), cal.get(2), cal.get(5) - 2, 23, 59, 59);
		return cal.getTime();
	}

	public static java.util.Date yestodayDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		cal.set(cal.get(1), cal.get(2), cal.get(5) - 1);
		return cal.getTime();
	}

	public static String convertToNextDay() {
		java.util.Date date = new java.util.Date(
				System.currentTimeMillis() - 86400000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(date);
	}

	public static java.util.Date checkDate(String date, String format)
			throws Exception {
		DateFormat df = new SimpleDateFormat(format);
		java.util.Date d = df.parse(date);
		if (!date.equals(df.format(d))) {
			throw new Exception("日期格式错误");
		}
		return d;
	}

	public static String getSpecifiedDayBefore(String specifiedDay) {
		Calendar c = Calendar.getInstance();
		java.util.Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day = c.get(5);
		c.set(5, day - 9);

		String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c
				.getTime());

		return dayBefore;
	}
	
	public static void convertDateInMap(Map<String, Object> map) {
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if (value instanceof Date) {
				value = convertDateToStr((Date)value, yyyyMMddHHmmss);
				map.put(key, value);
			}
		}
	}
	
	public static String parseJsonDate(String s) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");  
        Date date=sd.parse(s);  
        String str=sdf.format(date);
		return str;
	}

	public static String formatDate(Date argDate, String argFormat) {
		if (argDate == null) {
			return "";
		}

		SimpleDateFormat sdfFrom = null;
		String strResult = null;
		try {
			sdfFrom = new SimpleDateFormat(argFormat);
			strResult = sdfFrom.format(argDate).toString();
		} catch (Exception e) {
			strResult = "";
		} finally {
			sdfFrom = null;
		}

		return strResult;
	}
	
	// 日期校验
	public static boolean isDate(String str) {
		Pattern pattern = Pattern.compile("^\\d{4}\\d{2}\\d{2}$");   
		Matcher match = pattern.matcher(str);
		if (match.matches() == false) {
			return false;
		} else {
			return true;
		}
	}
	
	// 日期校验
	public static boolean isTime(String str) {
		Pattern pattern = Pattern.compile("^\\d{2}\\d{2}\\d{2}$");   
		Matcher match = pattern.matcher(str);
		if (match.matches() == false) {
			return false;
		} else {
			return true;
		}
	}
	
	
}
