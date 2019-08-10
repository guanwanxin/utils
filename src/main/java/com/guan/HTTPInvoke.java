package com.guan;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HTTPInvoke {
	private static final Log log = LogFactory.getLog(HTTPInvoke.class);

	@SuppressWarnings({ })
	public static String sendHttpRequestByGet(String url) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		String content = null;
		try {
			HttpGet httpget = new HttpGet(url);
			log.info("调用接口：" + url);
			Date start = new Date();
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);

			StatusLine status = response.getStatusLine();
			long lon;
			if (200 == status.getStatusCode()) {
				lon = new Date().getTime() - start.getTime();
				log.info("url:" + url + "获取接口数据成功(花" + lon + "毫秒)！！");
			}
			return content;
		} catch (ClientProtocolException e) {
			throw new Exception("接口调用失败", e);
		} catch (IOException e) {
			throw new Exception("接口调用失败", e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String sendHttpRequestByPost(String url,
			Map<String, String> map) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		String content = null;
		try {
			HttpPost httpost = new HttpPost(url);
			Set<Map.Entry<String, String>> s = map.entrySet();
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			Iterator<Map.Entry<String, String>> iter = s.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> m = (Map.Entry) iter.next();
				String key = (String) m.getKey();
				String value = (String) m.getValue();
				nvps.add(new BasicNameValuePair(key, value));
			}
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			new StringEntity(url);
			log.info("调用接口：" + url);
			Date start = new Date();
			HttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			StatusLine status = response.getStatusLine();
			if (200 == status.getStatusCode()) {
				long lon = new Date().getTime() - start.getTime();
				log.info("url:" + url + "获取接口数据成功(花" + lon + "毫秒)！！");
			}
			content = EntityUtils.toString(entity);
			log.info("map参数内容为：" + map + "/n接口反馈信息：" + content);
		} catch (UnsupportedEncodingException e) {
			throw new Exception("接口调用失败:", e);
		} catch (ClientProtocolException e) {
			throw new Exception("接口调用失败:", e);
		} catch (IOException e) {
			throw new Exception("接口调用失败:", e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return content;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int sendHttpRequestByPostCheckStatus(String url, String params)
			throws Exception {
		Map<String, String> map = stringToMap(params);
		HttpClient httpclient = new DefaultHttpClient();
		String content = null;
		try {
			HttpPost httpost = new HttpPost(url);
			Set<Map.Entry<String, String>> s = map.entrySet();
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			Iterator<Map.Entry<String, String>> iter = s.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> m = (Map.Entry) iter.next();
				String key = (String) m.getKey();
				String value = (String) m.getValue();
				nvps.add(new BasicNameValuePair(key, value));
			}
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			new StringEntity(url);
			log.info("调用接口：" + url);
			Date start = new Date();

			httpclient.getParams().setParameter("http.connection.timeout",
					Integer.valueOf(5000));
			httpclient.getParams().setParameter("http.socket.timeout",
					Integer.valueOf(30000));
			HttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			StatusLine status = response.getStatusLine();
			long lon;
			if (200 == status.getStatusCode()) {
				lon = new Date().getTime() - start.getTime();
				log.info("url:" + url + "获取接口数据成功(花" + lon + "毫秒)！！");
			}
			content = EntityUtils.toString(entity);
			log.info("map参数内容为：" + map + "/n接口反馈状态为：" + status.getStatusCode());
			return status.getStatusCode();
		} catch (UnsupportedEncodingException e) {
			throw new Exception("接口调用失败:", e);
		} catch (SocketTimeoutException e) {
			log.error("HttpClinet读响应超时(SocketTimeoutException)" + e);
			throw new Exception("SocketTimeoutException:", e);
		} catch (ConnectTimeoutException e) {
			log.error("HttpClinet连接超时(ConnectTimeoutException):" + e);
			throw new Exception("ConnectTimeoutException:", e);
		} catch (ClientProtocolException e) {
			log.info("接口用失调败,接口反馈信息：" + content);
			throw new Exception("接口调用失败:", e);
		} catch (IOException e) {
			log.info("接口用失调败,接口反馈信息：" + content);
			throw new Exception("接口用失调败:", e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
	}

	public static String sendHttpRequestByPostWithoutEncoder(String url,
			String params, String requestMeth) throws Exception {
		Map<String, String> urlMap = MatcherData.stringToMap(params);
		String redirectUrl = sendHttpRequestByPostWithoutEncoder(url, urlMap);
		return redirectUrl;
	}

	public static String sendHttpRequestByPostWithoutEncoder(String url,
			String params) throws Exception {
		Map<String, String> urlMap = MatcherData.stringToMap(params);
		String redirectUrl = sendHttpRequestByPostWithoutEncoder(url, urlMap);
		return redirectUrl;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String sendHttpRequestByPostWithoutEncoder(String url,
			Map<String, String> map) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		String content = null;
		String redirectUrl = "";
		try {
			HttpPost httpost = new HttpPost(url);
			Set<Map.Entry<String, String>> s = map.entrySet();
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			Iterator<Map.Entry<String, String>> iter = s.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> m = (Map.Entry) iter.next();
				String key = (String) m.getKey();
				String value = (String) m.getValue();
				System.out.println("key=" + key + ";value=" + value);
				nvps.add(new BasicNameValuePair(key, value));
			}
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			new StringEntity(url);
			log.info("调用接口：" + url);
			Date start = new Date();
			HttpResponse response = httpclient.execute(httpost);
			HttpEntity entity = response.getEntity();
			StatusLine status = response.getStatusLine();
			if (200 == status.getStatusCode()) {
				long lon = new Date().getTime() - start.getTime();
				log.info("url:" + url + "获取接口数据成功(花" + lon + "毫秒)！！");
			}

			content = EntityUtils.toString(entity);
			log.info("map参数内容为：" + map + "/n接口反馈信息：" + content);
			redirectUrl = content;
		} catch (Exception e) {
			throw new Exception("接口调用失败:", e);
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		return redirectUrl;
	}

	public static String sendHttpRequestByPost(String url, String params)
			throws Exception {
		log.info("调用接口url：" + url + "\n\r调用参数：" + params);
		return sendHttpRequestByPost(url, stringToMap(params));
	}

	public static Map<String, String> stringToMap(String params)
			throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(params)) {
			Matcher m = MatcherData.getMatcher(params,
					"^(.*?\\=.*?)(&(.*?\\=.*?))*", false);
			if (m.matches()) {
				String[] pss = params.split("&");
				for (String p : pss) {
					String[] kv = p.split("=");
					if (kv.length == 0)
						throw new Exception("参数格式不正确！");
					if (kv.length == 1) {
						param.put(kv[0], null);
					} else if (kv.length == 2) {
						log.info("kv:" + kv[1]);
						if (StringUtils.isEmpty(kv[0])) {
							throw new Exception("数据格式不正确！");
						}
						param.put(kv[0], kv[1]);
					} else {
						throw new Exception("数据格式不正确！");
					}
				}
			} else {
				throw new Exception("参数格式不正确！");
			}
		} else {
			log.info("http接口请求方法调用参数为空！");
		}
		return param;
	}

	@SuppressWarnings({ "unused" })
	private static void get(String url) {
		HttpClient httpclient = new DefaultHttpClient();

		try {
			HttpGet httpget = new HttpGet(url);

			System.out.println("executing request " + httpget.getURI());

			HttpResponse response = httpclient.execute(httpget);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200) {

				HttpEntity entity = response.getEntity();

				if (entity != null) {

					System.out.println("Response content length: "
							+ entity.getContentLength());

					System.out.println("Response content: "
							+ EntityUtils.toString(entity));
				}

			}

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			httpclient.getConnectionManager().shutdown();
		}
	}

	public static String sendHttpRequestByFormPost(String url, String params,
			String title) throws Exception {
		Map<String, String> sPara = stringToMap(params);
		List<String> keys = new ArrayList<String>(sPara.keySet());
		StringBuffer sbHtml = new StringBuffer();
		sbHtml.append(title + "......");
		sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\"  enctype=\"multipart/form-data\" action=\""
				+ url + "\" method=\"post\">");

		for (int i = 0; i < keys.size(); i++) {
			String name = (String) keys.get(i);
			String value = (String) sPara.get(name);
			sbHtml.append("<input type=\"hidden\" name=\"" + name
					+ "\" value=\"" + value + "\"/>");
		}

		sbHtml.append("</form>");
		sbHtml.append("<script>document.forms['alipaysubmit'].submit();</script>");
		System.out.println("主干发送到适配器form " + sbHtml.toString());
		return sbHtml.toString();
	}

	public static String syncNotifyBusinessPlatForm(String url, String params)
			throws Exception {
		Map<String, String> sPara = stringToMap(params);

		List<String> keys = new ArrayList<String>(sPara.keySet());
		StringBuffer sbHtml = new StringBuffer();
		sbHtml.append("<span id = \"showMsg\">支付结果确认中。。。（倒计时请耐心等待<span  style = \"color:red\" id = \"countdown\">10</span>秒)</span><div  id = 'closeWindow' align='center' style ='display:none'><a href='#'  onclick=\"window.open('', '_self', '');window.opener=null;window.close();\"\" >关闭窗口</a></div>");
		sbHtml.append("<form id=\"alipaysubmit\" name=\"alipaysubmit\"  enctype=\"multipart/form-data\" action=\""
				+ url + "\" method=\"post\">");
		for (int i = 0; i < keys.size(); i++) {
			String name = (String) keys.get(i);
			String value = (String) sPara.get(name);
			sbHtml.append("<input type=\"hidden\" name=\"" + name
					+ "\" value=\"" + value + "\"/>");
		}

		sbHtml.append("</form>");
		sbHtml.append(" <script>");
		sbHtml.append(" var submit = 10 ; ");
		sbHtml.append(" var timer =  window.setInterval(\"countdown();\", 1000);");
		sbHtml.append(" function countdown(){ ");
		sbHtml.append(" \t var s = document.getElementById(\"countdown\"); ");

		sbHtml.append(" \t if(submit <= 0){ ");
		sbHtml.append("  \t\t document.forms['alipaysubmit'].submit();  ");
		sbHtml.append("  \t\t clearInterval(timer);  ");
		sbHtml.append(" \t \t var showMsg = document.getElementById(\"showMsg\"); ");
		sbHtml.append(" \t \t showMsg.innerHTML =\" 正在跳转商户页面，请稍后！\"; ");

		sbHtml.append("  \t\t return false;  }");
		sbHtml.append("  \t s.innerHTML = submit--; }");
		sbHtml.append(" </script>");
		return sbHtml.toString();
	}
}
