package org.lodder.subtools.sublibrary.util.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import extensions.java.io.InputStream.InputStreamExt;
import extensions.java.nio.file.Path.PathExt;
import jakarta.ws.rs.core.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.HttpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record HttpClient(CookieManager cookieManager=new CookieManager()) {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    public String doGet(URL url, String userAgent, CookieManager cookieManager=null) throws IOException,
        HttpClientException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            getCookieManager(cookieManager).setCookies(conn);
            if (StringUtils.isNotBlank(userAgent)) {
                conn.setRequestProperty(HttpHeaders.USER_AGENT, userAgent);
            }
            if (conn.responseCode == 200) {
                return InputStreamExt.asString(conn.getInputStream(), StandardCharsets.UTF_8);
            }
            throw new HttpClientException(conn);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public String doPost(URL url, String userAgent, Map<String, String> data, CookieManager cookieManager=null)
        throws HttpClientException {
        HttpURLConnection conn = null;

        try {
            String urlParameters = data.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

            conn = (HttpURLConnection) url.openConnection();
            getCookieManager(cookieManager).setCookies(conn);
            conn.setRequestMethod("POST");
            if (StringUtils.isNotBlank(userAgent)) {
                conn.setRequestProperty(HttpHeaders.USER_AGENT, userAgent);
            }
            conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, HttpConnection.FORM_URL_ENCODED);
            conn.setRequestProperty(HttpHeaders.CONTENT_LENGTH,
                String.valueOf(urlParameters.getBytes(StandardCharsets.UTF_8).length));
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);

            try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                out.writeBytes(urlParameters);
                out.flush();
            }

            getCookieManager(cookieManager).storeCookies(conn);

            if (conn.responseCode == 302 && isUrl(conn.getHeaderField(HttpHeaders.LOCATION))) {
                return doGet(new URI(conn.getHeaderField(HttpHeaders.LOCATION)).toURL(), userAgent, cookieManager);
            }
            return InputStreamExt.asString(conn.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException | URISyntaxException e) {
            throw new HttpClientException(e, conn);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public boolean doDownloadFile(URL url, final Path file, CookieManager cookieManager=null) {
        LOGGER.debug("doDownloadFile: URL [{}], file [{}]", url, file);
        boolean success = true;

        try (InputStream in = url.getFile().endsWith(".gz") ?
            new GZIPInputStream(url.openStream()) : getInputStream(url, getCookieManager(cookieManager))) {
            byte[] data = in.readAllBytes();

            if (url.getFile().endsWith(".zip") || PathExt.isZipFile(new ByteArrayInputStream(data))) {
                PathExt.unzip(new ByteArrayInputStream(data), file, ".srt");
            } else {
                if (PathExt.isGZipCompressed(data)) {
                    data = PathExt.decompressGZip(data);
                }
                String content = new String(data, StandardCharsets.UTF_8);
                if (content.contains("Daily Download count exceeded")) {
                    LOGGER.error("Download problem: Addic7ed Daily Download count exceeded!");
                    success = false;
                } else {
                    Files.write(file, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE);
                }
            }
        } catch (Exception e) {
            success = false;
            LOGGER.error("Download problem", e);
        }
        return success;
    }

    private InputStream getInputStream(URL url, CookieManager cookieManager=null) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        cookieManager.setCookies(conn);
        conn.addRequestProperty(HttpHeaders.USER_AGENT, "Mozilla");
        conn.addRequestProperty("Referer", url.toString());
        conn.setInstanceFollowRedirects(false);

        int status = conn.getResponseCode();

        cookieManager.storeCookies(conn);

        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_SEE_OTHER) {
                String locationHeader = conn.getHeaderField(HttpHeaders.LOCATION);
                URL newUrl;
                if (HttpClient.isUrl(locationHeader)) {
                    newUrl = new URI(locationHeader).toURL();
                } else {
                    newUrl = new URI("%s://%s/%s".formatted(url.protocol, conn.getURL().host,
                        locationHeader.trim().replace(" ", "%20"))).toURL();
                }
                return getInputStream(newUrl, cookieManager);
            }
            throw new Exception("error: " + status);
        } else {
            return conn.getInputStream();
        }
    }

    public static boolean isUrl(String str) {
        Pattern urlPattern = Pattern.compile(
            "((https?|ftp|gopher|telnet|file):((//)|(\\\\\\\\))+[\\\\w\\\\d:#@%/;$()~_?\\\\+-=\\\\\\\\\\\\.&]*)",
            Pattern.CASE_INSENSITIVE);
        return urlPattern.matcher(str).find();
    }

    public String downloadText(String url) throws IOException {
        try (BufferedReader in = new BufferedReader(
            new InputStreamReader(new URI(url).toURL().openStream(), StandardCharsets.UTF_8))) {
            return in.lines().collect(Collectors.joining());
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public void storeCookies(String domain, Map<String, String> cookieMap) {
        cookieManager.storeCookies(domain, cookieMap);
    }

    private CookieManager getCookieManager(CookieManager cookieManager) {
        return cookieManager == null ? this.cookieManager : cookieManager;
    }
}
