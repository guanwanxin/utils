package com.guan;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

public class MapUtils {
	
	public static final String URL_MIDDLE_SEP_CHAR = "<hi:=>"; // URL拼接时的K与V之间的间隔符
	
	public static final String URL_END_SEP_CHAR = "<hi:$$>"; // URL拼接时的K-V与K-V的间隔符
	
	public static final String URL_END_SEP_CHAR_TRANS = "<hi:\\$\\$>"; // URL拼接时的K-V与K-V的间隔符

	/**
	 * 使用 Map按key进行排序得到key=value的字符串
	 * 
	 * @param map
	 * @param eqaulsType
	 *            K与V之间的拼接字符串 = 或者其他...
	 * @param spliceType
	 *            K-V与K-V之间的拼接字符串 & 或者|...
	 * @return
	 */
	public static String stringByKey(Map<String, String> map,
			String eqaulsType, String spliceType) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (sb.length() == 0) {
				sb.append(entry.getKey()).append(eqaulsType)
						.append(entry.getValue());
			} else {
				sb.append(spliceType).append(entry.getKey()).append(eqaulsType)
						.append(entry.getValue());
			}
		}

		return sb.toString();
	}

	public static String stringByKeyObj(Map<String, Object> map,
			String eqaulsType, String spliceType) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (sb.length() == 0) {
				sb.append(entry.getKey()).append(eqaulsType)
						.append(entry.getValue());
			} else {
				sb.append(spliceType).append(entry.getKey()).append(eqaulsType)
						.append(entry.getValue());
			}
		}

		return sb.toString();
	}

	/**
	 * 使用 Map按key进行排序得到key=value的字符串
	 * 
	 * @param plain
	 * @param eqaulsType
	 *            K与V之间的拼接字符串 = 或者其他...
	 * @param spliceType
	 *            K-V与K-V之间的拼接字符串 & 或者|...
	 * @return
	 */
	public static Map<String, String> stringToMap(String plain,
			String eqaulsType, String spliceType) {
		if (plain == null || plain.isEmpty()) {
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();

		String[] split = plain.split(spliceType);
		for (String kv : split) {
			if ("|".equals(kv)) {
				continue;
			}
			String[] kvArr = kv.split(eqaulsType);
			if (kvArr.length == 2) {
				map.put(kvArr[0], kvArr[1]);
			} else {
				map.put(kvArr[0], "");
			}
		}

		return map;
	}

	/**
	 * 使用 Map按key进行排序得到key=value的字符串
	 * 
	 * @param map
	 * @param spliceType
	 *            拼接字符串 & 或者|...
	 * @return
	 */
	public static String sortStringByKey(Map<String, Object> map,
			String spliceType) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, Object> sortMap = new TreeMap<String, Object>(
				new Comparator<String>() {

					@Override
					public int compare(String str1, String str2) {
						return str1.compareTo(str2);
					}
				});
		sortMap.putAll(map);

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : sortMap.entrySet()) {
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append(spliceType);
		}

		sb.deleteCharAt(sb.lastIndexOf(spliceType));
		return sb.toString();
	}

	/**
	 * 使用 Map按key进行排序得到value拼接的字符串
	 * 
	 * @param map
	 * @param spliceType
	 *            拼接字符串 & 或者|...
	 * @return
	 */
	public static String sortStringByKeyGetValue(Map<String, Object> map,
			String spliceType) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, Object> sortMap = new TreeMap<String, Object>(
				new Comparator<String>() {

					@Override
					public int compare(String str1, String str2) {
						return str1.compareTo(str2);
					}
				});
		sortMap.putAll(map);

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : sortMap.entrySet()) {
			sb.append(entry.getValue());
			sb.append(spliceType);
		}

		sb.deleteCharAt(sb.lastIndexOf(spliceType));
		return sb.toString();
	}

	/**
	 * 使用 Map按key进行排序
	 * 
	 * @param map
	 * @return
	 */
	public static String sortStringByLine(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, Object> sortMap = new TreeMap<String, Object>(
				new Comparator<String>() {

					@Override
					public int compare(String str1, String str2) {
						return str1.compareTo(str2);
					}
				});
		sortMap.putAll(map);

		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : sortMap.entrySet()) {
			sb.append(entry.getValue());
			sb.append("|");
		}
		return sb.toString();
	}

	/**
	 * @Description: Map按key进行排序拼接value(无拼接符)
	 * @param map
	 * @return String
	 */
	public static String sortStringByKeyGetValueNoSplice(Map<String, Object> map) {
		if (map == null || map.isEmpty()) {
			return null;
		}
		Map<String, Object> sortMap = new TreeMap<String, Object>(
				new Comparator<String>() {
					@Override
					public int compare(String str1, String str2) {
						return str1.compareTo(str2);
					}
				});
		sortMap.putAll(map);
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : sortMap.entrySet()) {
			sb.append(entry.getValue());
		}
		return sb.toString();
	}

	/**
	 * 移除值为空的数据项
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, String> removeNull(Map<String, String> map) {

		Map<String, String> resultMap = new HashMap<String, String>();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (entry.getValue() != null && !"".equals(entry.getValue())) {
				resultMap.put(entry.getKey(), entry.getValue());
			}
		}
		return resultMap;
	}

	/**
	 * 移除值为空的数据项
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, Object> removeNullObject(Map<String, Object> map) {

		Map<String, Object> resultMap = new HashMap<String, Object>();

		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() != null
					&& StringUtils.isNotBlank(String.valueOf(entry.getValue()))) {
				resultMap.put(entry.getKey(), entry.getValue());
			}
		}
		return resultMap;
	}

	/**
	 * 实体类转为MAP
	 * 
	 * @return
	 */
	public static Map<String, Object> getValueMap(Object obj) {

		Map<String, Object> map = new HashMap<String, Object>();

		// 获取f对象对应类中的所有属性域
		Field[] fields = obj.getClass().getDeclaredFields();
		for (int i = 0, len = fields.length; i < len; i++) {
			String varName = fields[i].getName();
			if (!(varName.equals("idPhoto") || varName.equals("sendSuccess"))) {
				try {
					// 获取原来的访问控制权限
					boolean accessFlag = fields[i].isAccessible();
					// 修改访问控制权限
					fields[i].setAccessible(true);
					// 获取在对象f中属性fields[i]对应的对象中的变量
					Object o = fields[i].get(obj);
					if (o != null)
						map.put(varName, o.toString());
					// 恢复访问控制权限
					fields[i].setAccessible(accessFlag);
				} catch (IllegalArgumentException ex) {
					ex.printStackTrace();
				} catch (IllegalAccessException ex) {
					ex.printStackTrace();
				}
			}
		}
		return map;
	}

	public static String createAutoFormHtml(String reqUrl,
			Map<String, String> hiddens, String encoding) {
		StringBuffer sf = new StringBuffer();
		sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset="
				+ encoding + "\"/></head><body>");
		sf.append("<form id = \"pay_form\" action=\"" + reqUrl
				+ "\" method=\"post\">");
		if (null != hiddens && 0 != hiddens.size()) {
			Set<Map.Entry<String, String>> set = hiddens.entrySet();
			Iterator<Map.Entry<String, String>> it = set.iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> ey = it.next();
				String key = ey.getKey();
				String value = ey.getValue();
				sf.append("<input type=\"hidden\" name=\"" + key + "\" id=\""
						+ key + "\" value=\"" + value + "\"/>");
			}
		}
		sf.append("</form>");
		sf.append("</body>");
		sf.append("<script type=\"text/javascript\">");
		sf.append("document.all.pay_form.submit();");
		sf.append("</script>");
		sf.append("</html>");
		return sf.toString();
	}
}
