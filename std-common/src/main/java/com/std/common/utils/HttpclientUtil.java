package com.std.common.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.exception.NetServiceException;

public class HttpclientUtil {
	private static final Logger logger = LoggerFactory.getLogger(HttpclientUtil.class);
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
	private static final int CONNECTION_TIMEOUT = 30000; // 连接超时时间
	private static final int CONNECTION_REQUEST_TIMEOUT = 30000; // 请求超时时间
	private static final int SOCKET_TIMEOUT = 60000; // 数据读取等待超时
	private static final String HTTPS = "https"; // https
	private static final int DEFAULT_HTTP_PORT = 80; // http端口
	private static final int DEFAULT_HTTPS_PORT = 443; // https端口
	private static final String DEFAULT_ENCODING = "UTF-8"; // 默认编码
	private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded"; //默认内容类型
	private static final String SSL_CERT_TYPE_PKCS12 = "PKCS12"; //证书类型
	
	// 请求配置
	private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(CONNECTION_TIMEOUT)
			.setSocketTimeout(SOCKET_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
			.setRedirectsEnabled(true).setRelativeRedirectsAllowed(true)
			.setCircularRedirectsAllowed(true)
			.setExpectContinueEnabled(false).build();
	
	// 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
	private static HttpRequestRetryHandler REQUEST_RETRY_HANDLER = new HttpRequestRetryHandler() {
		// 自定义的恢复策略
		public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
			// 设置恢复策略，在发生异常时候将自动重试3次
			if (executionCount >= 3) {
				// Do not retry if over max retry count
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// Retry if the server dropped connection on us
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// Do not retry on SSL handshake exception
				return false;
			}
			HttpRequest request = (HttpRequest) context.getAttribute(HttpCoreContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// Retry if the request is considered idempotent
				return true;
			}
			return false;
		}
	};
	
