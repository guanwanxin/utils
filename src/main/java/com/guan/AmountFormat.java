package com.guan;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmountFormat {
	public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+";

	public static String changeF2Y(Long amount) throws Exception {
		if (!amount.toString().matches("\\-?[0-9]+")) {
			throw new Exception("解析文件中的金额格式有误");
		}

		int flag = 0;
		String amString = amount.toString();
		if (amString.charAt(0) == '-') {
			flag = 1;
			amString = amString.substring(1);
		}
		StringBuffer result = new StringBuffer();
		if (amString.length() == 1) {
			result.append("0.0").append(amString);
		} else if (amString.length() == 2) {
			result.append("0.").append(amString);
		} else {
			String intString = amString.substring(0, amString.length() - 2);
			for (int i = 1; i <= intString.length(); i++) {
				if (((i - 1) % 3 == 0) && (i != 1)) {
					result.append(",");
				}
				result.append(intString.substring(intString.length() - i, intString.length() - i + 1));
			}
			result.reverse().append(".").append(amString.substring(amString.length() - 2));
		}
		if (flag == 1) {
			return "-" + result.toString();
		}
		return result.toString();
	}

	public static String changeF2Y(String amount) throws Exception {
		if (!amount.matches("\\-?[0-9]+")) {
			throw new Exception("解析文件中的金额格式有误");
		}
		return BigDecimal.valueOf(Long.valueOf(amount).longValue()).divide(new BigDecimal(100)).toString();
	}

	public static String changeY2F(Long amount) {
		return BigDecimal.valueOf(amount.longValue()).multiply(new BigDecimal(100)).toString();
	}

	public static String changeY2F(String amount) {
		String currency = amount.replaceAll("\\$|\\￥|\\,", "");
		int index = currency.indexOf(".");
		int length = currency.length();
		Long amLong = Long.valueOf(0L);
		if (index == -1) {
			amLong = Long.valueOf(currency + "00");
		} else if (length - index >= 3) {
			amLong = Long.valueOf(currency.substring(0, index + 3).replace(".", ""));
		} else if (length - index == 2) {
			amLong = Long.valueOf(currency.substring(0, index + 2).replace(".", "") + 0);
		} else {
			amLong = Long.valueOf(currency.substring(0, index + 1).replace(".", "") + "00");
		}
		return amLong.toString();
	}

	// 金额验证
	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
		Matcher match = pattern.matcher(str);
		if (match.matches() == false) {
			return false;
		} else {
			return true;
		}
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(AmountFormat.isNumber("100d"));;
	}
	
}
