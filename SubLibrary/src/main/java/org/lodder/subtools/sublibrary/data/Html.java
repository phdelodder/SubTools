package org.lodder.subtools.sublibrary.data;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;

import com.pivovarit.function.ThrowingSupplier;

import lombok.Getter;

public class Html {
    @Getter
    private final Manager manager;
    private String userAgent;

    public Html(Manager manager) {
        this.manager = manager;
        this.userAgent = "";
    }

    public Html(Manager manager, String userAgent) {
        this.manager = manager;
        this.userAgent = userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void storeCookies(String domain, Map<String, String> cookieMap) {
        manager.storeCookies(domain, cookieMap);
    }

    public String getHtml(String url) throws IOException, HttpClientException, ManagerSetupException, ManagerException {
        return manager.getContent(url, userAgent, false);
    }

    public String getHtmlDisk(String url) throws IOException, HttpClientException, ManagerSetupException, ManagerException {
        return manager.getContent(url, userAgent, true);
    }

    public String postHtml(String url, Map<String, String> data) throws ManagerException {
        return manager.post(url, userAgent, data);
    }

    public boolean isUrlCached(String url) {
        return manager.isCached(url);
    }

    public <X extends Exception> String getValue(String key, ThrowingSupplier<String, X> valueSupplier) throws X, ManagerSetupException {
        return manager.getValue(key, valueSupplier, false);
    }

    public <X extends Exception> String getValueDisk(String key, ThrowingSupplier<String, X> valueSupplier) throws X, ManagerSetupException {
        return manager.getValue(key, valueSupplier, true);
    }

    public <X extends Exception> Optional<String> getOptionalValue(String key, ThrowingSupplier<Optional<String>, X> valueSupplier)
            throws X, ManagerSetupException {
        return manager.getOptionalValue(key, valueSupplier, false);
    }

    public <X extends Exception> Optional<String> getOptionalValueDisk(String key, ThrowingSupplier<Optional<String>, X> valueSupplier)
            throws X, ManagerSetupException {
        return manager.getOptionalValue(key, valueSupplier, true);
    }

    public void sleepSeconds(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e1) {
            // restore interrupted status
            Thread.currentThread().interrupt();
        }
    }
}
