package com.kms.katalon.integration.qtest.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.kms.katalon.integration.qtest.QTestIntegrationAuthenticationManager;
import com.kms.katalon.integration.qtest.constants.StringConstants;
import com.kms.katalon.integration.qtest.exception.QTestUnauthorizedException;

public class QTestAPIRequestHelper {
	
	public static String sendPostOrPutRequestViaAPI(String url, String token, String body, String type) throws Exception{
		 if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
	            throw new QTestUnauthorizedException(StringConstants.QTEST_EXC_INVALID_TOKEN);
	        }
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(type);
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Authorization", token);
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			
			// Send post request
			OutputStream os = con.getOutputStream();
			os.write(body.getBytes());
			os.flush();
			os.close();
	 
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			StringBuffer response = new StringBuffer();
			String inputLine;
			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
			}
			reader.close();		
			return response.toString();
	}
	
	public static String sendPostRequestViaAPI(String url, String token, String body) throws Exception{
	   return sendPostOrPutRequestViaAPI(url, token, body, "POST");
	}
	
	public static String sendGetRequestViaAPI(String url, String token) throws Exception {
	    if (!QTestIntegrationAuthenticationManager.validateToken(token)) {
            throw new QTestUnauthorizedException(StringConstants.QTEST_EXC_INVALID_TOKEN);
        }
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty ("Authorization", token);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer sb = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		in.close();
		return sb.toString();
	}
}
