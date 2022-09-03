package org.lodder.subtools.sublibrary.data;

import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class XmlRPC {

    private final String apiServer;
    private String userAgent;
    private String token;

    public XmlRPC(String userAgent, String apiServer) {
        this.apiServer = apiServer;
        this.userAgent = userAgent;
    }

    protected Map<?, ?> invoke(String method, Object[] arguments) throws Exception {

        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(getApiServer()));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        Map<?, ?> response = (Map<?, ?>) client.execute(method, arguments);
        checkResponse(response);
        return response;
    }

    protected Map<?, ?> invoke(String method, Vector<Object> arguments) throws Exception {
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(getApiServer()));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(config);
        Map<?, ?> response = (Map<?, ?>) client.execute(method, arguments);
        checkResponse(response);
        return response;
    }

    /**
     * Check whether status is OK or not status code and message (e.g. 200 OK, 401 Unauthorized, ...)
     *
     * @param response
     * @throws XmlRpcFault thrown if status code is not OK
     */
    protected void checkResponse(Map<?, ?> response) throws Exception {
        String status = response.get("status").toString();

        if (status == null || "200 OK".equals(status) || "200".equals(status)) {
            return;
        }

        try {
            Scanner scanner = new Scanner(status);
            int nextInt = scanner.nextInt();
            scanner.close();
            throw new Exception(nextInt + " : " + status);
        } catch (NoSuchElementException e) {
        }
        throw new XmlRpcException("Illegal status code: " + status);
    }

    public String getApiServer() {
        return apiServer;
    }

    public String generateXmlRpc(String procname, String s[]) {
        StringBuilder str = new StringBuilder();
        str.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><methodCall><methodName>");
        str.append(procname).append("</methodName><params>");

        for (String value : s) {
            str.append("<param><value><string>").append(value).append("</string></value></param>");
        }

        str.append("</params></methodCall>");
        return str.toString();
    }

    public String generateXmlRpc(final String method, final Map<String, Object> arguments) {
        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?><methodCall><methodName>");
        sb.append(method);
        sb.append("</methodName><params><param><value><string>");
        sb.append(getToken());
        sb.append("</string></value></param><param><value><struct>");
        sb.append("<member><value><struct>");

        for (Entry<String, Object> e : arguments.entrySet()) {
            sb.append(addMapElement(e.getKey(), e.getValue().toString()));
        }

        sb.append("</struct></value></member>");

        sb.append("</struct></value></param></params></methodCall>");

        return sb.toString();
    }

    private static String addMapElement(final String name, final String value) {
        return "<member><name>" + name + "</name><value><string>" + elementEncoding(value)
                + "</string></value></member>";
    }

    private static String elementEncoding(String a) {
        a = a.replace("&", "&amp;");
        a = a.replace("<", "&lt;");
        a = a.replace(">", "&gt;");
        a = a.replace("'", "&apos;");
        return a.replace("\"", "&quot;");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
