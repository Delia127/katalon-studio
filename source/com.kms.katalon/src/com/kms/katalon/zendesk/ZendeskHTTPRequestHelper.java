package com.kms.katalon.zendesk;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.runtime.Platform;

import com.google.gson.Gson;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.ApplicationProxyUtil;
import com.kms.katalon.console.constants.ConsoleStringConstants;
import com.kms.katalon.core.webservice.support.UrlEncoder;
import com.kms.katalon.zendesk.ZendeskTicket.ZendeskCollaborator;
import com.kms.katalon.zendesk.ZendeskTicket.ZendeskTicketComment;

public class ZendeskHTTPRequestHelper {
    private static final String PROTOCOL_TLS = "TLS";

    private static final String ZENDESK_API_ENDPOING = "https://katalonhelp.zendesk.com/api/v2/";

    private static final String AUTHORIZATION_STRING = "Basic b2xpdmVyaG93YXJkQGthdGFsb24uY29tL3Rva2VuOnFsRU5yYXFsVU1XYU01WGlRVU9YamF5Y3ZHeW43d1I4aVBpbEVnS1Y=";

    private static final String BINARY_BODY_NAME = "file";

    private static final String CONTENT_TYPE_IMAGE_JPEG = "image/jpeg";

    private static final String TOKEN_PARAM = "token";

    private static final String AND_PARAM = "&";

    private static final String WITH_PARAM_VALUE = "=";

    private static final String FILENAME_PARAM = "filename";

    private static final String WITH_PARAMS = "?";

    private static final String API_UPLOADS = ZENDESK_API_ENDPOING + "uploads.json";

    private static final String API_CREATE_TICKET = ZENDESK_API_ENDPOING + "tickets.json";

    private static final int STATUS_CODE_CREATED = 201;

    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    private static final String HTTP_HEADER_AUTHORIZATION = "Authorization";

    public static ZendeskTicket createTicket(String summary, String description, List<File> attachments)
            throws ParseException, IOException, ZendeskRequestException, GeneralSecurityException {
        ZendeskCreateTicketRequest request = createZendeskCreateTicketRequest(summary, description, attachments);
        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put(HTTP_HEADER_CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        HttpResponse response = doPost(API_CREATE_TICKET, new StringEntity(new Gson().toJson(request)),
                additionalHeaders);
        HttpEntity resEntity = response.getEntity();
        String responseContent = (resEntity != null ? EntityUtils.toString(resEntity, StandardCharsets.UTF_8) : "");
        if (response.getStatusLine().getStatusCode() != STATUS_CODE_CREATED) {
            throw new ZendeskRequestException(responseContent);
        }
        if (resEntity != null) {
            EntityUtils.consume(resEntity);
        }
        return new Gson().fromJson(responseContent, ZendeskTicket.class);
    }

    private static ZendeskCreateTicketRequest createZendeskCreateTicketRequest(String summary, String description,
            List<File> attachments)
            throws IOException, ZendeskRequestException, ParseException, GeneralSecurityException {
        List<File> clonedAttachments = new ArrayList<>(attachments);
        clonedAttachments.add(new File(Platform.getLogFileLocation().toOSString()));
        ZendeskTicket ticket = createZendeskTicket(summary, description, clonedAttachments);
        return new ZendeskCreateTicketRequest(ticket);
    }

    private static ZendeskTicket createZendeskTicket(String summary, String description, List<File> attachments)
            throws IOException, ZendeskRequestException, ParseException, GeneralSecurityException {
        String email = ApplicationInfo.getAppProperty(ConsoleStringConstants.ARG_EMAIL);
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new EmailNotValidException(email);
        }
        ZendeskTicket ticket = new ZendeskTicket(summary);
        ZendeskTicketComment comment = createZendeskTicketComment(description, attachments);
        ticket.setComment(comment);
        ticket.setRequester(new ZendeskCollaborator(email, email));
        return ticket;
    }

