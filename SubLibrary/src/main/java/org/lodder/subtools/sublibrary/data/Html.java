package org.lodder.subtools.sublibrary.data;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.PageContentBuilderCacheTypeIntf;
import org.lodder.subtools.sublibrary.ManagerException;

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

    public PageContentBuilderCacheTypeIntf getHtml(String url) {
        return manager.getPageContentBuilder().url(url).userAgent(userAgent);
    }

    public String postHtml(String url, Map<String, String> data) throws ManagerException {
        return manager.post(url, userAgent, data);
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
