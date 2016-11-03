package com.kms.katalon.integration.jira.request;

import java.io.BufferedReader;
import java.io.File;
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
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;

import com.kms.katalon.core.util.JsonUtil;
import com.kms.katalon.integration.jira.JiraCredential;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.JiraInvalidURLException;
import com.kms.katalon.integration.jira.constant.JiraIntegrationMessageConstants;
import com.kms.katalon.logging.LogUtil;

public class JiraIntegrationRequest {

    protected <T> T getJiraObject(JiraCredential credential, String url, Class<T> clazz)
            throws JiraIntegrationException {
        try (CloseableHttpClient client = HttpClientBuilder.create().setSSLContext(getTrustedSSLContext()).build()) {
            HttpGet request = new HttpGet(url);

            addAuthenticationHeader(credential, request);
            request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

            String result = getResultFromRequest(client, request);
            return JsonUtil.fromJson(result, clazz);
        } catch (IllegalArgumentException e) {
            throw new JiraInvalidURLException(JiraIntegrationMessageConstants.MSG_INVALID_SERVER_URL);
        } catch (GeneralSecurityException e) {
            LogUtil.logError(e);
        } catch (JiraIntegrationException e) {
            throw e;
        } catch (IOException e) {
            throw new JiraIntegrationException(e);
        }
        return null;
    }

    public void sendPutRequest(JiraCredential credential, String url, String content) throws JiraIntegrationException {
        try (CloseableHttpClient httpClient = getClientBuilder()) {
            HttpPut post = new HttpPut(url);
            addAuthenticationHeader(credential, post);
            post.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            post.setEntity(new StringEntity(content));
            getResultFromRequest(httpClient, post);
        } catch (IllegalArgumentException e) {
            throw new JiraInvalidURLException(JiraIntegrationMessageConstants.MSG_INVALID_SERVER_URL);
        } catch (GeneralSecurityException e) {
            LogUtil.logError(e);
        } catch (JiraIntegrationException e) {
            throw e;
        } catch (IOException e) {
            throw new JiraIntegrationException(e);
        }
    }

    public void sendUploadRequest(JiraCredential credential, String url, String filePath)
            throws JiraIntegrationException {
        try (CloseableHttpClient httpClient = getClientBuilder()) {
            HttpPost post = new HttpPost(url);
            addAuthenticationHeader(credential, post);
            HttpEntity fileEntity = MultipartEntityBuilder.create()
                    .addPart("file", new FileBody(new File(filePath)))
                    .build();
            post.setEntity(fileEntity);
            getResultFromRequest(httpClient, post);
        } catch (IllegalArgumentException e) {
            throw new JiraInvalidURLException(JiraIntegrationMessageConstants.MSG_INVALID_SERVER_URL);
        } catch (GeneralSecurityException e) {
            LogUtil.logError(e);
        } catch (JiraIntegrationException e) {
            throw e;
        } catch (IOException e) {
            throw new JiraIntegrationException(e);
        }
    }

    protected <T> T[] getJiraArrayObjects(JiraCredential credential, String url, Class<T[]> clazz)
            throws JiraIntegrationException {
        T[] result = getJiraObject(credential, url, clazz);
        return result != null ? result : clazz.cast(Array.newInstance(clazz.getComponentType(), 0));
    }

    protected CloseableHttpClient getClientBuilder() throws GeneralSecurityException {
        return HttpClientBuilder.create().setSSLContext(getTrustedSSLContext()).build();
    }

    protected void addAuthenticationHeader(JiraCredential credential, HttpRequestBase request) {
        String authEncoded = new Base64()
                .encodeAsString((credential.getUsername() + ":" + credential.getPassword()).getBytes());
        request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + authEncoded);
        request.setHeader("X-Atlassian-Token", "no-check");
    }

    protected String getBodyString(CloseableHttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuilder result = new StringBuilder();
        String line = StringUtils.EMPTY;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    protected SSLContext getTrustedSSLContext() throws GeneralSecurityException {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).build();
        return sslContext;
    }

    protected String getResultFromRequest(CloseableHttpClient client, HttpRequestBase request)
            throws JiraIntegrationException {
        try (CloseableHttpResponse response = client.execute(request)) {
            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_NO_CONTENT:
                    return StringUtils.EMPTY;
                case HttpStatus.SC_OK:
                    return getBodyString(response);
                case HttpStatus.SC_UNAUTHORIZED:
                    throw new JiraIntegrationException(JiraIntegrationMessageConstants.MSG_INVALID_ACCOUNT);
                case HttpStatus.SC_FORBIDDEN:
                    throw new JiraIntegrationException(MessageFormat
                            .format(JiraIntegrationMessageConstants.MSG_INVALID_PERMISSION, getBodyString(response)));
                case HttpStatus.SC_NOT_FOUND:
                    throw new JiraInvalidURLException(JiraIntegrationMessageConstants.MSG_INVALID_SERVER_URL);
                default:
                    throw new JiraIntegrationException(MessageFormat
                            .format(JiraIntegrationMessageConstants.MSG_INVALID_REQUEST, request.getURI().toString()));
            }
        } catch (UnknownHostException ex) {
            throw new JiraIntegrationException(JiraIntegrationMessageConstants.MSG_INVALID_SERVER_URL);
        } catch (ClientProtocolException ex) {
            throw new JiraIntegrationException(MessageFormat.format(JiraIntegrationMessageConstants.MSG_INVALID_REQUEST,
                    request.getURI().toString()));
        } catch (IOException ex) {
            throw new JiraIntegrationException(ex);
        }
    }
}
