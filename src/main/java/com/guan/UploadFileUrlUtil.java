package com.guan;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadFileUrlUtil {
	public static final String LOGIN_ID = "logon_id";
	public static final String BEGIN_DATE = "begin_date";
	public static final String END_DATE = "end_date";
	public static final String SETTLE_DATE = "settle_date";
	public static final String PAY_BANK_CODE = "pay_bank_code";

	public static Map<String, String> assParamMap(String channelNo,
			String bankFileDate, String bankName) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("logon_id", channelNo);
		map.put("begin_date", exchangeDate(bankFileDate));
		map.put("end_date", exchangeToTomorrow(bankFileDate));
		map.put("settle_date", bankFileDate);
		map.put("pay_bank_code", bankName);

		return map;
	}

	public static String exchangeToTomorrow(String dateStr) {
		Date date = DateUtil.convertStrToDate(dateStr, "yyyy-MM-dd");
		Date nextDay = new Date(date.getTime() + 86400000L);
		return DateUtil.convertDateToStr(nextDay, "yyyy-MM-dd HH:mm:ss");
	}

	public static String exchangeDate(String dateStr) {
		Date date = DateUtil.convertStrToDate(dateStr, "yyyy-MM-dd");
		return DateUtil.convertDateToStr(date, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 2016-08-12 通过指定模式转换日期
	 * 
	 * @param dateStr
	 *            被转换的日期字符串,格式需为"yyyy-MM-dd"
	 * @param pattern
	 *            转换模式
	 * @return
	 */
	public static String exchangeDate(String dateStr, String pattern) {
		Date date = DateUtil.convertStrToDate(dateStr, "yyyy-MM-dd");
		return DateUtil.convertDateToStr(date, pattern);
	}
}
