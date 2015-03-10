package org.lodder.subtools.multisubdownloader;

import java.util.Calendar;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lodder.subtools.multisubdownloader.settings.model.UpdateCheckPeriod;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.util.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UpdateAvailableDropbox {

  private final String url;
  private String updatedUrl;
  private Manager manager;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateAvailableDropbox.class);


  private final static String programName = ConfigProperties.getInstance().getProperty(
      "updateProgramName");
  private final static String extension = ConfigProperties.getInstance().getProperty(
      "updateProgramExtension");

  public UpdateAvailableDropbox(Manager manager) {
    url = ConfigProperties.getInstance().getProperty("updateUrlDropbox");
    this.manager = manager;
    updatedUrl = "";
  }

  public boolean checkProgram(UpdateCheckPeriod updateCheckPeriod) {
    try {
      Calendar date = Calendar.getInstance();
      switch (updateCheckPeriod) {
        case DAILY:
          return check(programName, extension);
        case MANUAL:
          break;
        case MONTHLY:
          date.set(Calendar.DAY_OF_MONTH, 1);
          if (DateUtils.isSameDay(date, Calendar.getInstance())) {
            LOGGER.info(Messages.getString("UpdateAvailableDropbox.CheckingForUpdate"));
            return check(programName, extension);
          }
          break;
        case WEEKLY:
          date.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
          if (DateUtils.isSameDay(date, Calendar.getInstance())) {
            LOGGER.info(Messages.getString("UpdateAvailableDropbox.CheckingForUpdate"));
            return check(programName, extension);
          }
          break;
        default:
          break;
      }
    } catch (Exception e) {
      LOGGER.error("checkProgram", e);
    }
    return false;
  }

  public boolean checkMapping() {
    try {
      return check("Mapping", "xml");
    } catch (Exception e) {
      LOGGER.error("checkMapping", e);
    }

    return false;
  }

  public String getUpdateUrl() {
    return updatedUrl;
  }

  private boolean check(String baseName, String extension) {
    try {
      String newFoundVersion =
          ConfigProperties.getInstance().getProperty("version").replace("-SNAPSHOT", "");
      String source = manager.getContent(url, null, false);
      Document sourceDoc = Jsoup.parse(source);
      Elements results = sourceDoc.getElementsByClass("filename-link");
      for (Element result : results) {
        String href = result.attr("href");
        if (href.contains(baseName)) {
          String foundVersion =
              href.substring(href.lastIndexOf("/") + 1, href.length()).replace(baseName, "")
                  .replace("-v", "").replace("-r", "").replace("." + extension, "").trim()
                  .replace("?dl=0", "");
          int compare =
              compareVersions(
                  ConfigProperties.getInstance().getProperty("version").replace("-SNAPSHOT", ""),
                  foundVersion);
          if (compare < 0) {
            if (compareVersions(newFoundVersion, foundVersion) <= 0) {
              newFoundVersion = foundVersion;
              updatedUrl = href;
            }
          }
        }
      }
      if (HttpClient.isUrl(updatedUrl)) {
        return true;
      }
    } catch (ManagerSetupException  | ManagerException e) {
      LOGGER.error("", e);
    }

    return false;
  }

  private int compareVersions(String str1, String str2) {
    String[] vals1 = str1.split("\\.");
    String[] vals2 = str2.split("\\.");
    int i = 0;
    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
      i++;
    }

    if (i < vals1.length && i < vals2.length) {
      int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
      return Integer.signum(diff);
    }

    return Integer.signum(vals1.length - vals2.length);
  }
}
