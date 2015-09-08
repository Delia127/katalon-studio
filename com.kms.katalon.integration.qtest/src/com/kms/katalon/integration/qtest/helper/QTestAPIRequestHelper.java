package com.kms.katalon.integration.qtest.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.kms.katalon.integration.qtest.QTestIntegrationAuthenticationManager;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.exception.QTestAPIConnectionException;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;

public class QTestAPIRequestHelper {

    /**
     * Connects to qTest server via API, sends POST or PUT request and returns the response. 
     * 
     * @param url qTest URL as {@link String}
     * @param token QTest token
     * @param body String body of the request
     * @param type <code>PUT<code/> or <code>POST</code>
     * @return response message as {@link String}
     * @throws QTestException if the connection is invalid.
     */
    public static String sendPostOrPutRequestViaAPI(String url, String token, String body, String type)
            throws QTestException {
        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        HttpURLConnection con = null;
        BufferedReader reader = null;
        OutputStream os = null;
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod(type);
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_USER_AGENT, QTestStringConstants.RQ_DF_VALUE_USER_AGENT);
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_AUTHORIZATION, token);
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_USER_AGENT, QTestStringConstants.RQ_DF_VALUE_CONTENT_TYPE);
            con.setDoOutput(true);

            // Send post request
            os = con.getOutputStream();
            os.write(body.getBytes());
            os.flush();

            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            return response.toString();
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

            if (reader != null) {
                closeQuietly(reader);
            }

            if (os != null) {
                closeQuietly(os);
            }
        }
    }

    public static String sendPostRequestViaAPI(String url, String token, String body) throws QTestException {
        return sendPostOrPutRequestViaAPI(url, token, body, QTestStringConstants.CON_POST_METHOD);
    }

    /**
     * Connects to qTest server via API, sends GET request and returns the response. 
     * 
     * @param url qTest URL as {@link String}
     * @param token QTest token
     * @return response message as {@link String}
     * @throws QTestException if the connection is invalid.
     */
    public static String sendGetRequestViaAPI(String url, String token) throws QTestException {
        if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(QTestMessageConstants.QTEST_EXC_INVALID_TOKEN);
        }

        HttpURLConnection con = null;
        BufferedReader in = null;
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod(QTestStringConstants.CON_GET_METHOD);
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_USER_AGENT, QTestStringConstants.RQ_DF_VALUE_USER_AGENT);
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_AUTHORIZATION, token);

            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer sb = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();
            return sb.toString();
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

            if (in != null) {
                closeQuietly(in);
            }
        }
    }

    /**
     * Close the given {@link BufferedReader} without throwing Exception
     * <p>
     * Used in <code>finally</code> block only.
     * 
     * @param reader
     * @see #sendPostOrPutRequestViaAPI(String, String, String, String)
     * @see #sendGetRequestViaAPI(String, String)
     */
    private static void closeQuietly(BufferedReader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            // Ignore it
        }
    }

    /**
     * Close the given {@link OutputStream} without throwing any exception.
     * <p>
     * Used in <code>finally</code> block only.
     * 
     * @param os
     * @see #sendPostOrPutRequestViaAPI(String, String, String, String)
     * @see #sendGetRequestViaAPI(String, String)
     */
    public static void closeQuietly(OutputStream os) {
        try {
            os.close();
        } catch (IOException e) {
            // Ignore it
        }
    }
}
