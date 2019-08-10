package com.guan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HTTPSInvoke {
	private static final Log log = LogFactory.getLog(HTTPSInvoke.class);

	@SuppressWarnings("deprecation")
	public static String sendHttpsRequestByPost(String url,
			Map<String, String> params) throws Exception {
		String responseContent = null;
		HttpClient httpClient = new DefaultHttpClient();

		X509TrustManager xtm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

		};
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return false;
			}

			public void verify(String arg0, SSLSocket arg1) throws IOException {
			}

			public void verify(String arg0, X509Certificate arg1)
					throws SSLException {
			}

			public void verify(String arg0, String[] arg1, String[] arg2)
					throws SSLException {
			}
		};
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");

			ctx.init(null, new TrustManager[] { xtm }, null);

			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);

			socketFactory.setHostnameVerifier(hostnameVerifier);

			httpClient.getConnectionManager().getSchemeRegistry()
					.register(new Scheme("https", socketFactory, 443));
			log.info("调用接口:" + url);
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();

			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair((String) entry.getKey(),
						(String) entry.getValue()));
			}

			httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				responseContent = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (KeyManagementException e) {
			throw new Exception("接口调用失败:", e);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("接口调用失败:", e);
		} catch (UnsupportedEncodingException e) {
			throw new Exception("接口调用失败:", e);
		} catch (ClientProtocolException e) {
			throw new Exception("接口调用异常:", e);
		} catch (ParseException e) {
			throw new Exception("接口调用失败：", e);
		} catch (IOException e) {
			throw new Exception("接口调用失败：", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return responseContent;
	}

	public static String sendHttpsRequestByPost(String url, String params)
			throws Exception {
		return sendHttpsRequestByPost(url, stringToMap(params));
	}

	private static Map<String, String> stringToMap(String params)
			throws Exception {
		Map<String, String> param = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(params)) {
			Matcher m = MatcherData.getMatcher(params,
					"^(.*?\\=.*?)(&(.*?\\=.*?))*", false);
			if (m.matches()) {
				String[] pss = params.split("&");
				for (String p : pss) {
					String[] kv = p.split("=");
					if (kv.length == 0) {
						throw new Exception("参数格式不正确！");
					}
					if (kv.length == 1) {
						param.put(kv[0], null);
					}
					if ((StringUtils.isNotEmpty(kv[1]))
							&& (StringUtils.isEmpty(kv[0]))) {

						throw new Exception("数据格式不正确！");
					}
					param.put(kv[0], kv[1]);
				}
			} else {
				throw new Exception("参数格式不正确！");
			}
		} else {
			log.info("http接口post方法调用参数为空！");
		}
		return param;
	}

	@SuppressWarnings("deprecation")
	public static String sendHTTPSGetByCert(String path, String url,
			String password) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();

		String content = "";
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			FileInputStream instream = new FileInputStream(new File(path));

			trustStore.load(instream, password.toCharArray());

			SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);

			socketFactory
					.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme sch = new Scheme("https", 8443, socketFactory);
			httpclient.getConnectionManager().getSchemeRegistry().register(sch);

			HttpGet httpGet = null;
			httpGet = new HttpGet(url);

			HttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = EntityUtils.toString(entity);
			}
		} catch (KeyStoreException e) {
			throw new Exception("接口调用失败:", e);
		} catch (FileNotFoundException e) {
			throw new Exception("接口调用失败:", e);
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("接口调用失败:", e);
		} catch (CertificateException e) {
			throw new Exception("接口调用失败:", e);
		} catch (IOException e) {
			throw new Exception("接口调用失败:", e);
		} catch (KeyManagementException e) {
			throw new Exception("接口调用失败:", e);
		} catch (UnrecoverableKeyException e) {
			throw new Exception("接口调用失败:", e);
		}
		return content;
	}

	public static void main(String[] args) throws Exception {
		sendHttpsRequestByPost(
				"https://szap-gw.shenzhenair.com/szairpay/adapter/adapter-Query-queryString.action",
				"begin_time=2013-10-12+11%3A44%3A44&end_time=2013-10-12+17%3A46%3A33&input_charset=UTF-8&notify_url=http%3A%2F%2Fb2atest.shenzhenair.com%2Fb2a%2Fservlet%2Fb2a%2FreturnTicketServlet&order_no=RF201310120000000001&order_time=2013-10-12+11%3A44%3A44&partner=2088011652922864&pay_bank_code=alipay&return_url=http%3A%2F%2Fb2atest.shenzhenair.com%2Fb2a%2Fservlet%2Fb2a%2FreturnTicketServlet&sign=28e2df0eed2a69a7857bed2c510a7bb0&sign_type=MD5&trans_type=303");
	}
}
