package com.kms.katalon.integration.qtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.qas.api.internal.util.json.JsonException;
import org.qas.api.internal.util.json.JsonObject;
import org.qas.api.net.UrlEncoder;

import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.integration.qtest.exception.QTestAPIConnectionException;
import com.kms.katalon.integration.qtest.exception.QTestException;
import com.kms.katalon.integration.qtest.exception.QTestIOException;
import com.kms.katalon.integration.qtest.exception.QTestInvalidFormatException;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;
import com.kms.katalon.integration.qtest.helper.QTestAPIRequestHelper;

/**
 * Provides a set of utility methods for qTest authentication
 */
public class QTestIntegrationAuthenticationManager {

    private QTestIntegrationAuthenticationManager() {
        // Disable default constructor
    }

    private static final String USERNAME_PARAM = "j_username=";
    private static final String PASSWORD_PARAM = "j_password=";
    private static final String LOGIN_URL = "/api/login?";

    /**
     * Returns qTest token as String after sending an authentication request.
     * 
     * @param serverURL
     * @param username
     * @param password
     * @return qTest token
     * @throws QTestException
     *             thrown if system cannot send request or the params are
     *             unauthorized.
     */
    public static String getToken(String serverURL, String username, String password) throws QTestException {
        String url = serverURL + LOGIN_URL + USERNAME_PARAM + UrlEncoder.encode(username) + "&" + PASSWORD_PARAM
                + UrlEncoder.encode(password);
        HttpURLConnection con = null;
        OutputStream os = null;
        String response = "";
        try {
            URL obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod(QTestStringConstants.CON_POST_METHOD);
            con.setRequestProperty(QTestStringConstants.RQ_PROPERTY_CONTENT_TYPE, "application/x-www-form-urlencoded");
            con.setDoOutput(true);

            // Send post request
            os = con.getOutputStream();
            os.write("".getBytes());
            os.flush();

            int status = 0;
            try {
                status = con.getResponseCode();
            } catch (IOException e) {
                status = con.getResponseCode();
            }
            if (status == HttpURLConnection.HTTP_OK) {
                return getResponse(con.getInputStream());
            } else {
                response = getResponse(con.getErrorStream());

                JsonObject jo = new JsonObject(response.toString());
                throw new QTestIOException(jo.getString("message"));
            }
        } catch (IllegalArgumentException ex) {
            throw new QTestAPIConnectionException(ex.getMessage());
        } catch (IOException ex) {
            throw new QTestIOException(ex);
        } catch (JsonException ex) {
            throw QTestInvalidFormatException.createInvalidJsonFormatException(response);
        } finally {
            if (os != null) {
                QTestAPIRequestHelper.closeQuietly(os);
            }
        }
    }

    /**
     * Returns content of the given <code>inputStream</code>
     * 
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static String getResponse(InputStream inputStream) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static boolean validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        // TODO wait for qTest API
        return true;
    }

    /**
     * If username or password is null or empty, throw a
     * QTestUnauthorizedException
     * 
     * @param username
     * @param password
     * @throws QTestUnauthorizedException
     */
    public static void authenticate(String username, String password) throws QTestUnauthorizedException {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new QTestUnauthorizedException("Your qTest username or password is not valid.\n"
                    + "Please enter valid username and password in qTest setting page \n"
                    + "by choosing Project->Settings->qTest and click on Generate button.");
        }
    }
}
