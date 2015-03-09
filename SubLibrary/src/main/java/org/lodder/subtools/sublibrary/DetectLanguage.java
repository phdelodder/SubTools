package org.lodder.subtools.sublibrary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import me.champeau.ld.UberLanguageDetector;

import org.apache.commons.io.FileUtils;
import org.lodder.subtools.sublibrary.logging.Logger;

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

    return detector.detectLang(text);

  }

  private static String readText(File file) {
    String text = "";
    try {
      text = FileUtils.readFileToString(file);
    } catch (FileNotFoundException e) {
      Logger.instance.error(Logger.stack2String(e));
    } catch (IOException e) {
      Logger.instance.error(Logger.stack2String(e));
    }

    return text;
  }

}
