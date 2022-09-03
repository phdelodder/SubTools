package org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model.PodnapisiSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.data.XmlRPC;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JPodnapisiApi extends XmlRPC {

    private Date lastCheck;
    private Manager manager;
    public static final int maxAge = 90000;
    private static final Logger LOGGER = LoggerFactory.getLogger(JPodnapisiApi.class);

    public JPodnapisiApi(String useragent, Manager manager) {
        super(useragent, "http://ssp.podnapisi.net:8000/RPC2/");
        this.manager = manager;
    }

    private void login() throws Exception {
        Map<?, ?> response = invoke("initiate", new String[] { getUserAgent() });
        setToken(response.get("session").toString());
        String nonce = response.get("nonce").toString();
        String username = "jbiersubsdownloader";
        String password = "jbiersubsdownloader3";

        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            final MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.reset();

            final byte[] md5Digest = md.digest(password.getBytes("UTF-8"));
            final BigInteger md5Number = new BigInteger(1, md5Digest);
            final String md5String = md5Number.toString(16);

            sha.update(md5String.getBytes("UTF-8"));
            sha.update(nonce.getBytes("UTF-8"));
            final BigInteger shaNumber = new BigInteger(1, sha.digest());
            final String shaString = shaNumber.toString(16);

            Map<?, ?> responseLogin =
                    invoke("authenticate", new String[] { getToken(), username, shaString });
            lastCheck = new Date(System.currentTimeMillis() + maxAge);
            if (!"200".equals(responseLogin.get("status").toString())) {
                setToken(null);
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("API PODNAPISI login", e);
            setToken(null);
        }
    }

    public boolean isLoggedOn() {
        return this.getToken() != null;
    }

    @SuppressWarnings("unchecked")
    public List<PodnapisiSubtitleDescriptor> searchSubtitles(String[] filehash, String sublanguageid)
            throws Exception {
        checkLoginStatus();

        List<PodnapisiSubtitleDescriptor> subtitles = new ArrayList<>();

        Map<String, List<Map<String, String>>> response =
                (Map<String, List<Map<String, String>>>) invoke("search", new Object[] { getToken(),
                        filehash });
        try {
            List<Map<String, String>> subtitleData =
                    response.get("subtitles") == null
                            ? new ArrayList<>()
                            : (List<Map<String, String>>) response.get("subtitles");

            for (Map<String, String> subtitle : subtitleData) {
                if (subtitle.get("LanguageCode").equals(PODNAPISI_LANGS.get(sublanguageid))) {
                    subtitles.add(parsePodnapisiSubtitle(subtitle));
                }
            }
        } catch (Exception e) {
            LOGGER.error("API PODNAPISI searchSubtitles", e);
        }
        return subtitles;
    }

    public List<PodnapisiSubtitleDescriptor> searchSubtitles(String filename, int year, int season,
            int episode, String sublanguageid) throws IOException {
        List<PodnapisiSubtitleDescriptor> subtitles = new ArrayList<>();

        StringBuilder url =
                new StringBuilder("http://www.podnapisi.net/sl/ppodnapisi/search?sK=").append(URLEncoder.encode(filename, "UTF-8")).append("&sJ=").append(PODNAPISI_LANGS.get(sublanguageid));
        if (year > 0) {
            url.append("&sY=").append(year);
        }
        if (season > 0) {
            url.append("&sTS=").append(season).append("&sT=1"); // series
        } else {
            url.append("&sT=0"); // movies
        }
        if (episode > 0) {
            url.append("&sTE=").append(episode);
        }
        url.append("&sXML=1");

        Document doc = getXML(url.toString());

        if (doc != null) {

            NodeList nList = doc.getElementsByTagName("subtitle");

            for (int i = 0; i < nList.getLength(); i++) {
                if (nList.item(i).getNodeType() == Node.ELEMENT_NODE
                        && "subtitle".equals(nList.item(i).getNodeName())) {
                    Element eElement = (Element) nList.item(i);
                    subtitles.add(parsePodnapisiSubtitle(eElement));
                }
            }

        }

        return subtitles;
    }

    @SuppressWarnings("unchecked")
    public String download(String subtitleId) throws Exception {
        checkLoginStatus();

        Map<?, ?> response = invoke("download", new Object[] { getToken(), subtitleId });
        try {
            List<Map<String, String>> data = (List<Map<String, String>>) response.get("names");
            return "http://www.podnapisi.net/static/podnapisi/" + data.get(0).get("filename");
        } catch (Exception e) {
            LOGGER.error("API PODNAPISI download", e);
        }
        return null;
    }

    public String downloadUrl(String subtitleId) throws ManagerSetupException, ManagerException {
        String url = "http://simple.podnapisi.net/en/ondertitels-p" + subtitleId;
        String xml = manager.getContent(url, getUserAgent(), false);
        int downloadStartIndex = xml.indexOf("/download");
        int startIndex = 0;
        if (downloadStartIndex > 0) {
            // get starting point of string
            for (int i = downloadStartIndex; i > 0; i--) {
                if (xml.charAt(i) == '=') {
                    startIndex = i;
                    break;
                }
            }
            url = xml.substring(startIndex + 2, downloadStartIndex + 9);
            return "http://www.podnapisi.net/" + url;
        } else {
            LOGGER.error(
                    "Download URL for subtitleID {} can't be found, set to debug for more information!",
                    subtitleId);
            LOGGER.debug("The URL {}", url);
            LOGGER.debug("The Page {}", xml);
            return null;
        }
    }

    private void checkLoginStatus() throws Exception {
        if (!isLoggedOn()) {
            login();
        } else {
            keepAlive();
        }
    }

    private void keepAlive() throws Exception {
        final Date current = new Date();
        if (lastCheck.compareTo(current) < 0) {
            invoke("keepalive", new Object[] { getToken() });
            lastCheck = new Date(System.currentTimeMillis() + maxAge);
        }
    }

    protected Document getXML(String url) {
        try {
            return XMLHelper.getDocument(manager.getContent(url, getUserAgent(), false));
        } catch (ParserConfigurationException | IOException e) {
            LOGGER.error("API PODNAPISI getXML", e);
        } catch (Exception e) {
            LOGGER.error("API PODNAPISI getXML", e);
        }
        return null;
    }

    private PodnapisiSubtitleDescriptor parsePodnapisiSubtitle(Element eElement) {
        PodnapisiSubtitleDescriptor psd = new PodnapisiSubtitleDescriptor();
        psd.setFlagsString(XMLHelper.getStringTagValue("flags", eElement));
        psd.setLanguageCode(XMLHelper.getStringTagValue("languageId", eElement));
        psd.setMatchRanking(XMLHelper.getStringTagValue("rating", eElement));
        psd.setReleaseString(XMLHelper.getStringTagValue("release", eElement));
        psd.setSubtitleId(XMLHelper.getStringTagValue("id", eElement));
        psd.setSubtitleRating(XMLHelper.getStringTagValue("rating", eElement));
        psd.setUploaderName(XMLHelper.getStringTagValue("uploaderName", eElement));
        psd.setUploaderUid(XMLHelper.getStringTagValue("uploaderId", eElement));
        return psd;
    }

    private PodnapisiSubtitleDescriptor parsePodnapisiSubtitle(Map<String, String> subtitle) {
        PodnapisiSubtitleDescriptor psd = new PodnapisiSubtitleDescriptor();
        psd.setFlagsString(subtitle.get("FlagsString"));
        // psd.setInexact(subtitle.get("Inexact"));
        psd.setLanguageCode(subtitle.get("LanguageCode"));
        psd.setMatchRanking(subtitle.get("MatchRanking"));
        psd.setReleaseString(subtitle.get("ReleaseString"));
        psd.setSubtitleId(subtitle.get("SubtitleId"));
        psd.setSubtitleRating(subtitle.get("SubtitleRating"));
        psd.setUploaderName(subtitle.get("UploaderName"));
        psd.setUploaderUid(subtitle.get("UploaderUid"));
        return psd;
    }

    private static final Map<String, String> PODNAPISI_LANGS = Collections
            .unmodifiableMap(new HashMap<String, String>() {
                /**
                		 *
                		 */
                private static final long serialVersionUID = 2950169212654074275L;

                {
                    put("sl", "1");
                    put("en", "2");
                    put("no", "3");
                    put("ko", "4");
                    put("de", "5");
                    put("is", "6");
                    put("cs", "7");
                    put("fr", "8");
                    put("it", "9");
                    put("bs", "10");
                    put("ja", "11");
                    put("ar", "12");
                    put("ro", "13");
                    put("es-ar", "14");
                    put("hu", "15");
                    put("el", "16");
                    put("zh", "17");
                    put("lt", "19");
                    put("et", "20");
                    put("lv", "21");
                    put("he", "22");
                    put("nl", "23");
                    put("da", "24");
                    put("se", "25");
                    put("pl", "26");
                    put("ru", "27");
                    put("es", "28");
                    put("sq", "29");
                    put("tr", "30");
                    put("fi", "31");
                    put("pt", "32");
                    put("bg", "33");
                    put("mk", "35");
                    put("sk", "37");
                    put("hr", "38");
                    put("zh", "40");
                    put("hi", "42");
                    put("th", "44");
                    put("uk", "46");
                    put("sr", "47");
                    put("pt-br", "48");
                    put("ga", "49");
                    put("be", "50");
                    put("vi", "51");
                    put("fa", "52");
                    put("ca", "53");
                    put("id", "54");

                }
            });

}
