package org.lodder.subtools.multisubdownloader;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.lodder.subtools.sublibrary.ConfigProperties;
import org.lodder.subtools.sublibrary.cache.CacheManager;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.util.http.HttpClient;


public class UpdateAvailableDropbox {

	private final String url;
	private String updatedUrl;
	private final CacheManager ucm;
	private static long timeout = 900;
	private final static String programName = "MultiSubDownloader";
	private final static String extension = "jar";

	public UpdateAvailableDropbox() {
		url = "https://www.dropbox.com/sh/1gz18xwzinfgmbl/gDfSgYN1qC";
		ucm = CacheManager.getURLCache();
		updatedUrl = "";
	}

	public boolean checkProgram() {
		try {
			return check(programName, extension);
		} catch (Exception e) {
			Logger.instance.error(Logger.stack2String(e));
		}
		return false;
	}

	public boolean checkMapping() {
		try {
			return check("Mapping", "xml");
		} catch (Exception e) {
			Logger.instance.error(Logger.stack2String(e));
		}

		return false;
	}

	public String getUpdateUrl() {
		return updatedUrl;
	}

	private boolean check(String baseName, String extension) {
		try {
			String newFoundVersion = ConfigProperties.getInstance().getProperty("version");
			String source = ucm.fetchAsString(new URL(url), timeout);
			Document sourceDoc = Jsoup.parse(source);
			Elements results = sourceDoc.getElementsByClass("filename-link");
			for (Element result : results) {
				String href = result.attr("href");
				if (href.contains(baseName)) {
					String foundVersion = href
							.substring(href.lastIndexOf("/") + 1, href.length())
							.replace(baseName, "")
							.replace("-v", "").replace("." + extension, "").trim();
					int compare = compareVersions(ConfigProperties.getInstance().getProperty("version"), foundVersion);
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
		} catch (Exception e) {
			Logger.instance.error(Logger.stack2String(e));
		}

		return false;
	}

	private int compareVersions(String str1, String str2) {
		String[] vals1 = str1.split("\\.");
		String[] vals2 = str2.split("\\.");
		int i = 0;
		while (i < vals1.length && i < vals2.length
				&& vals1[i].equals(vals2[i])) {
			i++;
		}

		if (i < vals1.length && i < vals2.length) {
			int diff = Integer.valueOf(vals1[i]).compareTo(
					Integer.valueOf(vals2[i]));
			return Integer.signum(diff);
		}

		return Integer.signum(vals1.length - vals2.length);
	}
}
