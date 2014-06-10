package org.lodder.subtools.sublibrary.cache;

import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.util.Base64;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA. User: lodder Date: 17/04/11 Time: 9:06 To change this template use File
 * | Settings | File Templates.
 */
public class DiskCacheManager extends CacheManager {

  private static DiskCacheManager dc = null;
  private HashMap<String, File> indexList = null;
  private final File path;

  private DiskCacheManager() {
    super();
    path = new File(System.getProperty("user.home"), ".MultiSubDownloader");
    if (!path.exists()) {
      path.mkdir();
    }
    indexList = new HashMap<String, File>();
    if (new File(path, "index").exists()) loadIndex();
  }

  public static DiskCacheManager getDiskCache() {
    if (dc == null) dc = new DiskCacheManager();
    return dc;
  }

  private void loadIndex() {
    Document newDoc;
    try {
      newDoc =
          DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(path, "index"));
      NodeList nList = newDoc.getElementsByTagName("CacheEntry");

      for (int i = 0; i < nList.getLength(); i++) {
        if (nList.item(i).getNodeType() == Node.ELEMENT_NODE) {
          boolean enc =
              XMLHelper.getBooleanAtributeValue("cacheurl", "encode", (Element) nList.item(i));

          String key = "";
          if (enc) {
            String raw = XMLHelper.getStringTagRawValue("cacheurl", (Element) nList.item(i));
            key = new String(Base64.decode(raw));
          } else {
            key = XMLHelper.getStringTagValue("cacheurl", (Element) nList.item(i));
          }
          String value = XMLHelper.getStringTagValue("cachefile", (Element) nList.item(i));
          indexList.put(key, new File(value));
        }
      }
    } catch (ParserConfigurationException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (SAXException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (IOException e) {
      Logger.instance.error(Logger.stack2String(e));
    }

  }

  private void storeIndex() {
    Document newDoc;
    try {
      newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      Element rootElement = newDoc.createElement("DiskCacheManager");
      newDoc.appendChild(rootElement);

      for (String key : indexList.keySet()) {
        String value = indexList.get(key).toString();
        Element cacheEntry = newDoc.createElement("CacheEntry");


        String encKey = new String(Base64.encode(key.getBytes()));
        Element cacheUrl = newDoc.createElement("cacheurl");
        cacheUrl.setAttribute("encode", "true");
        cacheUrl.appendChild(newDoc.createTextNode(encKey));

        cacheEntry.appendChild(cacheUrl);

        Element cacheFile = newDoc.createElement("cachefile");
        cacheFile.appendChild(newDoc.createTextNode(value));
        cacheEntry.appendChild(cacheFile);

        rootElement.appendChild(cacheEntry);
      }

      XMLHelper.writeToFile(new File(path, "index"), newDoc);
    } catch (ParserConfigurationException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (Exception e) {
      Logger.instance.error(Logger.stack2String(e));
    }
  }

  public String fetchAsString(URL url, long timeout) throws Exception {
    CacheEntry ce;
    if (indexList.containsKey(url.toString())) {
      ce = loadCacheObject(indexList.get(url.toString()));
      if (ce == null) {
        ce = getCacheEntry(url, timeout);
        storeCacheObject(url, ce);
        Logger.instance.debug("Added Disk cached element: " + url);
      } else {
        Date current = new Date();
        if (ce.getExpiresDate().compareTo(current) < 0) {
          deleteCacheObject(indexList.get(url.toString()));
          ce = getCacheEntry(url, timeout);
          storeCacheObject(url, ce);
          Logger.instance.debug("Added Disk cached element: " + url);
        } else {
          Logger.instance.debug("Found Disk cached element: " + url);
        }
      }
    } else {
      ce = getCacheEntry(url, timeout);
      storeCacheObject(url, ce);
      Logger.instance.debug("Added Disk cached element: " + url);
    }
    return ce.getContent();
  }

  private void storeCacheObject(URL url, CacheEntry ce) {
    FileOutputStream fos;
    ObjectOutputStream out;
    final File location = new File(path, UUID.randomUUID().toString());
    try {
      fos = new FileOutputStream(location);
      out = new ObjectOutputStream(fos);
      out.writeObject(ce);
      out.close();
      indexList.put(url.toString(), location);
      storeIndex();
    } catch (FileNotFoundException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (IOException e) {
      Logger.instance.error(Logger.stack2String(e));
    }
  }

  private CacheEntry loadCacheObject(File location) {
    CacheEntry ce = null;
    FileInputStream fis;
    ObjectInputStream in;
    try {
      fis = new FileInputStream(location);
      in = new ObjectInputStream(fis);
      ce = (CacheEntry) in.readObject();
      in.close();
    } catch (IOException ex) {
      Logger.instance.error(Logger.stack2String(ex));
    } catch (ClassNotFoundException ex) {
      location.delete(); // since the refactoring move the class, the object still looking in the
                         // previous location
      Logger.instance.error(Logger.stack2String(ex));
    }
    return ce;
  }

  private void deleteCacheObject(File location) {
    location.delete();
  }

  public void removeEntry(String url) {
    super.removeEntry(url);
    deleteCacheObject(indexList.get(url.toString()));
  }

  protected void finalize() throws Throwable {
    super.finalize();
    storeIndex();
  }
}
