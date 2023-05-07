package org.lodder.subtools.sublibrary.util.http;

import java.io.IOException;
import java.io.Serial;
import java.net.HttpURLConnection;

public class HttpClientException extends Exception {

    @Serial
    private static final long serialVersionUID = 5583416046207372599L;
    private int responseCode = -1;
    private String responseMessage = "";

    public HttpClientException(HttpURLConnection connection) {
        storeConnectionResponseInfo(connection);
    }

    public HttpClientException() {
        super();
    }

    public HttpClientException(String message, HttpURLConnection connection) {
        super(message);
        storeConnectionResponseInfo(connection);
    }

    public HttpClientException(String message, Throwable cause, HttpURLConnection connection) {
        super(message, cause);
        storeConnectionResponseInfo(connection);
    }

    public HttpClientException(Throwable cause, HttpURLConnection connection) {
        super(cause);
        storeConnectionResponseInfo(connection);
    }

    protected HttpClientException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace, HttpURLConnection connection) {
        super(message, cause, enableSuppression, writableStackTrace);
        storeConnectionResponseInfo(connection);
    }

    private void storeConnectionResponseInfo(HttpURLConnection connection) {
        if (connection != null) {
            try {
                this.responseCode = connection.getResponseCode();
                this.responseMessage = connection.getResponseMessage();
            } catch (IOException e) {
                // let's keep this quiet!
            }
        }
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public String getResponseMessage() {
        return this.responseMessage;
    }

}
