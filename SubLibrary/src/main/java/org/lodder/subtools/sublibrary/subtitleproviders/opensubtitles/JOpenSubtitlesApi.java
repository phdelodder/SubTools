package org.lodder.subtools.sublibrary.subtitleproviders.opensubtitles;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.subtitleproviders.XmlRPC;
import org.lodder.subtools.sublibrary.subtitleproviders.opensubtitles.model.OpenSubtitlesMovieDescriptor;
import org.lodder.subtools.sublibrary.subtitleproviders.opensubtitles.model.OpenSubtitlesSubtitleDescriptor;
import org.lodder.subtools.sublibrary.util.NamedMatcher;
import org.lodder.subtools.sublibrary.util.NamedPattern;

public class JOpenSubtitlesApi extends XmlRPC {

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

  public synchronized void login(String username, String password, String language)
      throws Exception {
    Map<String, String> response =
        invoke("LogIn", new Object[] {username, password, language, getUserAgent()});

    setToken(response.get("token").toString());
  }

  public synchronized void logout() throws MalformedURLException {
    try {
      invoke("LogOut", new Object[] {getToken()});
    } catch (Exception localXmlRpcFault) {} finally {
      setToken(null);
    }
  }

  public Map<String, String> getServerInfo() throws Exception {
    return invoke("ServerInfo", new Object[] {getToken()});
  }

  public List<OpenSubtitlesMovieDescriptor> searchMoviesOnIMDB(String title) throws Exception {
    List<OpenSubtitlesMovieDescriptor> movies = new ArrayList<OpenSubtitlesMovieDescriptor>();
    Map<?, ?> response = invoke("SearchMoviesOnIMDB", new Object[] {getToken(), title});

    List<Map> movieData = (List<Map>) response.get("data");

    NamedPattern np =
        NamedPattern.compile(
            "(?<moviename>[\\w\\s:&().,_-]+)[\\.|\\[|\\(| ]{1}(?<year>19\\d{2}|20\\d{2})", 2);

    for (Map<?, ?> movie : movieData) {
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
          Logger.instance.error(e.getMessage());
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

  public List<OpenSubtitlesSubtitleDescriptor> searchSubtitles(Map<String, Object> queryList,
      String[] languages) throws Exception {
    List<OpenSubtitlesSubtitleDescriptor> subtitles =
        new ArrayList<OpenSubtitlesSubtitleDescriptor>();

    StringBuilder result = new StringBuilder();
    String separator = ",";
    if (languages.length > 0) {
      result.append((String) OS_LANGS.get(languages[0]));
      for (int i = 1; i < languages.length; i++) {
        result.append(separator);
        result.append((String) OS_LANGS.get(languages[i]));
      }
    }

    queryList.put("sublanguageid", result.toString());

    Vector<Object> params = new Vector<Object>();
    params.add(getToken());
    params.add(new Object[] {queryList});

    Map<?, ?> response = invoke("SearchSubtitles", params);
    try {
      Object[] data = (Object[]) response.get("data");
      for (Object o : data) {
        subtitles.add(parseOSSubtitle((Map<String, String>) o));
      }
    } catch (Exception localException) {}
    return subtitles;
  }

  private OpenSubtitlesSubtitleDescriptor parseOSSubtitle(Map<String, String> subtitle) {
    OpenSubtitlesSubtitleDescriptor oss = new OpenSubtitlesSubtitleDescriptor();
    oss.setUserNickName((String) subtitle.get("UserNickName"));
    oss.setSubFormat((String) subtitle.get("SubFormat"));
    oss.setIDSubtitle(Integer.parseInt((String) subtitle.get("IDSubtitle")));
    oss.setIDMovie(Integer.parseInt((String) subtitle.get("IDMovie")));
    oss.setSubBad((String) subtitle.get("SubBad"));
    oss.setUserID(Integer.parseInt((String) subtitle.get("UserID")));
    oss.setZipDownloadLink((String) subtitle.get("ZipDownloadLink"));
    oss.setSubSize(Long.parseLong((String) subtitle.get("SubSize")));
    oss.setSubFileName((String) subtitle.get("SubFileName"));
    oss.setSubDownloadLink((String) subtitle.get("SubDownloadLink"));
    oss.setUserRank((String) subtitle.get("UserRank"));
    oss.setSubActualCD((String) subtitle.get("SubActualCD"));
    oss.setMovieImdbRating((String) subtitle.get("MovieImdbRating"));
    oss.setSubAuthorComment((String) subtitle.get("SubAuthorComment"));
    oss.setSubRating((String) subtitle.get("SubRating"));
    oss.setSubtitlesLink((String) subtitle.get("SubtitlesLink"));
    oss.setSubHearingImpaired((String) subtitle.get("SubHearingImpaired"));
    oss.setSubHash((String) subtitle.get("SubHash"));
    oss.setIDSubMovieFile(Integer.parseInt((String) subtitle.get("IDSubMovieFile")));
    oss.setISO639((String) subtitle.get("ISO639"));
    oss.setSubDownloadsCnt(Integer.parseInt((String) subtitle.get("SubDownloadsCnt")));
    oss.setMovieHash((String) subtitle.get("MovieHash"));
    oss.setSubSumCD(Integer.parseInt((String) subtitle.get("SubSumCD")));
    oss.setSubComments((String) subtitle.get("SubComments"));
    oss.setMovieByteSize(Long.parseLong((String) subtitle.get("MovieByteSize")));
    oss.setLanguageName((String) subtitle.get("LanguageName"));
    oss.setMovieYear(Integer.parseInt((String) subtitle.get("MovieYear")));
    oss.setSubLanguageID((String) subtitle.get("SubLanguageID"));
    oss.setMovieReleaseName((String) subtitle.get("MovieReleaseName"));
    oss.setMovieTimeMS((String) subtitle.get("MovieTimeMS"));
    oss.setMatchedBy((String) subtitle.get("MatchedBy"));
    oss.setMovieName((String) subtitle.get("MovieName"));
    oss.setSubAddDate((String) subtitle.get("SubAddDate"));
    oss.setIDMovieImdb(Integer.parseInt((String) subtitle.get("IDMovieImdb")));
    oss.setMovieNameEng((String) subtitle.get("MovieNameEng"));
    oss.setIDSubtitle(Integer.parseInt((String) subtitle.get("IDSubtitleFile")));
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
