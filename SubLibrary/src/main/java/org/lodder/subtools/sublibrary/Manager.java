package org.lodder.subtools.sublibrary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
        try {
            return httpClient.downloadText(urlString);
        } catch (MalformedURLException e) {
            throw new ManagerException("incorrect url", e);
        } catch (IOException e) {
            throw new ManagerException(e);
        }
    }

    public String downloadText2(String urlString) throws ManagerException {
        try {
            return httpClient.downloadText(urlString);
        } catch (MalformedURLException e) {
            throw new ManagerException("incorrect url", e);
        } catch (IOException e) {
            throw new ManagerException(e);
        }
    }

    public InputStream getContentStream(String urlString, String userAgent, boolean longTermCache)
            throws ManagerSetupException, ManagerException {
        return IOUtils.toInputStream(getContent(urlString, userAgent, longTermCache), StandardCharsets.UTF_8);
    }

    public String getContent(String urlString, String userAgent, boolean longTermCache)
            throws ManagerSetupException, ManagerException {
        validate();

        try {
            InMemoryCache<String, String> cache = longTermCache ? diskCache : inMemoryCache;
            String content = cache.get(urlString);
            if (content == null) {
                content = httpClient.doGet(new URL(urlString), userAgent);
                cache.put(urlString, content);
            }
            return content;
        } catch (MalformedURLException e) {
            throw new ManagerException("incorrect url", e);
        } catch (HttpClientException e) {
            throw new ManagerException("Error occured with httpclient response: " + e.getResponseCode()
                    + " " + e.getResponseMessage());
        } catch (IOException | HttpClientSetupException e) {
            throw new ManagerException(e);
        }

    }

    public boolean store(String downloadlink, File file) throws ManagerException {
        try {
            return httpClient.doDownloadFile(new URL(downloadlink), file);
        } catch (MalformedURLException e) {
            throw new ManagerException("incorrect url", e);
        }
    }

    public String post(String urlString, String userAgent, Map<String, String> data)
            throws ManagerException {
        try {
            return httpClient.doPost(new URL(urlString), userAgent, data);
        } catch (MalformedURLException e) {
            throw new ManagerException("incorrect url", e);
        } catch (HttpClientSetupException | HttpClientException e) {
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
        if (httpClient == null) {
            throw new ManagerSetupException("HttpClient is not initialized");
        }
        if (inMemoryCache == null) {
            throw new ManagerSetupException("InMemoryCache is not initialized");
        }
        if (diskCache == null) {
            throw new ManagerSetupException("DiskCache is not initialized");
        }
    }

    public void removeCacheObject(String url) throws ManagerSetupException {
        validate();
        inMemoryCache.remove(url);
        diskCache.remove(url);
    }

    public boolean isCached(String url) {
        if (inMemoryCache.exists(url)) {
            return true;
        } else {
            return diskCache.exists(url);
        }
    }
}
