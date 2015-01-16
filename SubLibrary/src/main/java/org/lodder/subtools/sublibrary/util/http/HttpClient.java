package org.lodder.subtools.sublibrary.util.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.util.Files;


public class HttpClient {

  private static volatile Object myLock = new Object();
  private volatile static HttpClient hc;
  private final CookieManager cookieManager;

  HttpClient() {
    cookieManager = new CookieManager();
  }

  public static HttpClient getHttpClient() {
    Logger.instance.trace("HttpClient", "getHttpClient", "");
    if (hc == null) { // avoid sync penalty if we can
      synchronized (HttpClient.myLock) { // declare a private static Object to use for mutex
        if (hc == null) { // have to do this inside the sync
          hc = new HttpClient();
        }
      }
    }

    return hc;
  }

  public String doGet(URL url, String userAgent) {
    URLConnection conn = null;
    try {
      conn = url.openConnection();
      cookieManager.setCookies(conn);

      if (userAgent.length() > 0) conn.setRequestProperty("user-agent", userAgent);

      return getStringFromInputStream(conn.getInputStream());

    } catch (IOException e) {
      try {
        int respCode = ((HttpURLConnection) conn).getResponseCode();
        if (respCode == 429) {
          Logger.instance.log("HTTP STATUS CODE 429: gelieve 1 minuut te wachten");
          try {
            TimeUnit.MINUTES.sleep(1);
            TimeUnit.SECONDS.sleep(1);
          } catch (InterruptedException ie) {
            // restore interrupted status
            Thread.currentThread().interrupt();
          }
          return doGet(url, userAgent);
        } else {
          Logger.instance.error("Error response: " + respCode + " For url: " + url);
        }
      } catch (Exception ex) {
        Logger.instance.error(ex.getMessage());
        Logger.instance.debug(Logger.stack2String(ex));
      }
    }
    return "";
  }

  public String doPost(URL url, String userAgent, Map<String, String> data) {
    HttpURLConnection conn = null;
    Set<String> keys = data.keySet();
    Iterator<String> keyIter = keys.iterator();
    StringBuilder urlParameters = new StringBuilder();

    try {
      for (int i = 0; keyIter.hasNext(); i++) {
        Object key = keyIter.next();
        if (i != 0) {
          urlParameters.append("&");
        }
        urlParameters.append(key + "=" + URLEncoder.encode(data.get(key), "UTF-8"));
      }

      conn = (HttpURLConnection) url.openConnection();
      cookieManager.setCookies(conn);
      conn.setRequestMethod("POST");
      if (userAgent.length() > 0) conn.setRequestProperty("user-agent", userAgent);
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setRequestProperty("Content-Length",
          "" + Integer.toString(urlParameters.toString().getBytes().length));
      conn.setUseCaches(false);
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setInstanceFollowRedirects(false);

      DataOutputStream out = new DataOutputStream(conn.getOutputStream());

      out.writeBytes(urlParameters.toString());
      out.flush();
      out.close();

      cookieManager.storeCookies(conn);
      if (conn.getResponseCode() == 302) {
        if (isUrl(conn.getHeaderField("Location"))) {
          return doGet(new URL(conn.getHeaderField("Location")), userAgent);
        }
      }

      return getStringFromInputStream(conn.getInputStream());

    } catch (ProtocolException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (IOException e) {
      Logger.instance.error(Logger.stack2String(e));
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }

    return "";
  }

  private static String getStringFromInputStream(InputStream is) {
    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();

    String line;
    try {

      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return sb.toString();

  }

  public boolean doDownloadFile(URL url, final File file) {
    Logger.instance.debug("url: " + url.toString());
    boolean success = true;

    try {
      final InputStream in;
      if (url.getFile().endsWith(".gz")) {
        in = new BufferedInputStream(new GZIPInputStream(url.openStream()));
      } else {
        in = getInputStream(url);
      }

      byte[] data = IOUtils.toByteArray(in);
      in.close();

      if (url.getFile().endsWith(".zip") | Files.isZipFile(new ByteArrayInputStream(data))) {
        Files.unzip(new ByteArrayInputStream(data), file, ".srt");
      } else {
        if (Files.isGZipCompressed(data)) {
          data = Files.decompressGZip(data);
        }
        String content = new String(data, "UTF-8");
        if (content.contains("Daily Download count exceeded")) {
          Logger.instance.error("Download problem: Addic7ed Daily Download count exceeded!");
          success = false;
        } else {
          FileOutputStream outputStream = new FileOutputStream(file);
          IOUtils.write(data, outputStream);
          outputStream.close();
        }
      }
    } catch (Exception e) {
      success = false;
      Logger.instance.error("Download problem: " + e.getMessage());
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
      if (status == HttpURLConnection.HTTP_MOVED_TEMP
          || status == HttpURLConnection.HTTP_MOVED_PERM
          || status == HttpURLConnection.HTTP_SEE_OTHER) {
        if (HttpClient.isUrl(conn.getHeaderField("Location"))) {
          url = new URL(conn.getHeaderField("Location"));
        } else {
          String protocol = url.getProtocol();
          String host = conn.getURL().getHost();
          url =
              new URL(protocol + "://" + host + "/"
                  + conn.getHeaderField("Location").trim().replaceAll(" ", "%20"));
        }
        return getInputStream(url);
      }

      throw new Exception("error: " + status);
    } else {
      return conn.getInputStream();
    }
  }

  public static boolean isUrl(String str) {
    Pattern urlPattern =
        Pattern
            .compile(
                "((https?|ftp|gopher|telnet|file):((//)|(\\\\\\\\))+[\\\\w\\\\d:#@%/;$()~_?\\\\+-=\\\\\\\\\\\\.&]*)",
                Pattern.CASE_INSENSITIVE);
    Matcher matcher = urlPattern.matcher(str);
    if (matcher.find())
      return true;
    else
      return false;
  }

  public String downloadText(URL url) throws IOException {
    String html = IOUtils.toString(url.openConnection().getInputStream());
    String content = "";
    try (BOMInputStream bomIn = new BOMInputStream(IOUtils.toInputStream(html))) {
      content = IOUtils.toString(bomIn);
    }
    return content;
  }

}
