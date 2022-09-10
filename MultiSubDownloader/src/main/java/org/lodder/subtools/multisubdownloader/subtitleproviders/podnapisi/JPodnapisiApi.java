package org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.xmlrpc.XmlRpcException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model.PodnapisiSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.ManagerException;
import org.lodder.subtools.sublibrary.ManagerSetupException;
import org.lodder.subtools.sublibrary.data.XmlRPC;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.lodder.subtools.sublibrary.xml.XmlExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lombok.experimental.ExtensionMethod;

@ExtensionMethod({ XmlExtension.class })
public class JPodnapisiApi extends XmlRPC {

    private LocalDateTime nextCheck;
    private final Manager manager;
    public static final int maxAge = 90;
    private static final Logger LOGGER = LoggerFactory.getLogger(JPodnapisiApi.class);
    private static final String DOMAIN = "https://www.podnapisi.net/";

    public JPodnapisiApi(String useragent, Manager manager) {
        super(useragent, "http://ssp.podnapisi.net:8000/RPC2/");
        this.manager = manager;
    }

    private void login() throws MalformedURLException, XmlRpcException {
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

            final byte[] md5Digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
            final BigInteger md5Number = new BigInteger(1, md5Digest);
            final String md5String = md5Number.toString(16);

            sha.update(md5String.getBytes(StandardCharsets.UTF_8));
            sha.update(nonce.getBytes(StandardCharsets.UTF_8));
            final BigInteger shaNumber = new BigInteger(1, sha.digest());
            final String shaString = shaNumber.toString(16);

            Map<?, ?> responseLogin = invoke("authenticate", new String[] { getToken(), username, shaString });
            nextCheck = LocalDateTime.now().plusSeconds(maxAge);
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
    public List<PodnapisiSubtitleDescriptor> searchSubtitles(String[] filehash, Language language) throws MalformedURLException, XmlRpcException {
        checkLoginStatus();

        List<PodnapisiSubtitleDescriptor> subtitles = new ArrayList<>();

        Map<String, List<Map<String, String>>> response = (Map) invoke("search", new Object[] { getToken(), filehash });
        try {
            List<Map<String, String>> subtitleData =
                    response.get("subtitles") == null ? new ArrayList<>() : (List<Map<String, String>>) response.get("subtitles");

            subtitleData.stream()
                    .filter(subtitle -> languageIdToLanguage(subtitle.get("LanguageCode")) == language)
                    .map(this::parsePodnapisiSubtitle)
                    .forEach(subtitles::add);
        } catch (Exception e) {
            LOGGER.error("API PODNAPISI searchSubtitles", e);
        }
        return subtitles;
    }

    public List<PodnapisiSubtitleDescriptor> searchSubtitles(String filename, int year, int season, int episode, Language language) {
        StringBuilder url = new StringBuilder(DOMAIN + "sl/ppodnapisi/search?sK=")
                .append(URLEncoder.encode(filename, StandardCharsets.UTF_8));
        if (PODNAPISI_LANGS.containsKey(language)) {
            url.append("&sJ=").append(PODNAPISI_LANGS.get(language));
        }
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
            return doc.getElementsByTagName("subtitle").stream()
                    .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                    .filter(node -> "subtitle".equals(node.getNodeName()))
                    .map(Element.class::cast)
                    .map(this::parsePodnapisiSubtitle)
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    @SuppressWarnings("unchecked")
    public String download(String subtitleId) throws MalformedURLException, XmlRpcException {
        checkLoginStatus();

        Map<?, ?> response = invoke("download", new Object[] { getToken(), subtitleId });
        try {
            List<Map<String, String>> data = (List<Map<String, String>>) response.get("names");
            return DOMAIN + "static/podnapisi/" + data.get(0).get("filename");
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
            return DOMAIN + url;
        } else {
            LOGGER.error("Download URL for subtitleID {} can't be found, set to debug for more information!", subtitleId);
            LOGGER.debug("The URL {}", url);
            LOGGER.debug("The Page {}", xml);
            return null;
        }
    }

    private void checkLoginStatus() throws MalformedURLException, XmlRpcException {
        if (!isLoggedOn()) {
            login();
        } else {
            keepAlive();
        }
    }

    private void keepAlive() throws MalformedURLException, XmlRpcException {
        if (LocalDateTime.now().isAfter(nextCheck)) {
            invoke("keepalive", new Object[] { getToken() });
            nextCheck = LocalDateTime.now().plusSeconds(maxAge);
        }
    }

    protected Document getXML(String url) {
        try {
            return XMLHelper.getDocument(manager.getContent(url, getUserAgent(), false));
        } catch (Exception e) {
            LOGGER.error("API PODNAPISI getXML", e);
        }
        return null;
    }

    private PodnapisiSubtitleDescriptor parsePodnapisiSubtitle(Element eElement) {
        PodnapisiSubtitleDescriptor psd = new PodnapisiSubtitleDescriptor();
        psd.setFlagsString(XMLHelper.getStringTagValue("flags", eElement));
        psd.setLanguage(languageIdToLanguage(XMLHelper.getStringTagValue("languageId", eElement)));
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
        psd.setLanguage(languageIdToLanguage(subtitle.get("LanguageCode")));
        psd.setMatchRanking(subtitle.get("MatchRanking"));
        psd.setReleaseString(subtitle.get("ReleaseString"));
        psd.setSubtitleId(subtitle.get("SubtitleId"));
        psd.setSubtitleRating(subtitle.get("SubtitleRating"));
        psd.setUploaderName(subtitle.get("UploaderName"));
        psd.setUploaderUid(subtitle.get("UploaderUid"));
        return psd;
    }

    private Language languageIdToLanguage(String languageId) {
        return PODNAPISI_LANGS.entrySet().stream().filter(entry -> entry.getValue().equals(languageId)).map(Entry::getKey).findFirst().orElse(null);
    }

    private static final Map<Language, String> PODNAPISI_LANGS = Collections
            .unmodifiableMap(new EnumMap<>(Language.class) {
                private static final long serialVersionUID = 2950169212654074275L;

                {
                    put(Language.SLOVENIAN, "1");
                    put(Language.ENGLISH, "2");
                    put(Language.NORWEGIAN, "3");
                    put(Language.KOREAN, "4");
                    put(Language.GERMAN, "5");
                    put(Language.ICELANDIC, "6");
                    put(Language.CZECH, "7");
                    put(Language.FRENCH, "8");
                    put(Language.ITALIAN, "9");
                    put(Language.BOSNIAN, "10");
                    put(Language.JAPANESE, "11");
                    put(Language.ARABIC, "12");
                    put(Language.ROMANIAN, "13");
                    put(Language.SPANISH, "14"); // es-ar Spanish (Argentina)
                    put(Language.HUNGARIAN, "15");
                    put(Language.GREEK, "16");
                    put(Language.CHINESE_SIMPLIFIED, "17");
                    put(Language.LITHUANIAN, "19");
                    put(Language.ESTONIAN, "20");
                    put(Language.LATVIAN, "21");
                    put(Language.HEBREW, "22");
                    put(Language.DUTCH, "23");
                    put(Language.DANISH, "24");
                    put(Language.SWEDISH, "25");
                    put(Language.POLISH, "26");
                    put(Language.RUSSIAN, "27");
                    put(Language.SPANISH, "28");
                    put(Language.ALBANIAN, "29");
                    put(Language.TURKISH, "30");
                    put(Language.FINNISH, "31");
                    put(Language.PORTUGUESE, "32");
                    put(Language.BULGARIAN, "33");
                    put(Language.MACEDONIAN, "35");
                    put(Language.SLOVAK, "37");
                    put(Language.CROATIAN, "38");
                    put(Language.CHINESE_SIMPLIFIED, "40");
                    put(Language.HINDI, "42");
                    put(Language.THAI, "44");
                    put(Language.UKRAINIAN, "46");
                    put(Language.SERBIAN, "47");
                    put(Language.PORTUGUESE, "48"); // Portuguese (Brazil)
                    put(Language.IRISH, "49");
                    put(Language.BELARUSIAN, "50");
                    put(Language.VIETNAMESE, "51");
                    put(Language.PERSIAN, "52");
                    put(Language.CATALAN, "53");
                    put(Language.INDONESIAN, "54");

                }
            });

}
