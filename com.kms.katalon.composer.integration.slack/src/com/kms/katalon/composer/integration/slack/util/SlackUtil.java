package com.kms.katalon.composer.integration.slack.util;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.google.gson.stream.JsonReader;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.slack.constants.StringConstants;
import com.kms.katalon.constants.PreferenceConstants;

/**
 * Slack Utility for Team Collaboration
 * 
 * @see <a href="https://api.slack.com/methods/chat.postMessage">Slack Post Message API</a>
 *
 */
@SuppressWarnings("restriction")
public class SlackUtil {
	private IPreferenceStore PREFERENCE;
	private boolean ENABLED;
	private String TOKEN;
	private String CHANNEL;
	private String USERNAME;
	private boolean AS_USER;

	public SlackUtil() {
		PREFERENCE = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.IntegrationSlackPreferenceConstants.QUALIFIER);
		ENABLED = PREFERENCE.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_ENABLED);
		TOKEN = PREFERENCE.getString(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_AUTH_TOKEN);
		CHANNEL = PREFERENCE.getString(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_CHANNEL_GROUP);
		USERNAME = PREFERENCE.getString(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_USERNAME);
		AS_USER = PREFERENCE.getBoolean(PreferenceConstants.IntegrationSlackPreferenceConstants.SLACK_AS_USER);
	}

	/**
	 * Slack URL builder which will encode user info and message into URL
	 * 
	 * @param msg String message to send
	 * @return String URL
	 * @throws Exception UnsupportedEncodingException
	 * @see UnsupportedEncodingException
	 */
	private String getSlackApiUrl(String msg) throws Exception {
		String charset = "UTF-8";
		String url = "https://slack.com/api/chat.postMessage?";
		url += "token=" + URLEncoder.encode(TOKEN.trim(), charset);
		url += "&channel=" + URLEncoder.encode(CHANNEL.trim(), charset);
		if (!USERNAME.trim().isEmpty()) {
			url += "&username=" + URLEncoder.encode(USERNAME.trim(), charset);
		}
		if (AS_USER) {
			url += "&as_user=true";
		}
		url += "&text=" + URLEncoder.encode(msg, charset);
		return url;
	}

	/**
	 * Send message to Slack for Team Collaboration
	 * 
	 * @param msg String message to send
	 */
	public void sendMessage(String msg) {
		if (ENABLED) {
			try {
				boolean connectSuccessfully = false;
				String errorMsg = null;

				URL api = new URL(getSlackApiUrl(msg));
				HttpURLConnection con = (HttpURLConnection) api.openConnection();
				con.setRequestMethod("GET");
				// con.setRequestProperty("User-Agent", "Katalon Studio");
				InputStreamReader in = new InputStreamReader(con.getInputStream());

				JsonReader reader = new JsonReader(in);
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					if (StringUtils.equals(name, "ok")) {
						connectSuccessfully = reader.nextBoolean();
					} else if (StringUtils.equals(name, "error")) {
						errorMsg = reader.nextString();
					} else {
						reader.skipValue(); // avoid some unhandled events
					}
				}
				reader.endObject();
				reader.close();

				System.out.println(StringConstants.UTIL_SENDING_MSG_PREFIX + msg);
				if (connectSuccessfully && errorMsg == null) {
					System.out.println(StringConstants.UTIL_SUCCESS_MSG_PREFIX + msg);
				} else if (!connectSuccessfully && errorMsg != null) {
					System.out.println(StringConstants.UTIL_ERROR_MSG_PREFIX
							+ SlackMsgStatus.getMsgDescription(errorMsg));
					LoggerSingleton.getInstance().getLogger()
							.warn(StringConstants.UTIL_ERROR_MSG_PREFIX + SlackMsgStatus.getMsgDescription(errorMsg));
				} else {
					System.out.println(StringConstants.UTIL_ERROR_MSG_PLS_CHK_INTERNET_CONNECTION);
				}
			} catch (Exception e) {
				LoggerSingleton.getInstance().getLogger().error(e);
			}
		}
	}

	/**
	 * Slack Bold format text message
	 * 
	 * @param msg String text message
	 * @return String Bold format text message
	 */
	public String fmtBold(String msg) {
		return "*" + msg + "*";
	}

	/**
	 * Slack Italic format text message
	 * 
	 * @param msg String text message
	 * @return String Italic format text message
	 */
	public String fmtItalic(String msg) {
		return "_" + msg + "_";
	}

	/**
	 * Slack responded message status
	 */
	public static class SlackMsgStatus {
		private static Map<String, String> msgMap;

		public SlackMsgStatus() {
			msgMap = new HashMap<String, String>();
			msgMap.put("channel_not_found", StringConstants.SLACK_ERROR_MSG_CHANNEL_NOT_FOUND);
			msgMap.put("not_in_channel", StringConstants.SLACK_ERROR_MSG_NOT_IN_CHANNEL);
			msgMap.put("is_archived", StringConstants.SLACK_ERROR_MSG_IS_ARCHIVED);
			msgMap.put("msg_too_long", StringConstants.SLACK_ERROR_MSG_MSG_TOO_LONG);
			msgMap.put("no_text", StringConstants.SLACK_ERROR_MSG_NO_TEXT);
			msgMap.put("rate_limited", StringConstants.SLACK_ERROR_MSG_RATE_LIMITED);
			msgMap.put("not_authed", StringConstants.SLACK_ERROR_MSG_NOT_AUTHED);
			msgMap.put("invalid_auth", StringConstants.SLACK_ERROR_MSG_INVALID_AUTH);
			msgMap.put("account_inactive", StringConstants.SLACK_ERROR_MSG_ACCOUNT_INACTIVE);
		}

		public static Map<String, String> getMsgMap() {
			return msgMap;
		}

		public static String getMsgDescription(String msgCode) {
			if (getMsgMap().get(msgCode) == null) {
				// No message description found
				return msgCode;
			}
			return getMsgMap().get(msgCode);
		}
	}
}
