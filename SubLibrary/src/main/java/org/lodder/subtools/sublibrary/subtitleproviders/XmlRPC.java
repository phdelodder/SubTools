package org.lodder.subtools.sublibrary.subtitleproviders;

import java.net.URL;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Created by IntelliJ IDEA. User: lodder Date: 20/08/11 Time: 13:22 To change
 * this template use File | Settings | File Templates.
 */
public class XmlRPC {

	private final String apiServer;
	private String userAgent;
	private String token;

	public XmlRPC(String userAgent, String apiServer) {
		this.apiServer = apiServer;
		this.userAgent = userAgent;
	}

	protected Map<String, String> invoke(String method, Object[] arguments)
			throws Exception {

		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL(getApiServer()));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		Map<String, String> response = (Map<String, String>) client.execute(
				method, arguments);
		checkResponse(response);
		return response;
	}

	protected Map<String, String> invoke(String method, Vector<Object> arguments)
			throws Exception {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL(getApiServer()));
		XmlRpcClient client = new XmlRpcClient();
		client.setConfig(config);
		Map<String, String> response = (Map<String, String>) client.execute(
				method, arguments);
		checkResponse(response);
		return response;
	}

	/**
	 * Check whether status is OK or not status code and message (e.g. 200 OK,
	 * 401 Unauthorized, ...)
	 * 
	 * @param response
	 * @throws XmlRpcFault
	 *             thrown if status code is not OK
	 */
	protected void checkResponse(Map<?, ?> response) throws Exception {
	    String status = response.get("status").toString();

	    if ((status == null) || (status.equals("200 OK")) || (status.equals("200"))) {
	      return;
	    }

	    try {
	      throw new Exception(new Scanner(status).nextInt() +" : "+ status);
	    } catch (NoSuchElementException e) {}
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
			str.append("<param><value><string>").append(value)
					.append("</string></value></param>");
		}

		str.append("</params></methodCall>");
		return str.toString();
	}

	public String generateXmlRpc(final String method,
			final Map<String, Object> arguments) {
		String str = "";
		str += "<?xml version=\"1.0\" encoding=\"utf-8\"?><methodCall><methodName>";
		str += method;
		str += "</methodName><params><param><value><string>";
		str += getToken();
		str += "</string></value></param><param><value><struct>";

		str += "<member><value><struct>";

		for (String s : arguments.keySet()) {
			str += addMapElement(s, arguments.get(s).toString());
		}

		str += "</struct></value></member>";

		str += "</struct></value></param></params></methodCall>";
		return str;
	}

	private static String addMapElement(final String name, final String value) {
		return "<member><name>" + name + "</name><value><string>"
				+ elementEncoding(value) + "</string></value></member>";
	}

	private static String elementEncoding(String a) {
		a = a.replace("&", "&amp;");
		a = a.replace("<", "&lt;");
		a = a.replace(">", "&gt;");
		a = a.replace("'", "&apos;");
		a = a.replace("\"", "&quot;");
		return a;
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
