package org.lodder.subtools.sublibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.lodder.subtools.sublibrary.cache.DiskCache;
import org.lodder.subtools.sublibrary.cache.InMemoryCache;
import org.lodder.subtools.sublibrary.util.http.HttpClient;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.lodder.subtools.sublibrary.util.http.HttpClientSetupException;

public class Manager {

  private HttpClient httpClient;
  private InMemoryCache<String, String> inMemoryCache;
  private DiskCache<String, String> diskCache;

  public Manager() {

  }

  public String downloadText(String urlString) throws ManagerException {
    URL url;
    try {
      url = new URL(urlString);
      return httpClient.downloadText(url);
    } catch (MalformedURLException e) {
      throw new ManagerException("incorrect url", e);
    } catch (IOException e) {
      throw new ManagerException(e);
    }
  }

  public InputStream getContentStream(String urlString, String userAgent, boolean longTermCache)
      throws ManagerSetupException, ManagerException {
    return IOUtils.toInputStream(getContent(urlString, userAgent, longTermCache));
  }

  public String getContent(String urlString, String userAgent, boolean longTermCache)
      throws ManagerSetupException, ManagerException {
    validate();

    String content = null;
    URL url;

    try {
      if (longTermCache) {
        content = diskCache.get(urlString);
      } else {
        content = inMemoryCache.get(urlString);
      }
      if (content == null) {
        url = new URL(urlString);
        content = httpClient.doGet(url, userAgent);
        if (longTermCache) {
          diskCache.put(urlString, content);
        } else {
          inMemoryCache.put(urlString, content);
        }
      }
    } catch (MalformedURLException e) {
      throw new ManagerException("incorrect url", e);
    } catch (IOException e) {
      throw new ManagerException(e);
    } catch (HttpClientException e) {
      throw new ManagerException("Error occured with httpclient response: " + e.getResponseCode()
          + " " + e.getResponseMessage());
    } catch (HttpClientSetupException e) {
      throw new ManagerException(e);
    }

    return content;
  }

  public boolean store(String downloadlink, File file) throws ManagerException {
    URL url;
    try {
      url = new URL(downloadlink);
      return httpClient.doDownloadFile(url, file);
    } catch (MalformedURLException e) {
      throw new ManagerException("incorrect url", e);
    }
  }


  public String post(String urlString, String userAgent, Map<String, String> data)
      throws ManagerException {
    URL url;
    try {
      url = new URL(urlString);
      return httpClient.doPost(url, userAgent, data);
    } catch (MalformedURLException e) {
      throw new ManagerException("incorrect url", e);
    } catch (HttpClientSetupException e) {
      throw new ManagerException(e);
    } catch (HttpClientException e) {
      throw new ManagerException(e);
    }

  }

  public void setHttpClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public void setInMemoryCache(InMemoryCache<String, String> inMemoryCache) {
    this.inMemoryCache = inMemoryCache;
  }

  public void setDiskCache(DiskCache<String, String> diskCache) {
    this.diskCache = diskCache;
  }

  private void validate() throws ManagerSetupException {
    if (httpClient == null) throw new ManagerSetupException("HttpClient is not initialized");
    if (inMemoryCache == null) throw new ManagerSetupException("InMemoryCache is not initialized");
    if (diskCache == null) throw new ManagerSetupException("DiskCache is not initialized");
  }

  public void removeCacheObject(String url) throws ManagerSetupException {
    validate();
    inMemoryCache.remove(url);
    diskCache.remove(url);
  }

  public boolean isCached(String url) {
    if (inMemoryCache.exists(url))
      return true;
    else
      return diskCache.exists(url);
  }
}
