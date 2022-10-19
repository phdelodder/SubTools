package org.lodder.subtools.sublibrary.util.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.lodder.subtools.sublibrary.util.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HttpClient {

    private final CookieManager cookieManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);

    public HttpClient() {
        this(new CookieManager());
    }

    public String doGet(URL url, String userAgent) throws IOException, HttpClientException, HttpClientSetupException {
        URLConnection conn = url.openConnection();
        cookieManager.setCookies(conn);

        if (userAgent != null && userAgent.length() > 0) {
            conn.setRequestProperty("user-agent", userAgent);
        }

        int respCode = ((HttpURLConnection) conn).getResponseCode();

        if (respCode == 200) {
            String result = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
            ((HttpURLConnection) conn).disconnect();
            return result;
        }
        throw new HttpClientException((HttpURLConnection) conn);
    }

    public String doPost(URL url, String userAgent, Map<String, String> data) throws HttpClientSetupException, HttpClientException {
        HttpURLConnection conn = null;

        try {
            String urlParameters = data.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            conn = (HttpURLConnection) url.openConnection();
            cookieManager.setCookies(conn);
            conn.setRequestMethod("POST");
            if (userAgent.length() > 0) {
                conn.setRequestProperty("user-agent", userAgent);
            }
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes(StandardCharsets.UTF_8).length));
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);

            try (DataOutputStream out = new DataOutputStream(conn.getOutputStream())) {
                out.writeBytes(urlParameters);
                out.flush();
                out.close();
            }

            cookieManager.storeCookies(conn);

            if ((conn.getResponseCode() == 302) && isUrl(conn.getHeaderField("Location"))) {
                return doGet(new URL(conn.getHeaderField("Location")), userAgent);
            }

            String result = IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8);
            conn.disconnect();
            return result;

        } catch (IOException e) {
            throw new HttpClientException(e, conn);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public boolean doDownloadFile(URL url, final File file) {
        LOGGER.debug("doDownloadFile: URL [{}], file [{}]", url, file);
        boolean success = true;

        try (InputStream in = url.getFile().endsWith(".gz") ? new GZIPInputStream(url.openStream()) : getInputStream(url)) {
            byte[] data = IOUtils.toByteArray(in);
            in.close();

            if (url.getFile().endsWith(".zip") || Files.isZipFile(new ByteArrayInputStream(data))) {
                Files.unzip(new ByteArrayInputStream(data), file, ".srt");
            } else {
                if (Files.isGZipCompressed(data)) {
                    data = Files.decompressGZip(data);
                }
                String content = new String(data, StandardCharsets.UTF_8);
                if (content.contains("Daily Download count exceeded")) {
                    LOGGER.error("Download problem: Addic7ed Daily Download count exceeded!");
                    success = false;
                } else {
                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        IOUtils.write(data, outputStream);
                    }
                }
            }
        } catch (Exception e) {
            success = false;
            LOGGER.error("Download problem", e);
        }
        return success;
    }

    private InputStream getInputStream(URL url) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        cookieManager.setCookies(conn);
        conn.addRequestProperty("User-Agent", "Mozilla");
        conn.addRequestProperty("Referer", url.toString());
        conn.setInstanceFollowRedirects(false);

        int status = conn.getResponseCode();

        cookieManager.storeCookies(conn);

        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER) {
                if (HttpClient.isUrl(conn.getHeaderField("Location"))) {
                    url = new URL(conn.getHeaderField("Location"));
                } else {
                    String protocol = url.getProtocol();
                    String host = conn.getURL().getHost();
                    url = new URL(protocol + "://" + host + "/"
                            + conn.getHeaderField("Location").trim().replace(" ", "%20"));
                }
                return getInputStream(url);
            }

            throw new Exception("error: " + status);
        } else {
            return conn.getInputStream();
        }
    }

    public static boolean isUrl(String str) {
        Pattern urlPattern = Pattern.compile("((https?|ftp|gopher|telnet|file):((//)|(\\\\\\\\))+[\\\\w\\\\d:#@%/;$()~_?\\\\+-=\\\\\\\\\\\\.&]*)",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = urlPattern.matcher(str);
        return matcher.find();
    }

    public String downloadText(String url) throws java.io.IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
            return in.lines().collect(Collectors.joining());
        }
    }

    public void storeCookies(String domain, Map<String, String> cookieMap) {
        cookieManager.storeCookies(domain, cookieMap);
    }

}
