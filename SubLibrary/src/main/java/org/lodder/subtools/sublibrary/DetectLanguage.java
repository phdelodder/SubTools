package org.lodder.subtools.sublibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.lodder.subtools.sublibrary.logging.Logger;

import me.champeau.ld.UberLanguageDetector;

public class DetectLanguage {

  public static String execute(File file) throws Exception {
    String text = readText(file);

    /*
     * text = text.substring(0, 500); Detect.setKey("D6D3A40EDC77276322060DCC2004F50B6CCA351F");
     * Language detectedLanguage = null; try { detectedLanguage = Detect.execute(text); } catch
     * (Exception e) { Logger.instance.error(e.getMessage()); } if (detectedLanguage == null){
     * Logger.instance.log("Cannot detect language pause for 15 seconds before retry!"); try { //
     * Pause for 15 seconds TimeUnit.SECONDS.sleep(15); } catch (InterruptedException e) {
     * Thread.currentThread().interrupt(); // restore // interrupted // status } return
     * execute(file); } return detectedLanguage.toString();
     */

    UberLanguageDetector detector = UberLanguageDetector.getInstance();

    String language = detector.detectLang(text);
    return language;

  }

  private static String readText(File file) {
    String text = "";
    FileInputStream is = null;
    try {
      is = new FileInputStream(file);

      byte[] contents = new byte[(int) file.length()];
      is.read(contents);

      text = new String(contents, "UTF-8");
    } catch (FileNotFoundException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (IOException e) {
      Logger.instance.error(Logger.stack2String(e));
    } finally {
      try {
        if (is != null) is.close();
      } catch (IOException e) {
        Logger.instance.error(Logger.stack2String(e));
      }
    }

    return text;
  }

}
