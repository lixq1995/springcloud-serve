package com.test.common.util.http;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import lombok.extern.slf4j.Slf4j;


/**
 * @author by Lixq
 * @Classname HttpsTrustManager
 * @Description TODO
 * @Date 2021/4/5 21:48
 */
@Slf4j
public class HttpsTrustManager implements X509TrustManager {

  private X509TrustManager sunX509TrustManager = null;

  public HttpsTrustManager(KeyStore keyStore) {
    if (keyStore == null) {
      return;
    }
    try {
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
      trustManagerFactory.init(keyStore);
      TrustManager[] tms = trustManagerFactory.getTrustManagers();
      for (TrustManager tm : tms) {
        if (tm instanceof X509TrustManager) {
          this.sunX509TrustManager = (X509TrustManager)tm;
          return;
        }
      }
    } catch (NoSuchAlgorithmException e) {
      log.error(e.getMessage());
    } catch (KeyStoreException e) {
      log.error(e.getMessage());
    } catch (NoSuchProviderException e) {
      log.error(e.getMessage());
    }
  }

  public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    if (this.sunX509TrustManager == null)
      return;
    this.sunX509TrustManager.checkClientTrusted(x509Certificates, s);
  }

  public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
    if (this.sunX509TrustManager == null)
      return;
    this.sunX509TrustManager.checkServerTrusted(x509Certificates, s);
  }

  public X509Certificate[] getAcceptedIssuers() {
    if (this.sunX509TrustManager == null)
      return new X509Certificate[0];
    return this.sunX509TrustManager.getAcceptedIssuers();
  }
}
