package org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi;

import org.lodder.subtools.multisubdownloader.subtitleproviders.podnapisi.model.PodnapisiSubtitleDescriptor;
import org.lodder.subtools.sublibrary.cache.CacheManager;
import org.lodder.subtools.sublibrary.data.XmlRPC;
import org.lodder.subtools.sublibrary.logging.Logger;
import org.lodder.subtools.sublibrary.xml.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: lodder
 * Date: 20/08/11
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */
public class JPodnapisiApi extends XmlRPC{

    private final CacheManager ucm;
    private Date lastCheck;
    public static final int maxAge =  90000;
    	
    public JPodnapisiApi(String useragent){
        super(useragent, "http://ssp.podnapisi.net:8000/RPC2/");
        ucm = CacheManager.getURLCache();
    }

    private void login() throws Exception {
        Map<?, ?> response = invoke("initiate", new String[]{getUserAgent()});
        setToken(response.get("session").toString());
        String nonce = response.get("nonce").toString();
        String username = "jbiersubsdownloader";
        String password = "jbiersubsdownloader3";

        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            final MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.reset();

            final byte[] md5Digest = md.digest(password.getBytes());
            final BigInteger md5Number = new BigInteger(1, md5Digest);
            final String md5String = md5Number.toString(16);

            sha.update(md5String.getBytes());
            sha.update(nonce.getBytes());
            final BigInteger shaNumber = new BigInteger(1, sha.digest());
            final String shaString =shaNumber.toString(16);

            Map<?, ?> responseLogin = invoke("authenticate", new String[]{getToken(), username, shaString});
            lastCheck = new Date(System.currentTimeMillis() + maxAge);
            if (!responseLogin.get("status").toString().equals("200"))
                setToken(null);
        } catch (NoSuchAlgorithmException e) {
        	Logger.instance.error(Logger.stack2String(e));
            setToken(null);
        }
    }

    public boolean isLoggedOn() {
        return this.getToken() != null;
    }

    public List<PodnapisiSubtitleDescriptor> searchSubtitles(String[] filehash, String sublanguageid) throws Exception {
    	checkLoginStatus();

        List<PodnapisiSubtitleDescriptor> subtitles = new ArrayList<PodnapisiSubtitleDescriptor>();

        Map<?, ?> response = invoke("search", new Object[] {getToken(), filehash});
        try {
            List<Map<String, String>> subtitleData = (response.get("subtitles") == null) ?
                new ArrayList<Map<String, String>>() :
                (List<Map<String, String>>) response.get("subtitles");

            for (Map<String, String> subtitle : subtitleData) {
                if (subtitle.get("LanguageCode").equals(PODNAPISI_LANGS.get(sublanguageid)))
                    subtitles.add(parsePodnapisiSubtitle(subtitle));
            }
        } catch (Exception e) {
            Logger.instance.error(Logger.stack2String(e));
        }
        return subtitles;
    }

    public List<PodnapisiSubtitleDescriptor> searchSubtitles(String filename, int year, int season, int episode, String sublanguageid) throws IOException {
        List<PodnapisiSubtitleDescriptor> subtitles = new ArrayList<PodnapisiSubtitleDescriptor>();

        String url = "http://www.podnapisi.net/sl/ppodnapisi/search?sK="+ URLEncoder.encode(filename, "UTF-8") +"&sJ="+ PODNAPISI_LANGS.get(sublanguageid);
        if (year > 0)
            url = url + "&sY=" + year;
        if (season > 0){
        	url = url + "&sTS=" + season + "&sT=1"; //series 
        }else{
        	url = url + "&sT=0"; //movies
        }
        if (episode > 0)
        	url = url + "&sTE=" + episode;
        url = url +"&sXML=1";

        Document doc = getXML(url);

        NodeList nList = doc.getElementsByTagName("subtitle");

        for (int i = 0; i < nList.getLength(); i++) {
            if(nList.item(i).getNodeType() == Node.ELEMENT_NODE && nList.item(i).getNodeName().equals("subtitle")){
                Element eElement = (Element) nList.item(i);
                subtitles.add(parsePodnapisiSubtitle(eElement));
            }
        }

        return subtitles;
    }

    public String download(String subtitleId) throws Exception {
    	checkLoginStatus();        

        Map<?, ?> response = invoke("download", new Object[] {getToken(), subtitleId});
        try {
            List<Map<String, String>> data = (List<Map<String, String>>) response.get("names");
            return "http://www.podnapisi.net/static/podnapisi/" + data.get(0).get("filename");
        } catch (Exception e) {
        	Logger.instance.error(Logger.stack2String(e));
        }
        return null;
    }

    public String downloadUrl(String subtitleId) throws Exception {
        String url = "http://simple.podnapisi.net/en/ondertitels-p"+subtitleId;
    	ucm.setRatelimit(0);
    	String xml = ucm.fetchAsString(new URL(url), 900);
    	int beginIndex = xml.indexOf("/ppodnapisi/predownload/i/");
    	if (beginIndex > 0) {
    		StringTokenizer st = new StringTokenizer(xml.substring(beginIndex - 3), "\"");
        	url = st.nextToken();
        	url = url.replace("predownload", "download");
        	return "http://simple.podnapisi.net" + url;
    	} else {
    		Logger.instance.error("Download URL for subtitleID:"
    				+ subtitleId
    				+ " can't be found, set to debug for more information!");
    		Logger.instance.debug("The URL:" + url);
    		Logger.instance.debug("The Page:" + xml);
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
		if (lastCheck.compareTo(current) < 0){
			invoke("keepalive", new Object[] {getToken()});
			lastCheck = new Date(System.currentTimeMillis() + maxAge);
		}
	}

	protected Document getXML(String url) {
        try {
            return XMLHelper.getDocument(ucm.fetchAsInputStream(new URL(url), 900));
        } catch (MalformedURLException e) {
            Logger.instance.error(Logger.stack2String(e));
        } catch (ParserConfigurationException e) {
            Logger.instance.error(Logger.stack2String(e));
        } catch (SAXException e) {
            Logger.instance.error(Logger.stack2String(e));
        } catch (IOException e) {
            Logger.instance.error(Logger.stack2String(e));
        } catch (Exception e) {
            Logger.instance.error(Logger.stack2String(e));
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
        //psd.setInexact(subtitle.get("Inexact"));
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
