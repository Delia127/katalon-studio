package com.kms.katalon.integration.qtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.qas.api.internal.util.json.JsonObject;
import org.qas.api.net.UrlEncoder;

import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;

public class QTestIntegrationAuthenticationManager {

	private static final String USERNAME_PARAM = "j_username=";
	private static final String PASSWORD_PARAM = "j_password=";
	private static final String LOGIN_URL = "/api/login?";

	public static String getToken(String serverURL, String username, String password) throws Exception {
		String url = serverURL + LOGIN_URL + USERNAME_PARAM + UrlEncoder.encode(username) + "&" + PASSWORD_PARAM
				+ UrlEncoder.encode(password);

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setDoOutput(true);

		// Send post request
		OutputStream os = con.getOutputStream();
		os.write("".getBytes());
		os.flush();
		os.close();

		int status = 0;
		try {
			status = con.getResponseCode();
		} catch (IOException e) {
			status = con.getResponseCode();
		}
		if (status == HttpURLConnection.HTTP_OK) {
			return getReponse(con.getInputStream());
		} else {
			String response = getReponse(con.getErrorStream());

			JsonObject jo = new JsonObject(response.toString());
			throw new IOException(jo.getString("message"));
		}
	}

	private static String getReponse(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		StringBuffer response = new StringBuffer();
		String inputLine;
		while ((inputLine = reader.readLine()) != null) {
			response.append(inputLine);
		}
		reader.close();
		return response.toString();
	}

	public static boolean validateToken(String token) {
		if (token == null || token.isEmpty()) {
			return false;
		}
		// TODO wait for qTest API
		return true;
	}

	/**
	 * If username or password is null or empty, throw a QTestUnauthorizedException
	 * @param username
	 * @param password
	 */
	public static void authenticate(String username, String password) {
		if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
			throw new QTestUnauthorizedException(
					"Your qTest username or password is not valid.\n"
							+ "Please enter valid username and password in qTest setting page \n"
							+ "by choosing Project->Settings->qTest and click on Generate button.");
		}
	}
}
