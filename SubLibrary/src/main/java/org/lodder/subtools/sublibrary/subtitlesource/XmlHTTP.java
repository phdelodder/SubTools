package org.lodder.subtools.sublibrary.subtitlesource;

import org.lodder.subtools.sublibrary.cache.CacheManager;
import org.lodder.subtools.sublibrary.cache.DiskCacheManager;
import org.lodder.subtools.sublibrary.logging.Level;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;





import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA. User: lodder Date: 20/08/11 Time: 15:11 To change this template use
 * File | Settings | File Templates.
 */
public class XmlHTTP {

  private CacheManager ucm;
  private DiskCacheManager dcm;
  private String userAgent;
  private int ratelimit;
  private int dayRatelimit;

  public XmlHTTP() {
    ucm = CacheManager.getURLCache();
    dcm = DiskCacheManager.getDiskCache();
  }

  public Document getXML(String url) {
    return getXML(url, 900);
  }

  public Document getXML(String url, long timeout) {
    Logger.instance.trace("XmlHTTPAPI", "getXML", "timeout: " + timeout);
    try {
      if (userAgent != null) {
        ucm.setUserAgent(userAgent);
      }
      ucm.setRatelimit(ratelimit);
      ucm.setDayRateLimit(dayRatelimit);
      return XMLHelper.getDocument(ucm.fetchAsInputStream(new URL(url), timeout));
    } catch (MalformedURLException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (ParserConfigurationException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (SAXException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (IOException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (Exception e) {
      Logger.instance.error(Logger.stack2String(e));
    }
    return null;
  }

  public Document getXMLDisk(String url) {
    // cache timout is 30 dagen
    return getXMLDisk(url, 24 * 60 * 60 * 30);
  }

  public Document getXMLDisk(String url, long timeout) {
    Logger.instance.trace("XmlHTTPAPI", "getXMLDisk", "timeout: " + timeout);
    try {
      if (userAgent != null) {
        dcm.setUserAgent(userAgent);
      }
      dcm.setRatelimit(ratelimit);
      dcm.setDayRateLimit(dayRatelimit);
      String content = dcm.fetchAsString(new URL(url), timeout);
      // Bierdopje issue! OElig
      content = content.replaceAll("&OElig;", "Œ");
      // Bierdopje issue! &Pi;
      content = content.replaceAll("&Pi;", "\u003A0").replaceAll("&pi;", "\u003A0");
      // BIerdopje issue! &hellip;
      content = content.replaceAll("&hellip;", "...");
      return XMLHelper.getDocument(content);
    } catch (Exception e) {
      if (Logger.instance.getLogLevel().intValue() < Level.INFO.intValue()) {
        Logger.instance.error(Logger.stack2String(e));
      } else {
        Logger.instance.error(e.getMessage());
      }
    }
    return null;
  }

  public void removeCacheEntry(String url) {
    dcm.removeEntry(url);
    ucm.removeEntry(url);
  }

  /**
   * @return the userAgent
   */
  public String getUserAgent() {
    return userAgent;
  }

  /**
   * @param userAgent the userAgent to set
   */
  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  /**
   * @return the ratelimit
   */
  public int getRatelimit() {
    return ratelimit;
  }

  /**
   * @param ratelimit the ratelimit to set
   */
  public void setRatelimit(int ratelimit) {
    this.ratelimit = ratelimit;
  }

  /**
   * @return the dayRatelimit
   */
  public int getDayRatelimit() {
    return dayRatelimit;
  }

  /**
   * @param dayRatelimit the dayRatelimit to set
   */
  public void setDayRatelimit(int dayRatelimit) {
    this.dayRatelimit = dayRatelimit;
  }
}
