package org.lodder.subtools.sublibrary.util.http;

import java.io.IOException;
import java.io.Serial;
import java.net.HttpURLConnection;

import manifold.ext.props.rt.api.val;

public class HttpClientException extends Exception {

    @Serial
    private static final long serialVersionUID = 5583416046207372599L;
    @val int responseCode;
    @val String responseMessage;

    public HttpClientException(HttpURLConnection connection) {
        this(null, connection);
    }

    public HttpClientException(Throwable cause, HttpURLConnection connection) {
        super(cause);
        this.responseCode = getResponseCode(connection);
        this.responseMessage = getResponseMessage(connection);
    }

    private int getResponseCode(HttpURLConnection connection) {
        if (connection != null) {
            try {
                return connection.getResponseCode();
            } catch (IOException e) {
            }
        }
        return -1;
    }

    private String getResponseMessage(HttpURLConnection connection) {
        if (connection != null) {
            try {
                return connection.getResponseMessage();
            } catch (IOException e) {
            }
        }
        return "";
    }
}
