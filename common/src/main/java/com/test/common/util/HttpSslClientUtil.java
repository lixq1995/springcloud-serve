package com.test.common.util;

import com.google.gson.Gson;
import com.google.json.JsonSanitizer;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

@Slf4j
public class HttpSslClientUtil {

  private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(25000).setConnectTimeout(25000).build();

  private static final String DEFAULT_CONTENT_TYPE = "application/json";

  private static final String DEFAULT_CHARSET_UTF8 = "UTF-8";

  private static final String DEFAULT_CHARSET_UTF8_LOWER = "utf-8";

  private static final int TIMEOUT = 25000;

  public static <Clazz> Clazz httpPost(String url, Map<String, Object> paramMap, Map<String, String> headers, Class<Clazz> returnObject) {
    String result = httpPost(url, paramMap, headers, "application/json");
    String jsonData = JsonSanitizer.sanitize(result);
    return (Clazz)(new Gson()).fromJson(jsonData, returnObject);
  }

  public static <Clazz> Clazz httpPost(String url, String jsonParam, Map<String, String> headers, Class<Clazz> returnObject) {
    String result = httpPost(url, jsonParam, headers, "application/json");
    String jsonData = JsonSanitizer.sanitize(result);
    return (Clazz)(new Gson()).fromJson(jsonData, returnObject);
  }

  public static String httpPost(String url, String jsonParam, Map<String, String> headers) {
    return httpPost(url, jsonParam, headers, "application/json");
  }

  public static String httpPost(String url, Map<String, Object> paramMap, Map<String, String> headers) {
    return httpPost(url, paramMap, headers, "application/json");
  }

  public static <Clazz> Clazz httpPost(String url, Map<String, Object> paramMap, Map<String, String> headers, Class<Clazz> returnObject, String contentType) {
    String result = httpPost(url, paramMap, headers, contentType);
    String jsonData = JsonSanitizer.sanitize(result);
    return (Clazz)(new Gson()).fromJson(jsonData, returnObject);
  }

  public static <Clazz> Clazz httpPost(String url, String jsonParam, Map<String, String> headers, Class<Clazz> returnObject, String contentType) {
    String result = httpPost(url, jsonParam, headers, contentType);
    String jsonData = JsonSanitizer.sanitize(result);
    return (Clazz)(new Gson()).fromJson(jsonData, returnObject);
  }

  public static String httpPost(String url, Map<String, Object> paramMap, Map<String, String> headers, String contentType) {
    String jsonParam = (new Gson()).toJson(paramMap);
    return httpPost(url, jsonParam, headers, contentType);
  }

  public static String httpPost(String url, String jsonParam, Map<String, String> headers, String contentType) {
    SslClient sslClient = null;
    HttpClient httpClient = null;
    try {
      sslClient = new SslClient();
    } catch (NoSuchAlgorithmException|java.security.KeyManagementException e) {
      log.error(e.getMessage());
    }
    HttpPost httpPost = new HttpPost(url);
    httpPost.setConfig(requestConfig);
    try {
      if (jsonParam != null) {
        StringEntity entity = new StringEntity(jsonParam, "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType(contentType);
        httpPost.setEntity((HttpEntity)entity);
        for (Map.Entry<String, String> header : headers.entrySet()) {
          httpPost.setHeader(header.getKey(), header.getValue());
        }
      }
      if (sslClient == null) {
        return null;
      }
      HttpResponse result = sslClient.execute((HttpUriRequest)httpPost);
      if (result.getStatusLine().getStatusCode() == 200 || result
        .getStatusLine().getStatusCode() == 201) {
        try {
          HttpEntity entity = result.getEntity();
          String returnString = EntityUtils.toString(entity, "utf-8");
          return returnString;
        } catch (IOException e) {
          log.error(e.getMessage());
        }
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    } finally {
      httpPost.releaseConnection();
    }
    return null;
  }

  public static String httpGet(String url, Map<String, String> params, Map<String, String> headers) {
    SslClient sslClient = null;
    HttpClient httpClient = null;
    try {
      sslClient = new SslClient();
    } catch (NoSuchAlgorithmException|java.security.KeyManagementException e) {
      log.error(e.getMessage());
    }
    HttpGet request = new HttpGet(url + paramMapToUrl(params));
    request.setConfig(requestConfig);
    for (Map.Entry<String, String> header : headers.entrySet()) {
      request.setHeader(header.getKey(), header.getValue());
    }
    try {
      if (sslClient != null) {
        HttpResponse response = sslClient.execute((HttpUriRequest)request);
        if (response.getStatusLine().getStatusCode() == 200 || response
          .getStatusLine().getStatusCode() == 201) {
          HttpEntity entity = response.getEntity();
          String returnString = EntityUtils.toString(entity, "utf-8");
          return returnString;
        }
        log.error(url);
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    } finally {
      request.releaseConnection();
    }
    return null;
  }

  private static String paramMapToUrl(Map<String, String> params) {
    StringBuilder sb = new StringBuilder("?");
    for (Map.Entry<String, String> param : params.entrySet()) {
      sb.append((String)param.getKey() + "=" + (String)param.getValue() + "&");
    }
    return sb.toString();
  }
}
