package com.guan;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.StringUtils;

public class StringUtil {
	public static final int DIRECT_LEFT = 0;
	public static final int DIRECT_RIGHT = 1;

	public static String nvl(String sIn) {
		return sIn == null ? "" : sIn;
	}

	public static boolean isNullOrEmpty(CharSequence argCharSeq) {
		if ((argCharSeq == null) || (argCharSeq.toString().trim().length() < 1)) {
			return true;
		}

		return false;
	}

	public static boolean isNullOrEmpty(Timestamp timestamp) {
		if ((timestamp == null) || (timestamp.toString().trim().length() < 1)) {
			return true;
		}

		return false;
	}

	public static boolean equalsString(String argStr1, String argStr2) {
		if ((argStr1 == null) && (argStr2 == null)) {
			return true;
		}
		if ((argStr1 == null) || (argStr2 == null)) {
			return false;
		}
		return argStr1.equals(argStr2);
	}

	public static String getFirstLowerCase(String argString) {
		if (isNullOrEmpty(argString)) {
			return "";
		}
		if (argString.length() < 2) {
			return argString.toLowerCase();
		}

		char ch = argString.charAt(0);
		ch = Character.toLowerCase(ch);

		return ch + argString.substring(1);
	}

	public static String getFirstUpperCase(String argString) {
		if (isNullOrEmpty(argString)) {
			return "";
		}
		if (argString.length() < 2) {
			return argString.toUpperCase();
		}

		char ch = argString.charAt(0);
		ch = Character.toUpperCase(ch);

		return ch + argString.substring(1);
	}

	public static void appendLine(StringBuffer argSbf) {
		argSbf.append(System.getProperty("line.separator"));
	}

	public static String formatMsg(String src, Object... argParams) {
		return String.format(src, argParams);
	}

	public static int getCount(String src, String strChar) {
		int result = 0;

		int beginIndex = 0;
		int endIndex = src.indexOf(strChar, beginIndex);

		while (endIndex >= 0) {
			result++;
			beginIndex = endIndex + strChar.length();
			endIndex = src.indexOf(strChar, beginIndex);
		}

		return result;
	}

	public static String replaceStr(String src, String sFnd, String sRep) {
		if ((src == null) || ("".equals(nvl(sFnd)))) {
			return src;
		}

		String sTemp = "";
		int endIndex = 0;
		int beginIndex = 0;
		do {
			endIndex = src.indexOf(sFnd, beginIndex);
			if (endIndex >= 0) {
				sTemp = sTemp + src.substring(beginIndex, endIndex) + nvl(sRep);
				beginIndex = endIndex + sFnd.length();
			} else if (beginIndex >= 0) {
				sTemp = sTemp + src.substring(beginIndex);
				break;
			}
		} while (endIndex >= 0);

		return sTemp;
	}

	public static int compare(String argStr1, String argStr2) {
		if ((argStr1 == null) && (argStr2 == null)) {
			return 0;
		}
		if (argStr1 == null) {
			return -1;
		}
		if (argStr2 == null) {
			return 1;
		}

		return argStr1.compareTo(argStr2);
	}

	public static String paddingSpaceForChar(String strIn, int len, int direct) {
		if (strIn == null) {
			return null;
		}
		int strInLen = getStrLength(strIn);
		if (strInLen == len) {
			return strIn;
		}
		if (strInLen > len) {
			return chopStr(strIn, len);
		}

		StringBuffer sb = new StringBuffer(strIn == null ? "" : strIn);
		for (int i = 0; i < len - strInLen; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	public static boolean isHalf(char c) {
		if (((65377 > c) || (c > 65439)) && ((' ' > c) || (c > '~'))) {
			return false;
		}
		return true;
	}

	public static int getStrLength(String s) {
		if (s == null) {
			return 0;
		}

		int len = 0;
		for (int i = 0; i < s.length(); i++) {
			if (isHalf(s.charAt(i))) {
				len++;
			} else {
				len += 2;
			}
		}
		return len;
	}

	public static String chopStr(String s, int byteLen) {
		if (byteLen < 0) {
			return "";
		}
		if (s == null) {
			return null;
		}

		int len = 0;
		for (int i = 0; i < s.length(); i++) {
			if (isHalf(s.charAt(i))) {
				len++;
			} else {
				len += 2;
			}
			if (len > byteLen) {
				return s.substring(0, i);
			}
		}
		return s;
	}

	public static String toString(Object obj) {
		return obj == null ? "" : obj.toString();
	}

	public static String splitAmount(BigDecimal decimal) {
		if (decimal == null) {
			return "";
		}

		String sign = "";
		if (decimal.doubleValue() < 0.0D) {
			sign = "-";
		}
		String str = decimal.setScale(2, 4).abs().toString();
		StringBuffer sb = new StringBuffer(str);
		int len = sb.length() - 3;
		int i = 0;
		while (len > 3) {
			sb.insert(sb.length() - 3 - (3 + i * 4), ',');
			len -= 3;
			i++;
		}
		return sign + sb.toString();
	}

	public static String splitString(String str) {
		StringBuffer sb = null;
		if (str.contains("-")) {
			str = str.replace("-", "");
			sb = new StringBuffer(str);
			int len = sb.length() - 3;
			int i = 0;
			while (len > 3) {
				sb.insert(sb.length() - 3 - (3 + i * 4), ',');
				len -= 3;
				i++;
			}
			return "-" + sb.toString();
		}
		sb = new StringBuffer(str);
		int len = sb.length() - 3;
		int i = 0;
		while (len > 3) {
			sb.insert(sb.length() - 3 - (3 + i * 4), ',');
			len -= 3;
			i++;
		}
		return sb.toString();
	}

	public static String numberStrAbs(String data) {
		String rslt = "0";
		if (!StringUtils.isEmpty(data)) {
			data = data.replace(",", "");
			BigDecimal bigDecimal = new BigDecimal(data);

			bigDecimal = bigDecimal.abs();
			rslt = bigDecimal.toString();
		}
		return rslt;
	}

	public static void main(String[] args) {
		String data = "1494.00";
		String rslt = numberStrAbs(data);
		System.out.println(rslt);
	}
}
