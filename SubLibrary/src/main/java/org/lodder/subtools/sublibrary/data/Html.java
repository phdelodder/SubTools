package org.lodder.subtools.sublibrary.data;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.Manager.PageContentBuilderCacheTypeIntf;

@RequiredArgsConstructor
public class Html {
    @Getter
    private final Manager manager;
    private final String userAgent;

    public Html(Manager manager) {
        this.manager = manager;
        this.userAgent = "";
    }

    public void storeCookies(String domain, Map<String, String> cookieMap) {
        manager.storeCookies(domain, cookieMap);
    }

    public PageContentBuilderCacheTypeIntf getHtml(String url) {
        return manager.getPageContentBuilder().url(url).userAgent(userAgent);
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
