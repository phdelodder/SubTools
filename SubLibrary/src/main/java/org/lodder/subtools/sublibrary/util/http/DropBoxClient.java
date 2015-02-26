/**
 * 
 */
package org.lodder.subtools.sublibrary.util.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.lodder.subtools.sublibrary.logging.Logger;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;

/**
 * @author lodder
 * 
 */
public class DropBoxClient {

  private static DropBoxClient dbc;
  private DbxClient dbxClient;
  private String locationOffset = "/Ondertitels/PrivateRepo/";
  private String unSortedLocationOffset = "/Ondertitels/Unsorted/";

  DropBoxClient() {
    dropboxInit();
  }

  public static DropBoxClient getDropBoxClient() {
    if (dbc == null) dbc = new DropBoxClient();
    return dbc;
  }

  private void dropboxInit() {
    final String accessToken = "3x5qOT-XdxgAAAAAAAAAAVa2Hrj23e7EiO98AZqw-UqGEr7I4lJG6eL8M1s9LlG0";

    DbxRequestConfig config =
        new DbxRequestConfig("PersonalDownload/1.0", java.util.Locale.getDefault().toString());

    dbxClient = new DbxClient(config, accessToken);
  }

  public boolean doDownloadFile(String location, File output) {
    boolean success = false;
    try (FileOutputStream outputStream = new FileOutputStream(output)){
      dbxClient.getFile(locationOffset + location, null, outputStream);
      success = true;
      outputStream.close();
    } catch (DbxException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (IOException e) {
      Logger.instance.error(Logger.stack2String(e));
    }
    return success;
  }

  public void put(File inputFile, String filename, String languageCode) {
    FileInputStream inputStream = null;
    try {
      inputStream = new FileInputStream(inputFile);
      dbxClient.uploadFile(unSortedLocationOffset + "/" + languageCode + "/" + filename,
          DbxWriteMode.add(), inputFile.length(), inputStream);
    } catch (DbxException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (IOException e) {
      Logger.instance.error(Logger.stack2String(e));
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          Logger.instance.error(Logger.stack2String(e));
        }
      }
    }
  }

  /**
   * @param location
   * @return
   */
  public String getFile(String location) {
    String content = "";
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      dbxClient.getFile(location, null, outputStream);
      content = outputStream.toString("UTF-8");
      outputStream.close();
    } catch (DbxException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (IOException e) {
      Logger.instance.error(Logger.stack2String(e));
    }
    return content;
  }

}
