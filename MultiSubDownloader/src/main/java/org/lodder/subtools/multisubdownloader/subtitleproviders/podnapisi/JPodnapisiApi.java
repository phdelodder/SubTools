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
import java.util.Optional;

import org.apache.xmlrpc.XmlRpcException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.lodder.subtools.multisubdownloader.subtitleproviders.SubtitleApi;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.exception.PodnapisiException;
import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model.PodnapisiSubtitleDescriptor;
import org.lodder.subtools.sublibrary.Language;
import org.lodder.subtools.sublibrary.Manager;
import org.lodder.subtools.sublibrary.cache.CacheType;
import org.lodder.subtools.sublibrary.data.ProviderSerieId;
import org.lodder.subtools.sublibrary.data.XmlRPC;
import org.lodder.subtools.sublibrary.model.SubtitleSource;
import org.lodder.subtools.sublibrary.settings.model.SerieMapping;
import org.lodder.subtools.sublibrary.util.OptionalExtension;
import org.lodder.subtools.sublibrary.util.http.HttpClientException;
import org.lodder.subtools.sublibrary.xml.XmlExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;

@Getter(value = AccessLevel.PROTECTED)
@ExtensionMethod({ XmlExtension.class, OptionalExtension.class })
public class JPodnapisiApi extends XmlRPC implements SubtitleApi {

    public static final int maxAge = 90;
    private static final Logger LOGGER = LoggerFactory.getLogger(JPodnapisiApi.class);
    private static final String DOMAIN = "https://www.podnapisi.net";
    private LocalDateTime nextCheck;
    @Getter
    private final Manager manager;

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