    private static ZendeskTicketComment createZendeskTicketComment(String description, List<File> attachments)
            throws IOException, ZendeskRequestException, ParseException, GeneralSecurityException {
        ZendeskTicketComment comment = new ZendeskTicketComment(description);
        if (attachments != null && !attachments.isEmpty()) {
            ZendeskUploadAttachmentResponse uploadAttachmentResponse = uploadFiles(attachments);
            comment.setUploads(new String[] { uploadAttachmentResponse.getUpload().getToken() });
        }
        return comment;
    }

    private static ZendeskUploadAttachmentResponse uploadFiles(List<File> attachments)
            throws ParseException, IOException, ZendeskRequestException, GeneralSecurityException {
        ZendeskUploadAttachmentResponse response = null;
        for (File file : attachments) {
            if (!file.exists()) {
                continue;
            }
            response = uploadFile(file, response != null ? response.getUpload().getToken() : null);
        }
        return response;
    }

    private static ZendeskUploadAttachmentResponse uploadFile(File attachment, String previouseToken)
            throws ParseException, IOException, ZendeskRequestException, GeneralSecurityException {
        String fileName = attachment.getName();
        HttpResponse response = doPost(buildUploadAttachmentUrl(fileName, previouseToken), MultipartEntityBuilder
                .create()
                .addBinaryBody(BINARY_BODY_NAME, attachment, ContentType.create(CONTENT_TYPE_IMAGE_JPEG), fileName)
                .build(), null);
        HttpEntity resEntity = response.getEntity();
        String responseContent = (resEntity != null ? EntityUtils.toString(resEntity, StandardCharsets.UTF_8) : "");
        if (response.getStatusLine().getStatusCode() != STATUS_CODE_CREATED) {
            throw new ZendeskRequestException(responseContent);
        }
        if (resEntity != null) {
            EntityUtils.consume(resEntity);
        }
        return new Gson().fromJson(responseContent, ZendeskUploadAttachmentResponse.class);
    }

    private static HttpResponse doPost(String url, HttpEntity httpEntity, Map<String, String> additionalHeaders)
            throws IOException, GeneralSecurityException {
        HttpClientBuilder builder = HttpClientBuilder.create();
        addProxy(builder);
        builder.setSSLSocketFactory(createSslConnectionSocketFactory());
        HttpClient httpClient = builder.build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(httpEntity);
        httpPost.addHeader(HTTP_HEADER_AUTHORIZATION, AUTHORIZATION_STRING);
        if (additionalHeaders != null && !additionalHeaders.isEmpty()) {
            additionalHeaders.entrySet()
                    .stream()
                    .forEach(headerEntry -> httpPost.addHeader(headerEntry.getKey(), headerEntry.getValue()));
        }

        HttpResponse response = httpClient.execute(httpPost);
        return response;
    }

    private static SSLConnectionSocketFactory createSslConnectionSocketFactory()
            throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                return true;
            }
        }).useProtocol(PROTOCOL_TLS).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
        return sslsf;
    }

    private static void addProxy(HttpClientBuilder builder) throws IOException {
        Proxy proxy = ApplicationProxyUtil.getProxy();
        if (proxy != Proxy.NO_PROXY && proxy.address() instanceof InetSocketAddress) {
            InetSocketAddress socketAddress = (InetSocketAddress) proxy.address();
            builder.setProxy(
                    new HttpHost(socketAddress.getHostName(), socketAddress.getPort(), proxy.type().toString()));
        }
    }

    private static String buildUploadAttachmentUrl(String fileName, String previouseToken) {
        return API_UPLOADS + WITH_PARAMS + FILENAME_PARAM + WITH_PARAM_VALUE + UrlEncoder.encode(fileName)
                + (previouseToken != null ? AND_PARAM + TOKEN_PARAM + WITH_PARAM_VALUE + previouseToken
                        : StringUtils.EMPTY);
    }
}
