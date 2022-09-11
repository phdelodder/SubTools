/**
 *
 */
package org.lodder.subtools.sublibrary.util.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.lodder.subtools.sublibrary.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

/**
 * @author lodder
 *
 */
public class DropBoxClient {

    private static DropBoxClient dbc;
    private DbxClientV2 dbxClient;
    private final String locationOffset = "/Ondertitels/PrivateRepo/";
    private final String unSortedLocationOffset = "/Ondertitels/Unsorted";

    private static final Logger LOGGER = LoggerFactory.getLogger(DropBoxClient.class);

    DropBoxClient() {
        dropboxInit();
    }

    public static DropBoxClient getDropBoxClient() {
        if (dbc == null) {
            dbc = new DropBoxClient();
        }
        return dbc;
    }

    private void dropboxInit() {
        final String accessToken = "3x5qOT-XdxgAAAAAAAAAAVa2Hrj23e7EiO98AZqw-UqGEr7I4lJG6eL8M1s9LlG0";

        DbxRequestConfig config = new DbxRequestConfig("PersonalDownload/1.0");

        dbxClient = new DbxClientV2(config, accessToken);
    }

    public boolean doDownloadFile(String location, File output) {
        boolean success = false;
        try (FileOutputStream outputStream = new FileOutputStream(output)) {
            dbxClient.files().download(locationOffset + location).download(outputStream);
            success = true;
            outputStream.close();
        } catch (DbxException | IOException e) {
            LOGGER.error("doDownloadFile", e);
        }
        return success;
    }

    public void put(File inputFile, String filename, Language language) {
        // FileInputStream inputStream = null;
        // try {
        // inputStream = new FileInputStream(inputFile);
        // dbxClient.files().upload(unSortedLocationOffset + "/" + languageCode + "/" + filename).uploadAndFinish(inputStream, inputFile.length());
        // } catch (DbxException | IOException e) {
        // LOGGER.error("upload path: " + unSortedLocationOffset + "/" + languageCode + "/" + filename );
        // LOGGER.error("put", e);
        // } finally {
        // if (inputStream != null) {
        // try {
        // inputStream.close();
        // } catch (IOException e) {
        // LOGGER.error("put close inputStream", e);
        // }
        // }
        // }
    }

    public String getFile(String location) {
        String content = "";
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            dbxClient.files().download(location).download(outputStream);
            content = outputStream.toString(StandardCharsets.UTF_8);
        } catch (DbxException | IOException e) {
            LOGGER.error("getFile", e);
        }
        return content;
    }

}
