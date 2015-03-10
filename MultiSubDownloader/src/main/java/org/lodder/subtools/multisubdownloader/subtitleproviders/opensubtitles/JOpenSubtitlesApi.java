package org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.model.OpenSubtitlesMovieDescriptor;
import org.lodder.subtools.multisubdownloader.subtitleproviders.opensubtitles.model.OpenSubtitlesSubtitleDescriptor;
import org.lodder.subtools.sublibrary.data.XmlRPC;
import org.lodder.subtools.sublibrary.util.NamedMatcher;
import org.lodder.subtools.sublibrary.util.NamedPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JOpenSubtitlesApi extends XmlRPC {

  private static final Logger LOGGER = LoggerFactory.getLogger(JOpenSubtitlesApi.class);

  public JOpenSubtitlesApi(String useragent) throws Exception {
    super(useragent, "http://api.opensubtitles.org/xml-rpc");
    if (!isLoggedOn()) loginAnonymous();
  }

  public void loginAnonymous() throws Exception {
    login("", "");
  }

  public void login(String username, String password) throws Exception {
    login(username, password, "en");
  }

  @SuppressWarnings("unchecked")
  public synchronized void login(String username, String password, String language)
      throws Exception {
    Map<String, String> response =
        (Map<String, String>) invoke("LogIn", new Object[] {username, password, language,
            getUserAgent()});

    setToken(response.get("token"));
  }

  public synchronized void logout() throws MalformedURLException {
    try {
      invoke("LogOut", new Object[] {getToken()});
    } catch (Exception localXmlRpcFault) {} finally {
      setToken(null);
    }
  }

  @SuppressWarnings("unchecked")
  public Map<String, String> getServerInfo() throws Exception {
    return (Map<String, String>) invoke("ServerInfo", new Object[] {getToken()});
  }

  @SuppressWarnings("unchecked")
  public List<OpenSubtitlesMovieDescriptor> searchMoviesOnIMDB(String title) throws Exception {
    List<OpenSubtitlesMovieDescriptor> movies = new ArrayList<OpenSubtitlesMovieDescriptor>();
    Map<String, List<Map<String, String>>> response =
        (Map<String, List<Map<String, String>>>) invoke("SearchMoviesOnIMDB", new Object[] {
            getToken(), title});

    List<Map<String, String>> movieData = (List<Map<String, String>>) response.get("data");

    NamedPattern np =
        NamedPattern.compile(
            "(?<moviename>[\\w\\s:&().,_-]+)[\\.|\\[|\\(| ]{1}(?<year>19\\d{2}|20\\d{2})", 2);

    for (Map<String, String> movie : movieData) {
      Scanner titleScanner = new Scanner((String) movie.get("title"));
      titleScanner.useDelimiter("(Â)|(\\s+aka\\s+)");

      NamedMatcher nm = np.matcher(titleScanner.next().trim());

      int imdbid = 0;
      int year = 0;

      if (nm.find()) {
        try {
          imdbid = Integer.parseInt((String) movie.get("id"));
          year = Integer.parseInt(nm.group("year"));
        } catch (Exception e) {
          LOGGER.error("searchMoviesOnImdb parse imdbid and year", e);
        }

        movies.add(new OpenSubtitlesMovieDescriptor(nm.group("moviename"), year, imdbid));
      }

      titleScanner.close();
    }

    return movies;
  }

  public OpenSubtitlesMovieDescriptor getIMDBMovieDetails(int imdbid) throws Exception {
    Map<?, ?> response =
        invoke("GetIMDBMovieDetails", new Object[] {getToken(), Integer.valueOf(imdbid)});
    try {
      Map<?, ?> data = (Map<?, ?>) response.get("data");

      String name = (String) data.get("title");
      int year = Integer.parseInt((String) data.get("year"));

      return new OpenSubtitlesMovieDescriptor(name, year, imdbid);
    } catch (RuntimeException localRuntimeException) {}
    return null;
  }

  public synchronized boolean isLoggedOn() {
    return getToken() != null;
  }

  public List<OpenSubtitlesSubtitleDescriptor> searchSubtitles(String moviehash,
      String moviebytesize, String[] languages) throws Exception {
    Map<String, Object> queryList = new HashMap<String, Object>();
    queryList.put("moviehash", moviehash);
    queryList.put("moviebytesize", moviebytesize);
    return searchSubtitles(queryList, languages);
  }

  public List<OpenSubtitlesSubtitleDescriptor> searchSubtitles(int imdbid, String[] languages)
      throws Exception {
    Map<String, Object> queryList = new HashMap<String, Object>();
    queryList.put("imdbid", Integer.valueOf(imdbid));
    return searchSubtitles(queryList, languages);
  }

  public List<OpenSubtitlesSubtitleDescriptor> searchSubtitles(String query, String[] languages)
      throws Exception {
    Map<String, Object> queryList = new HashMap<String, Object>();
    queryList.put("query", query);
    return searchSubtitles(queryList, languages);
  }

  public List<OpenSubtitlesSubtitleDescriptor> searchSubtitles(String query, int season,
      List<Integer> episode, String[] languages) throws Exception {
    Map<String, Object> queryList = new HashMap<String, Object>();
    queryList.put("query", query);
    queryList.put("season", Integer.valueOf(season));
    queryList.put("episode", episode.get(0));
    return searchSubtitles(queryList, languages);
  }

  @SuppressWarnings("unchecked")
  public List<OpenSubtitlesSubtitleDescriptor> searchSubtitles(Map<String, Object> queryList,
      String[] languages) throws Exception {
    List<OpenSubtitlesSubtitleDescriptor> subtitles =
        new ArrayList<OpenSubtitlesSubtitleDescriptor>();

    StringBuilder result = new StringBuilder();
    String separator = ",";
    if (languages.length > 0) {
      result.append(OS_LANGS.get(languages[0]));
      for (int i = 1; i < languages.length; i++) {
        result.append(separator);
        result.append(OS_LANGS.get(languages[i]));
      }
    }

    queryList.put("sublanguageid", result.toString());

    Vector<Object> params = new Vector<Object>();
    params.add(getToken());
    params.add(new Object[] {queryList});

    Map<String, ?> response = (Map<String, ?>) invoke("SearchSubtitles", params);
    try {
      if (response.get("data") instanceof Object[]) {
        Object[] data = (Object[]) response.get("data");
        for (Object o : data) {
          subtitles.add(parseOSSubtitle((Map<String, String>) o));
        }
      }
    } catch (Exception localException) {
      LOGGER.error("searhSubtitles parsing", localException);
    }
    return subtitles;
  }

  private OpenSubtitlesSubtitleDescriptor parseOSSubtitle(Map<String, String> subtitle) {
    OpenSubtitlesSubtitleDescriptor oss = new OpenSubtitlesSubtitleDescriptor();
    oss.setUserNickName(subtitle.get("UserNickName"));
    oss.setSubFormat(subtitle.get("SubFormat"));
    oss.setIDSubtitle(Integer.parseInt(subtitle.get("IDSubtitle")));
    oss.setIDMovie(Integer.parseInt(subtitle.get("IDMovie")));
    oss.setSubBad(subtitle.get("SubBad"));
    oss.setUserID(Integer.parseInt(subtitle.get("UserID")));
    oss.setZipDownloadLink(subtitle.get("ZipDownloadLink"));
    oss.setSubSize(Long.parseLong(subtitle.get("SubSize")));
    oss.setSubFileName(subtitle.get("SubFileName"));
    oss.setSubDownloadLink(subtitle.get("SubDownloadLink"));
    oss.setUserRank(subtitle.get("UserRank"));
    oss.setSubActualCD(subtitle.get("SubActualCD"));
    oss.setMovieImdbRating(subtitle.get("MovieImdbRating"));
    oss.setSubAuthorComment(subtitle.get("SubAuthorComment"));
    oss.setSubRating(subtitle.get("SubRating"));
    oss.setSubtitlesLink(subtitle.get("SubtitlesLink"));
    oss.setSubHearingImpaired(subtitle.get("SubHearingImpaired"));
    oss.setSubHash(subtitle.get("SubHash"));
    oss.setIDSubMovieFile(Integer.parseInt(subtitle.get("IDSubMovieFile")));
    oss.setISO639(subtitle.get("ISO639"));
    oss.setSubDownloadsCnt(Integer.parseInt(subtitle.get("SubDownloadsCnt")));
    oss.setMovieHash(subtitle.get("MovieHash"));
    oss.setSubSumCD(Integer.parseInt(subtitle.get("SubSumCD")));
    oss.setSubComments(subtitle.get("SubComments"));
    oss.setMovieByteSize(Long.parseLong(subtitle.get("MovieByteSize")));
    oss.setLanguageName(subtitle.get("LanguageName"));
    oss.setMovieYear(Integer.parseInt(subtitle.get("MovieYear")));
    oss.setSubLanguageID(subtitle.get("SubLanguageID"));
    oss.setMovieReleaseName(subtitle.get("MovieReleaseName"));
    oss.setMovieTimeMS(subtitle.get("MovieTimeMS"));
    oss.setMatchedBy(subtitle.get("MatchedBy"));
    oss.setMovieName(subtitle.get("MovieName"));
    oss.setSubAddDate(subtitle.get("SubAddDate"));
    oss.setIDMovieImdb(Integer.parseInt(subtitle.get("IDMovieImdb")));
    oss.setMovieNameEng(subtitle.get("MovieNameEng"));
    oss.setIDSubtitle(Integer.parseInt(subtitle.get("IDSubtitleFile")));
    return oss;
  }

  private static final Map<String, String> OS_LANGS = Collections
      .unmodifiableMap(new HashMap<String, String>() {
        /**
	           *
	           */
        private static final long serialVersionUID = 1683941675464726802L;

        {
          put("en", "eng");
          put("fr", "fre");
          put("hu", "hun");
          put("cs", "cze");
          put("pl", "pol");
          put("sk", "slo");
          put("pt", "por");
          put("pt-br", "pob");
          put("es", "spa");
          put("el", "ell");
          put("ar", "ara");
          put("sq", "alb");
          put("hy", "arm");
          put("ay", "ass");
          put("bs", "bos");
          put("bg", "bul");
          put("ca", "cat");
          put("zh", "chi");
          put("hr", "hrv");
          put("da", "dan");
          put("nl", "dut");
          put("eo", "epo");
          put("et", "est");
          put("fi", "fin");
          put("gl", "glg");
          put("ka", "geo");
          put("de", "ger");
          put("he", "heb");
          put("hi", "hin");
          put("is", "ice");
          put("id", "ind");
          put("it", "ita");
          put("ja", "jpn");
          put("kk", "kaz");
          put("ko", "kor");
          put("lv", "lav");
          put("lt", "lit");
          put("lb", "ltz");
          put("mk", "mac");
          put("ms", "may");
          put("no", "nor");
          put("oc", "oci");
          put("fa", "per");
          put("ro", "rum");
          put("ru", "rus");
          put("sr", "scc");
          put("sl", "slv");
          put("sv", "swe");
          put("th", "tha");
          put("tr", "tur");
          put("uk", "ukr");
          put("vi", "vie");

        }
      });
}
