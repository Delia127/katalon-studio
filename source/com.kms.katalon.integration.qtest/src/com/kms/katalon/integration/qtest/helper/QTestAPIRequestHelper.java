package com.kms.katalon.integration.qtest.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.integration.qtest.QTestIntegrationAuthenticationManager;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.credential.IQTestToken;
import com.kms.katalon.integration.qtest.exception.QTestAPIConnectionException;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestIOException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;

public class QTestAPIRequestHelper {

    /**
     * Connects to qTest server via API, sends POST or PUT request and returns the response.
     * 
     * @param url
     * qTest URL as {@link String}
     * @param token
     * QTest token
     * @param body
     * String body of the request
     * @param type
     * <code>PUT<code/> or <code>POST</code>
     * @return response message as {@link String}
     * @throws QTestException
     * if the connection is invalid.
     */
    public static String sendPostOrPutRequestViaAPI(String url, IQTestToken token, String body, String type)
            throws QTestException {
        if (!QTestIntegrationAuthenticationManager.validateToken(token.getAccessTokenHeader())) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        HttpURLConnection con = null;
        BufferedReader reader = null;
        OutputStream os = null;
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection(getProxy());
            con.setRequestMethod(type);
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_USER_AGENT,
                    QTestStringConstants.RQ_DF_VALUE_USER_AGENT);
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_AUTHORIZATION, token.getAccessTokenHeader());
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_CONTENT_TYPE,
                    QTestStringConstants.RQ_DF_VALUE_CONTENT_TYPE);
            con.setDoOutput(true);

            // Send post request
            os = con.getOutputStream();
            os.write(body.getBytes());
            os.flush();

            return getResponse(con.getInputStream());
        } catch (IOException e) {
            if (con != null) {
                try {
                    throw new QTestAPIConnectionException(con.getResponseCode(),
                            e.getMessage() + "\n. Body = [" + body + "]");
                } catch (IOException ex) {
                    throw new QTestAPIConnectionException(ex.getMessage());
                }
            } else {
                throw new QTestAPIConnectionException(e.getMessage());
            }
        } finally {
            if (con != null) {
                con.disconnect();
            }
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(os);
        }
    }

    public static String sendPostRequestViaAPI(String url, IQTestToken token, String body) throws QTestException {
        return sendPostOrPutRequestViaAPI(url, token, body, QTestStringConstants.CON_POST_METHOD);
    }

    /**
     * Connects to qTest server via API, sends GET request and returns the response.
     * 
     * @param url
     * qTest URL as {@link String}
     * @param token
     * QTest token
     * @return response message as {@link String}
     * @throws QTestException
     * if the connection is invalid.
     */
    public static String sendGetRequestViaAPI(String url, IQTestToken token) throws QTestException {
        if (!QTestIntegrationAuthenticationManager.validateToken(token.getAccessTokenHeader())) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        HttpURLConnection con = null;
        BufferedReader in = null;
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection(getProxy());
            con.setRequestMethod(QTestStringConstants.CON_GET_METHOD);
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_USER_AGENT,
                    QTestStringConstants.RQ_DF_VALUE_USER_AGENT);
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_AUTHORIZATION, token.getAccessTokenHeader());

            return getResponse(con.getInputStream());
        } catch (IOException e) {
            if (con != null) {
                try {
                    throw new QTestAPIConnectionException(con.getResponseCode(), e.getMessage());
                } catch (IOException ex) {
                    throw new QTestAPIConnectionException(ex.getMessage());
                }
            } else {
                throw new QTestAPIConnectionException(e.getMessage());
            }
        } finally {
            if (con != null) {
                con.disconnect();
            }

            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Returns content of the given <code>inputStream</code>
     * 
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String getResponse(InputStream inputStream) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, StringConstants.DF_CHARSET));
            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public static Proxy getProxy() throws QTestIOException {
        try {
            return ProxyUtil.getProxy(ProxyPreferences.getProxyInformation());
        } catch (IOException e) {
            throw new QTestIOException(e);
        } catch (URISyntaxException e) {
            throw new QTestIOException(e.getMessage());
        }
    }
}
