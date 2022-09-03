package org.lodder.subtools.sublibrary.data;

import java.io.IOException;
import java.util.Map;

import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;

public class Html {

    private Manager manager;
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

    public String getHtml(String url) throws IOException, HttpClientException,
            ManagerSetupException, ManagerException {
        return manager.getContent(url, userAgent, false);
    }

    public String getHtmlDisk(String url) throws IOException,
            HttpClientException, ManagerSetupException, ManagerException {
        return manager.getContent(url, userAgent, false);
    }

    public String postHtml(String url, Map<String, String> data) throws ManagerException {
        return manager.post(url, userAgent, data);
    }

    public boolean isCached(String url) {
        return manager.isCached(url);
    }
}