	/**
	 * 获取DefaultHttpClient实例
	 * @param charset
	 *            参数编码集, 可空
	 * @return DefaultHttpClient 对象
	 */
	private static CloseableHttpClient getCloseableHttpClient(final String charset){
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE)
				.setCharset(Charset.forName(StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset)).build();
		SchemePortResolver schemePortResolver = new SchemePortResolver() {
			@Override
			public int resolve(HttpHost httpHost) throws UnsupportedSchemeException {
				return DEFAULT_HTTP_PORT;
			}
		};
		return HttpClientBuilder
				.create()
				.setRetryHandler(REQUEST_RETRY_HANDLER)
				.setDefaultConnectionConfig(connectionConfig)
				.setUserAgent(USER_AGENT)
				.setDefaultRequestConfig(REQUEST_CONFIG)
				.setSchemePortResolver(schemePortResolver)
				.build();
	}
	
	/**
	 * 获取DefaultHttpClient实例, 忽略掉对服务器端证书的验证, 服务端不要求验证客户端证书
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	private static CloseableHttpClient getTrustedSSLCloseableHttpClient(final String charset) throws Exception {
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
			// 默认信任所有证书
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		}).build();
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE)
				.setCharset(Charset.forName(StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset))
				.build();
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
		SchemePortResolver schemePortResolver = new SchemePortResolver() {
			@Override
			public int resolve(HttpHost httpHost) throws UnsupportedSchemeException {
				return DEFAULT_HTTPS_PORT;
			}
		};
		return HttpClientBuilder
				.create()
				.setDefaultConnectionConfig(connectionConfig)
				.setUserAgent(USER_AGENT)
				.setDefaultRequestConfig(REQUEST_CONFIG)
				.setSSLSocketFactory(sslConnectionSocketFactory)
				.setSchemePortResolver(schemePortResolver)
				.build();
	}
	
	/**
	 * 获取DefaultHttpClient实例, 使用本地SSL证书发送到服务端， 服务端需要用来验证证书有效性；忽略掉对服务器端证书的校验
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	private static CloseableHttpClient getSSLPKCS12CloseableHttpClient(final String keyStorePath,final String password, final String charset) throws Exception {
		KeyStore keyStore = loadKeyStore(new URL(keyStorePath), password, SSL_CERT_TYPE_PKCS12);
        SSLContext sslContext = SSLContexts.custom()
        		.loadTrustMaterial(null, new TrustStrategy() {
        			// 默认信任所有证书
        			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        				return true;
        			}
        		})  //信任策略，不对服务器端的证书进行校验
        		.loadKeyMaterial(keyStore, password.toCharArray()) // 加载客户端证书
        		.build();
		ConnectionConfig connectionConfig = ConnectionConfig.custom()
				.setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE)
				.setCharset(Charset.forName(StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset))
				.build();
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);
		SchemePortResolver schemePortResolver = new SchemePortResolver() {
			@Override
			public int resolve(HttpHost httpHost) throws UnsupportedSchemeException {
				return DEFAULT_HTTPS_PORT;
			}
		};
		return HttpClientBuilder
				.create()
				.setDefaultConnectionConfig(connectionConfig)
				.setUserAgent(USER_AGENT)
				.setDefaultRequestConfig(REQUEST_CONFIG)
				.setSSLSocketFactory(sslConnectionSocketFactory)
				.setSchemePortResolver(schemePortResolver)
				.build();
	}
	
	/**
	 * 从给定的路径中加载此 KeyStore
	 * @param url
	 *            keystore URL路径
	 * @param password
	 *            keystore访问密钥
	 * @return keystore 对象
	 */
	private static KeyStore loadKeyStore(final URL url, final String password, final String certType) throws Exception {
		if (url == null)
			throw new IllegalArgumentException("Keystore url may not be null");
		KeyStore keystore = KeyStore.getInstance(StringUtils.isEmpty(certType)?KeyStore.getDefaultType():certType);
		InputStream is = null;
		try {
			is = url.openStream();
			keystore.load(is, password != null ? password.toCharArray() : null);
		} finally {
			if (is != null) {
				is.close();
				is = null;
			}
		}
		return keystore;
	}
	
	/**
	 * 释放资源
	 * @param httpClient
	 * @param response
	 */
	private static void closeResource(CloseableHttpClient httpClient, CloseableHttpResponse response) {
		if (response != null) {
			try {
				response.close();
			} catch (IOException e) {
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
		
		if (httpClient != null) {
			try {
				httpClient.close();
			} catch (Exception e) {
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
		}
	}
	
	/**
	 * 将传入的键/值对参数转换为NameValuePair参数集
	 * @param paramsMap
	 *            参数集, 键/值对
	 * @return NameValuePair参数集
	 */
	private static List<NameValuePair> getNameValuePairList(Map<String, String> params) {
		if (MapUtils.isEmpty(params)) {
			return null;
		}
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return nameValuePairs;
	}
	
	/**
	 * 处理响应，获取响应报文
	 * @param url
	 * @param encoding
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private static String handleResponse(String url, String charset, CloseableHttpResponse response) throws Exception {
		String result = null;
		if (response != null) {
			// 获取响应实体
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				charset = ( ContentType.get(entity) == null || ContentType.get(entity).getCharset() == null ) ? (StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset) : ContentType.get(entity).getCharset().toString();
				result = EntityUtils.toString(entity, charset);
			}
			// 释放entity
			EntityUtils.consume(entity);
		}
		return result;
	}
	
	/**
	 * GET 请求 ， params,charset可选 charset为null时设置为默认字符编码
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String get(String url, Map<String, String> params, String charset) {
		String result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			List<NameValuePair> nameValuePairs = getNameValuePairList(params);
			
			if(url.toLowerCase().startsWith(HTTPS)) {
				httpClient = getTrustedSSLCloseableHttpClient(charset);
			}else {
				httpClient = getCloseableHttpClient(charset);
			}
			
			HttpGet httpGet = null;
			if(CollectionUtils.isNotEmpty(nameValuePairs)) {
				URIBuilder builder = new URIBuilder(url);
			    builder.setParameters(nameValuePairs);
			    builder.setCharset(Charset.forName(StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset));
			    httpGet = new HttpGet(builder.build());
			}else {
				httpGet = new HttpGet(url);
			}
			
	        // 发送请求，并接收响应
			response = httpClient.execute(httpGet);
			result = handleResponse(url, charset, response);
		} catch (Exception e) {
			throw new NetServiceException(e);
		} finally {
			closeResource(httpClient, response);
		}
		return result;
	}
	
	/**
	 * GET 请求 ， params,charset可选 charset为null时设置为默认字符编码
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String get(String url, Map<String, String> params, String messageBody, String charset) {
		String result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			List<NameValuePair> nameValuePairs = getNameValuePairList(params);
			
			if(url.toLowerCase().startsWith(HTTPS)) {
				httpClient = getTrustedSSLCloseableHttpClient(charset);
			}else {
				httpClient = getCloseableHttpClient(charset);
			}
			
			URI uri = null;
			if(CollectionUtils.isNotEmpty(nameValuePairs)) {
				URIBuilder builder = new URIBuilder(url);
			    builder.setParameters(nameValuePairs);
			    builder.setCharset(Charset.forName(StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset));
			    uri = builder.build();
			}else {
				uri = URI.create(url);
			}
			RequestBuilder requestBuilder = RequestBuilder.get(uri);
			
			StringEntity stringEntity = null;
			if(StringUtils.isNotEmpty(messageBody))
				stringEntity = new StringEntity(messageBody, StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset);
			if(null!=stringEntity) {
				requestBuilder.setEntity(stringEntity);
			}
			
	        // 发送请求，并接收响应
			response = httpClient.execute(requestBuilder.build());
			result = handleResponse(url, charset, response);
		} catch (Exception e) {
			throw new NetServiceException(e);
		} finally {
			closeResource(httpClient, response);
		}
		return result;
	}
	
	/**
	 * GET 请求 ， params,charset可选 charset为null时设置为默认字符编码, 服务端要证书验证
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String getSSLPKCS12(String url, Map<String, String> params, String messageBody, String keyStorePath, String password, String charset) {
		String result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			List<NameValuePair> nameValuePairs = getNameValuePairList(params);
			
			if(url.toLowerCase().startsWith(HTTPS)) {
				httpClient = getSSLPKCS12CloseableHttpClient(keyStorePath, password, charset);
			}else {
				throw new NetServiceException("请求类型URL必须为https！");
			}
			
			URI uri = null;
			if(CollectionUtils.isNotEmpty(nameValuePairs)) {
				URIBuilder builder = new URIBuilder(url);
			    builder.setParameters(nameValuePairs);
			    builder.setCharset(Charset.forName(StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset));
			    uri = builder.build();
			}else {
				uri = URI.create(url);
			}
			RequestBuilder requestBuilder = RequestBuilder.get(uri);
			
			StringEntity stringEntity = null;
			if(StringUtils.isNotEmpty(messageBody))
				stringEntity = new StringEntity(messageBody, StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset);
			if(null!=stringEntity) {
				requestBuilder.setEntity(stringEntity);
			}
			
	        // 发送请求，并接收响应
			response = httpClient.execute(requestBuilder.build());
			result = handleResponse(url, charset, response);
		} catch (Exception e) {
			throw new NetServiceException(e);
		} finally {
			closeResource(httpClient, response);
		}
		return result;
	}
	
	/**
	 * post 请求 ， params,charset可选 charset为null时设置为默认字符编码
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String post(String url, Object params, String charset) {
		if (params instanceof String)
			return postWithString(url, (String)params, charset);
		else
			return postWithUrlEncodedForm(url, (null==params?null:(Map<String, String>)params), charset);
	}
	
	/**
	 * multipart post 请求，附文件上传
	 * @param url
	 * @param params
	 * @param files
	 * @param charset
	 * @return
	 */
	public static String post(String url, Map<String, String> params, Map<String, File> files, String charset) {
		return postWithMultipartFormData(url, params, files, charset);
	}
	
	/**
	 * multipart post 请求
	 * @param url
	 * @param params
	 * @param files
	 * @param charset
	 * @return
	 */
	private static String postWithMultipartFormData(String url, Map<String, String> params, Map<String, File> files, String charset) {
		String result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			multipartEntityBuilder.setCharset(Charset.forName(StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset));
			
			// 文本参数
			if (MapUtils.isNotEmpty(params)) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					multipartEntityBuilder.addPart(entry.getKey(), new StringBody(entry.getValue(), ContentType.MULTIPART_FORM_DATA));
				}
			}
			// 文件
			if (MapUtils.isNotEmpty(files)) {
				for (Map.Entry<String, File> entry : files.entrySet()) {
					multipartEntityBuilder.addPart(entry.getKey(), new FileBody(entry.getValue()));
				}
			}
			
			if(url.toLowerCase().startsWith(HTTPS)) {
				httpClient = getTrustedSSLCloseableHttpClient(charset);
			}else {
				httpClient = getCloseableHttpClient(charset);
			}
			
			HttpPost httpPost = new HttpPost(url);
			HttpEntity httpEntity = multipartEntityBuilder.build();
			httpPost.setEntity(httpEntity);
			
	        // 发送请求，并接收响应
			response = httpClient.execute(httpPost);
			result = handleResponse(url, charset, response);
		} catch (Exception e) {
			throw new NetServiceException(e);
		} finally {
			closeResource(httpClient, response);
		}
		return result;
	}
	
	/**
	 * post 请求 ， params,charset可选 charset为null时设置为默认字符编码
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	private static String postWithUrlEncodedForm(String url, Map<String, String> params, String charset) {
		String result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			List<NameValuePair> nameValuePairs = getNameValuePairList(params);
			UrlEncodedFormEntity formEntity = null;
			if(CollectionUtils.isNotEmpty(nameValuePairs))
				formEntity = new UrlEncodedFormEntity(nameValuePairs, StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset);
			
			if(url.toLowerCase().startsWith(HTTPS)) {
				httpClient = getTrustedSSLCloseableHttpClient(charset);
			}else {
				httpClient = getCloseableHttpClient(charset);
			}
			
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-type", DEFAULT_CONTENT_TYPE);
			if(null!=formEntity)
				httpPost.setEntity(formEntity);
			
	        // 发送请求，并接收响应
			response = httpClient.execute(httpPost);
			result = handleResponse(url, charset, response);
		} catch (Exception e) {
			throw new NetServiceException(e);
		} finally {
			closeResource(httpClient, response);
		}
		return result;
	}
	
	/**
	 * post 请求 ， params,charset可选 charset为null时设置为默认字符编码
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	private static String postWithString(String url, String params, String charset) {
		String result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			StringEntity stringEntity = null;
			if(StringUtils.isNotEmpty(params))
				stringEntity = new StringEntity(params, StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset);
			
			if(url.toLowerCase().startsWith(HTTPS)) {
				httpClient = getTrustedSSLCloseableHttpClient(charset);
			}else {
				httpClient = getCloseableHttpClient(charset);
			}
			
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-type", DEFAULT_CONTENT_TYPE);
			if(null!=stringEntity)
				httpPost.setEntity(stringEntity);
			
	        // 发送请求，并接收响应
			response = httpClient.execute(httpPost);
			result = handleResponse(url, charset, response);
		} catch (Exception e) {
			throw new NetServiceException(e);
		} finally {
			closeResource(httpClient, response);
		}
		return result;
	}
	
	/**
	 * https post 请求 ， params,charset可选 charset为null时设置为默认字符编码,PKCS12类型证书
	 * @param url
	 * @param params
	 * @param charset
	 * @return
	 */
	public static String postSSLPKCS12(String url, Object params, String keyStorePath, String password, String charset) {
		if (params instanceof String)
			return postSSLPKCS12WithString(url, (String)params, keyStorePath, password, charset);
		else
			return postSSLPKCS12WithUrlEncodedForm(url, (null==params?null:(Map<String, String>)params), keyStorePath, password, charset);
	}
	
	/**
	 * https post 请求 ， params,charset可选 charset为null时设置为默认字符编码,PKCS12类型证书
	 * @param url
	 * @param params
	 * @param keyStorePath : linux [ file:/cert/xxx.p12 ] ; windows [ C://cert/xxx.p12 ]
	 * @param password
	 * @param charset
	 * @return
	 */
	private static String postSSLPKCS12WithUrlEncodedForm(String url, Map<String, String> params, String keyStorePath, String password, String charset) {
		String result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			List<NameValuePair> nameValuePairs = getNameValuePairList(params);
			UrlEncodedFormEntity formEntity = null;
			if(CollectionUtils.isNotEmpty(nameValuePairs))
				formEntity = new UrlEncodedFormEntity(nameValuePairs, StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset);
			
			if(url.toLowerCase().startsWith(HTTPS)) {
				httpClient = getSSLPKCS12CloseableHttpClient(keyStorePath, password, charset);
			}else {
				throw new NetServiceException("请求类型URL必须为https！");
			}
			
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Content-type", DEFAULT_CONTENT_TYPE);
			if(null!=formEntity)
				httpPost.setEntity(formEntity);
			
	        // 发送请求，并接收响应
			response = httpClient.execute(httpPost);
			result = handleResponse(url, charset, response);
		} catch (Exception e) {
			throw new NetServiceException(e);
		} finally {
			closeResource(httpClient, response);
		}
		return result;
	}
	
	/**
	 * https post 请求 ， params,charset可选 charset为null时设置为默认字符编码,PKCS12类型证书
	 * @param url
	 * @param params
	 * @param keyStorePath : linux [ file:/cert/xxx.p12 ] ; windows [ C://cert/xxx.p12 ]
	 * @param password
	 * @param charset
	 * @return
	 */
	private static String postSSLPKCS12WithString(String url, String params, String keyStorePath, String password, String charset) {
		String result = null;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			if (StringUtils.isBlank(url)) {
				return null;
			}
			StringEntity stringEntity = null;
			if(StringUtils.isNotEmpty(params))
				stringEntity = new StringEntity(params, StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset);
			
			if(url.toLowerCase().startsWith(HTTPS)) {
				httpClient = getSSLPKCS12CloseableHttpClient(keyStorePath, password, charset);
			}else {
				throw new NetServiceException("请求类型URL必须为https！");
			}
			
			HttpPost httpPost = new HttpPost(url);
			if(null!=stringEntity)
				httpPost.setEntity(stringEntity);
			
	        // 发送请求，并接收响应
			response = httpClient.execute(httpPost);
			result = handleResponse(url, charset, response);
		} catch (Exception e) {
			throw new NetServiceException(e);
		} finally {
			closeResource(httpClient, response);
		}
		return result;
	}
	
	/**
	 * 文件下载
	 * @param url
	 * @param params
	 * @param messageBody
	 * @param filePath
	 * @param charset
	 * @return true : 成功； false : 失败
	 */
	public static boolean download(String url, Map<String, String> params, String messageBody, String filePath, String charset) {
		boolean result = false;
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response = null;
		try {
			if (StringUtils.isBlank(url)) {
				return result;
			}
			List<NameValuePair> nameValuePairs = getNameValuePairList(params);
			
			if(url.toLowerCase().startsWith(HTTPS)) {
				httpClient = getTrustedSSLCloseableHttpClient(charset);
			}else {
				httpClient = getCloseableHttpClient(charset);
			}
			
			URI uri = null;
			if(CollectionUtils.isNotEmpty(nameValuePairs)) {
				URIBuilder builder = new URIBuilder(url);
			    builder.setParameters(nameValuePairs);
			    builder.setCharset(Charset.forName(StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset));
			    uri = builder.build();
			}else {
				uri = URI.create(url);
			}
			RequestBuilder requestBuilder = RequestBuilder.get(uri);
			
			StringEntity stringEntity = null;
			if(StringUtils.isNotEmpty(messageBody))
				stringEntity = new StringEntity(messageBody, StringUtils.isEmpty(charset)?DEFAULT_ENCODING:charset);
			if(null!=stringEntity) {
				requestBuilder.setEntity(stringEntity);
			}
			
	        // 发送请求，并接收响应
			response = httpClient.execute(requestBuilder.build());

			if (response != null) {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() == 200) {
						File file = new File(filePath);
						FileUtils.writeByteArrayToFile(file, EntityUtils.toByteArray(response.getEntity()));
						result = true;
					}
				}
				// 释放entity
				EntityUtils.consume(entity);
			}
			
		} catch (Exception e) {
			throw new NetServiceException(e);
		} finally {
			closeResource(httpClient, response);
		}
		return result;
	}
	
	public static void main(String[] args) {
		
		long ts = System.currentTimeMillis();
		String url = "http://jishi.woniu.com:8888/9yin/loadServerList.do?";
		Map<String, String> params = new HashMap<>();
		params.put("gameId", "10");
		String charset = null; 
//		logger.info(get(url,params, "{\"gameId\":10}", charset));
		download("https://legacy.gitbook.com/download/pdf/book/renjun1004/netty-in-action-", null, null, "E:\\test\\netty-in-action-.pdf", charset);
		logger.info("time {} used.", System.currentTimeMillis()-ts);
	}
}
