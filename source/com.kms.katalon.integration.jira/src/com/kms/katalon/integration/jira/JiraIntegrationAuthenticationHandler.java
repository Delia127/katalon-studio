package com.kms.katalon.integration.jira;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;

import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import com.atlassian.jira.rest.client.api.domain.User;
import com.kms.katalon.core.util.JsonUtil;
import com.kms.katalon.integration.jira.constant.JiraIntegrationMessageConstants;
import com.kms.katalon.integration.jira.entity.JiraIssueType;
import com.kms.katalon.integration.jira.entity.JiraProject;
import com.kms.katalon.logging.LogUtil;

public class JiraIntegrationAuthenticationHandler {

    private <T> T getJiraObject(JiraCredential credential, String url, Class<T> clazz)
            throws JiraIntegrationException {
        try (CloseableHttpClient client = HttpClientBuilder.create().setSSLContext(getTrustedSSlContext()).build()) {
            HttpGet request = new HttpGet(url);

            addAuthenticationHeader(credential, request);

            String result = getResultFromRequest(client, request);
            return JsonUtil.fromJson(result, clazz);
        } catch (GeneralSecurityException e) {
            LogUtil.logError(e);
        } catch (JiraIntegrationException e) {
            throw e;
        } catch (IOException e) {
            throw new JiraIntegrationException(e);
        }
        return null;
    }

    private <T> T[] getJiraArrayObjects(JiraCredential credential, String url, Class<T[]> clazz)
            throws JiraIntegrationException {
        T[] result = getJiraObject(credential, url, clazz);
        return result != null ? result : clazz.cast(Array.newInstance(clazz.getComponentType(), 0));
    }

    public User authenticate(JiraCredential credential) throws JiraIntegrationException {
        return getJiraObject(credential, JiraAPIURL.getUserAPIUrl(credential), User.class);
    }

    private void addAuthenticationHeader(JiraCredential credential, HttpGet request) {
        String authEncoded = new Base64()
                .encodeAsString((credential.getUsername() + ":" + credential.getPassword()).getBytes());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + authEncoded);
        request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
    }

    public JiraIssueType[] getJiraIssuesTypes(JiraCredential credential) throws JiraIntegrationException {
        return getJiraArrayObjects(credential, JiraAPIURL.getIssueTypeAPIUrl(credential), JiraIssueType[].class);
    }

    public JiraProject[] getJiraProjects(JiraCredential credential) throws JiraIntegrationException {
        return getJiraArrayObjects(credential, JiraAPIURL.getProjectAPIUrl(credential), JiraProject[].class);
    }

    private String getResultFromRequest(CloseableHttpClient client, HttpRequestBase request)
            throws JiraIntegrationException {
        try (CloseableHttpResponse response = client.execute(request)) {
            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    return getBodyString(response);
                case HttpStatus.SC_UNAUTHORIZED:
                    throw new JiraIntegrationException(JiraIntegrationMessageConstants.MSG_INVALID_ACCOUNT);
                case HttpStatus.SC_FORBIDDEN:
                    throw new JiraIntegrationException(JiraIntegrationMessageConstants.MSG_INVALID_PERMISSION);
                case HttpStatus.SC_NOT_FOUND:
                    throw new JiraIntegrationException(JiraIntegrationMessageConstants.MSG_INVALID_SERVER_URL);
                default:
                    throw new JiraIntegrationException(MessageFormat
                            .format(JiraIntegrationMessageConstants.MSG_INVALID_REQUEST, request.getURI().toString()));
            }
        } catch (UnknownHostException ex) {
            throw new JiraIntegrationException(JiraIntegrationMessageConstants.MSG_INVALID_SERVER_URL);
        } catch (ClientProtocolException ex) {
            throw new JiraIntegrationException(MessageFormat
                    .format(JiraIntegrationMessageConstants.MSG_INVALID_REQUEST, request.getURI().toString()));
        } catch (IOException ex) {
            throw new JiraIntegrationException(ex);
        }
    }

    private String getBodyString(CloseableHttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = StringUtils.EMPTY;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    private SSLContext getTrustedSSlContext() throws GeneralSecurityException {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        return sslContext;
    }
}
