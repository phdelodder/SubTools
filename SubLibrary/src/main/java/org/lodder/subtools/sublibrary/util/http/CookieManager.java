package org.lodder.subtools.sublibrary.util.http;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

/**
 * CookieManager is a simple utilty for handling cookies when working with java.net.URL and
 * java.net.URLConnection objects.
 *
 *
 * Cookiemanager cm = new CookieManager(); URL url = new
 * URL("http://www.hccp.org/test/cookieTest.jsp");
 *
 * . . .
 *
 * // getting cookies: URLConnection conn = url.openConnection(); conn.connect();
 *
 * // setting cookies cm.storeCookies(conn); cm.setCookies(url.openConnection());
 *
 * @author Ian Brown
 *
 **/

public class CookieManager {

    private final Map<String, Map<String, Map<String, String>>> store;

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE_VALUE_DELIMITER = ";";
    private static final String PATH = "path";
    private static final String EXPIRES = "expires";
    private static final String DATE_FORMAT = "EEE, dd-MMM-yyyy hh:mm:ss z";
    private static final String SET_COOKIE_SEPARATOR = "; ";
    private static final String COOKIE = "Cookie";

    private static final char NAME_VALUE_SEPARATOR = '=';
    private static final char DOT = '.';

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public CookieManager() {
        store = new HashMap<>();
    }

    /**
     * Retrieves and stores cookies returned by the host on the other side of the the open
     * java.net.URLConnection.
     *
     * The connection MUST have been opened using the connect() method or a IOException will be
     * thrown.
     *
     * @param conn a java.net.URLConnection - must be open, or IOException will be thrown
     * @throws java.io.IOException Thrown if conn is not open.
     */
    public void storeCookies(URLConnection conn) throws IOException {

        // let's determine the domain from where these cookies are being sent
        String domain = getDomainFromHost(conn.getURL().getHost());

        Map<String, Map<String, String>> domainStore; // this is where we will store cookies for this
                                                      // domain

        // now let's check the store to see if we have an entry for this domain
        if (store.containsKey(domain)) {
            // we do, so lets retrieve it from the store
            domainStore = store.get(domain);
        } else {
            // we don't, so let's create it and put it in the store
            domainStore = new HashMap<>();
            store.put(domain, domainStore);
        }

        // OK, now we are ready to get the cookies out of the URLConnection

        String headerName = null;
        for (int i = 1; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
            if (SET_COOKIE.equalsIgnoreCase(headerName)) {
                Map<String, String> cookie = new HashMap<>();
                StringTokenizer st = new StringTokenizer(conn.getHeaderField(i), COOKIE_VALUE_DELIMITER);

                // the specification dictates that the first name/value pair
                // in the string is the cookie name and value, so let's handle
                // them as a special case:

                if (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    String name = token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR));
                    String value = token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1);
                    domainStore.put(name, cookie);
                    cookie.put(name, value);
                }

                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    cookie.put(token.substring(0, token.indexOf(NAME_VALUE_SEPARATOR)).toLowerCase(),
                            token.substring(token.indexOf(NAME_VALUE_SEPARATOR) + 1));
                }
            }
        }
    }

    /**
     * Prior to opening a URLConnection, calling this method will set all unexpired cookies that match
     * the path or subpaths for thi underlying URL
     *
     * The connection MUST NOT have been opened method or an IOException will be thrown.
     *
     * @param conn a java.net.URLConnection - must NOT be open, or IOException will be thrown
     * @throws java.io.IOException Thrown if conn has already been opened.
     */
    public void setCookies(URLConnection conn) throws IOException {

        // let's determine the domain and path to retrieve the appropriate
        // cookies
        URL url = conn.getURL();
        String domain = getDomainFromHost(url.getHost());
        String path = url.getPath();

        Map<String, Map<String, String>> domainStore = store.get(domain);
        if (domainStore == null) {
            return;
        }
        StringBuilder cookieStringBuffer = new StringBuilder();

        for (Entry<String, Map<String, String>> entry : domainStore.entrySet()) {
            Map<String, String> cookie = entry.getValue();
            // check cookie to ensure path matches and cookie is not expired
            // if all is cool, add cookie to header string
            if (comparePaths(cookie.get(PATH), path)
                    && isNotExpired(cookie.get(EXPIRES))) {
                if (cookieStringBuffer.length() > 0) {
                    cookieStringBuffer.append(SET_COOKIE_SEPARATOR);
                }

                cookieStringBuffer.append(entry.getKey());
                cookieStringBuffer.append("=");
                cookieStringBuffer.append(cookie.get(entry.getKey()));
            }
        }

        try {
            conn.setRequestProperty(COOKIE, cookieStringBuffer.toString());
        } catch (java.lang.IllegalStateException ise) {
            throw new IOException(
                    "Illegal State! Cookies cannot be set on a URLConnection that is already connected. "
                            + "Only call setCookies(java.net.URLConnection) AFTER calling java.net.URLConnection.connect().");
        }
    }

    private String getDomainFromHost(String host) {
        if (host.indexOf(DOT) != host.lastIndexOf(DOT)) {
            return host.substring(host.indexOf(DOT) + 1);
        } else {
            return host;
        }
    }

    private boolean isNotExpired(String cookieExpires) {
        if (cookieExpires == null) {
            return true;
        }
        try {
            return LocalDateTime.now().isBefore(LocalDateTime.parse(cookieExpires, DATE_FORMATTER));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean comparePaths(String cookiePath, String targetPath) {
        if (cookiePath == null) {
            return true;
        } else if ("/".equals(cookiePath)) {
            return true;
        } else {
            return targetPath.regionMatches(0, cookiePath, 0, cookiePath.length());
        }

    }

    /**
     * Returns a string representation of stored cookies organized by domain.
     */
    @Override
    public String toString() {
        return store.toString();
    }
}