    public Optional<ProviderSerieId> getPodnapisiShowName(String showName) throws PodnapisiException {
        String url = DOMAIN + "/sl/ppodnapisi/search?sK=" + URLEncoder.encode(showName.trim().toLowerCase(), StandardCharsets.UTF_8);
        return getXML(url).selectFirst(".subtitle-entry") != null
                ? Optional.of(new ProviderSerieId(showName, showName))
                : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public List<PodnapisiSubtitleDescriptor> getSubtitles(String[] filehash, Language language) throws PodnapisiException {
        return getManager().valueBuilder().memoryCache()
                .key("%s-subtitles-%s-%s".formatted(getSubtitleSource().getName(), filehash, language))
                .collectionSupplier(PodnapisiSubtitleDescriptor.class, () -> {
                    try {
                        checkLoginStatus();
                        Map<String, List<Map<String, String>>> response =
                                (Map<String, List<Map<String, String>>>) invoke("search", new Object[] { getToken(), filehash });
                        List<Map<String, String>> subtitleData =
                                response.get("subtitles") == null ? new ArrayList<>() : (List<Map<String, String>>) response.get("subtitles");

                        return subtitleData.stream()
                                .filter(subtitle -> languageIdToLanguage(subtitle.get("LanguageCode")) == language)
                                .map(this::parsePodnapisiSubtitle)
                                .toList();
                    } catch (Exception e) {
                        throw new PodnapisiException(e);
                    }
                }).getCollection();
    }

    public List<PodnapisiSubtitleDescriptor> getMovieSubtitles(String movieName, int year, int season, int episode, Language language)
            throws PodnapisiException {
        return getSubtitles(new SerieMapping(movieName, movieName, movieName), year, season, episode, language);

    }

    public List<PodnapisiSubtitleDescriptor> getSerieSubtitles(SerieMapping providerSerieId, int season, int episode, Language language)
            throws PodnapisiException {
        return getSubtitles(providerSerieId, null, season, episode, language);

    }

    private List<PodnapisiSubtitleDescriptor> getSubtitles(SerieMapping providerSerieId, Integer year, int season, int episode, Language language)
            throws PodnapisiException {
        return getManager().valueBuilder()
                .memoryCache()
                .key("%s-subtitles-%s-%s-%s-%s".formatted(getSubtitleSource().name(), providerSerieId.getProviderId(), season, episode, language))
                .collectionSupplier(PodnapisiSubtitleDescriptor.class, () -> {
                    try {
                        StringBuilder url = new StringBuilder(DOMAIN + "/sl/ppodnapisi/search?sK=")
                                .append(URLEncoder.encode(providerSerieId.getProviderId().trim().toLowerCase(), StandardCharsets.UTF_8));
                        if (PODNAPISI_LANGS.containsKey(language)) {
                            url.append("&sJ=").append(PODNAPISI_LANGS.get(language));
                        }
                        if (year != null) {
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

                        return getXML(url.toString()).select(".subtitle-entry").stream().map(this::parsePodnapisiSubtitle).toList();
                    } catch (Exception e) {
                        throw new PodnapisiException(e);
                    }
                })
                .getCollection();
    }

    @SuppressWarnings("unchecked")
    public String download(String subtitleId) throws PodnapisiException {
        try {
            checkLoginStatus();

            Map<?, ?> response = invoke("download", new Object[] { getToken(), subtitleId });
            List<Map<String, String>> data = (List<Map<String, String>>) response.get("names");
            return DOMAIN + "/static/podnapisi/" + data.get(0).get("filename");
        } catch (Exception e) {
            throw new PodnapisiException(e);
        }
    }

    public Optional<String> downloadUrl(String subtitleId) throws PodnapisiException {
        try {
            String url = DOMAIN + "/en/ondertitels-p" + subtitleId;
            String xml = manager.getPageContentBuilder().url(url).userAgent(getUserAgent()).cacheType(CacheType.NONE).get();
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
                return Optional.of(DOMAIN + "/" + url);
            } else {
                LOGGER.error("Download URL for subtitleID {} can't be found, set to debug for more information!", subtitleId);
                LOGGER.debug("The URL {}", url);
                LOGGER.debug("The Page {}", xml);
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new PodnapisiException(e);
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

    protected Document getXML(String url) throws PodnapisiException {
        try {
            return manager.getPageContentBuilder().url(url).userAgent(getUserAgent()).cacheType(CacheType.MEMORY).retries(1)
                    .retryPredicate(e -> e instanceof HttpClientException httpClientException && httpClientException.getResponseCode() >= 500
                            && httpClientException.getResponseCode() < 600)
                    .retryWait(5).getAsJsoupDocument();
        } catch (Exception e) {
            throw new PodnapisiException(e);
        }
    }

    private PodnapisiSubtitleDescriptor parsePodnapisiSubtitle(Element elem) {
        return PodnapisiSubtitleDescriptor.builder()
                .hearingImpaired(elem.selectFirst(".flags i[data-content='Hearing impaired']") != null)
                .language(languageIdToLanguage(elem.selectFirst(".language").text()))
                .releaseString(elem.selectFirst(".release").text())
                .uploaderName(elem.select("td").get(4).select("a").text())
                .url(elem.selectFirst("td a").attr("href")).build();
    }

    private PodnapisiSubtitleDescriptor parsePodnapisiSubtitle(Map<String, String> subtitle) {
        return PodnapisiSubtitleDescriptor.builder()
                // .flagsString(subtitle.get("FlagsString"))
                // psd.setInexact(subtitle.get("Inexact"))
                .language(languageIdToLanguage(subtitle.get("LanguageCode")))
                // .matchRanking(subtitle.get("MatchRanking"))
                .releaseString(subtitle.get("ReleaseString"))
                // .subtitleId(subtitle.get("SubtitleId"))
                // .subtitleRating(subtitle.get("SubtitleRating"))
                .uploaderName(subtitle.get("UploaderName"))
                // .uploaderUid(subtitle.get("UploaderUid"))
                .url(subtitle.get("url")).build();
    }

    @Override
    public SubtitleSource getSubtitleSource() {
        return SubtitleSource.PODNAPISI;
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
