package org.lodder.subtools.sublibrary.data.IMDB;

import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import org.lodder.subtools.sublibrary.cache.CacheManager;
import org.lodder.subtools.sublibrary.logging.Logger;


public class IMDBSearchID {

    private final CacheManager ucm;

    public IMDBSearchID() {
        ucm = CacheManager.getURLCache();
    }

    public int getImdbId(String title, int year) {
        int imdbid = this.getImdbIdOnGoogle(title, year);
        if (imdbid == 0) {
            return this.getImdbIdOnYahoo(title, year);
        } else {
            return imdbid;
        }
    }

    public int getImdbIdOnYahoo(String title, int year) {
        try {
            StringBuilder sb = new StringBuilder("http://search.yahoo.com/search;_ylt=A1f4cfvx9C1I1qQAACVjAQx.?p=");
            sb.append(URLEncoder.encode(title, "UTF-8"));

            if (year > 0) {
                sb.append("+%28").append(year).append("%29");
            }

            sb.append("+site%3Aimdb.com&fr=yfp-t-501&ei=UTF-8&rd=r1");

            String xml = ucm.fetchAsString(new URL(sb.toString()), 900);
            int beginIndex = xml.indexOf("/title/tt");
            StringTokenizer st = new StringTokenizer(xml.substring(beginIndex + 7), "/\"");
            String imdbId = st.nextToken();

            if (imdbId.startsWith("tt")) {
                Logger.instance.debug("Found imdb with Yahoo: " + imdbId);
                return Integer.parseInt(imdbId.substring(2));
            }

        } catch (Exception e) {
            Logger.instance.log(e.getMessage());
        }
        return 0;
    }


    public int getImdbIdOnGoogle(String title, int year) {
        try {
            StringBuilder sb = new StringBuilder("http://www.google.com/search?q=");
            sb.append(URLEncoder.encode(title, "UTF-8"));

            if (year > 0) {
                sb.append("+%28").append(year).append("%29");
            }

            sb.append("+site%3Awww.imdb.com&meta=");

            String xml = ucm.fetchAsString(new URL(sb.toString()), 900);
            String imdbId = "";

            int beginIndex = xml.indexOf("/title/tt");
            if (beginIndex > -1) {
                StringTokenizer st = new StringTokenizer(xml.substring(beginIndex + 7), "/\"");
                imdbId = st.nextToken();
            }

            if (imdbId.startsWith("tt")) {
                Logger.instance.debug("Found imdb with Google: " + imdbId);
                return Integer.parseInt(imdbId.substring(2));
            }

        } catch (Exception e) {
            Logger.instance.log(e.getMessage());

        }
        return 0;
    }

}
