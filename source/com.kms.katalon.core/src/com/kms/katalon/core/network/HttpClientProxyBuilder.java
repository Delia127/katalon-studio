package com.kms.katalon.core.network;

import java.io.IOException;
import java.net.Proxy;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import com.kms.katalon.core.util.internal.ProxyUtil;

public class HttpClientProxyBuilder {

    private static PoolingHttpClientConnectionManager connectionManager;
    
    static {
        try {
            initializeConnectionManager();
        } catch(Exception e) {
            connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setMaxTotal(2000);
            connectionManager.setDefaultMaxPerRoute(500);
        }
    }
    
    /**
     * This method initializes a {@link PoolingHttpClientConnectionManager} which registers to HTTPS
     * a SSL socket factory that trusts all certificates (including self-signed). We set the trust strategy
     * and host verifder in this stage because a {@link HttpClientBuilder} when being set a
     * {@link PoolingHttpClientConnectionManager}
     * will ignore all custom SSLContext
     * 
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws KeyManagementException
     */
    private static void initializeConnectionManager()
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true)
                .build();
        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext,
                new NoopHostnameVerifier());
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("https", sslConnectionFactory)
                .build();
        connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(2000);
        connectionManager.setDefaultMaxPerRoute(500);
    }
    
    private final HttpClientBuilder clientBuilder;

    private final HttpClientContext clientContext;

    public HttpClientProxyBuilder(HttpClientBuilder clientBuilder, HttpClientContext clientContext) {
        this.clientBuilder = clientBuilder;
        this.clientContext = clientContext;
    }

    public HttpClientBuilder getClientBuilder() {
        return clientBuilder;
    }

    public HttpClientContext getClientContext() {
        return clientContext;
    }

    public static HttpClientProxyBuilder create(ProxyInformation proxyInfo)
            throws URISyntaxException, IOException, GeneralSecurityException {
        Proxy proxy = ProxyUtil.getProxy(proxyInfo);
        HttpClientBuilder clientBuilder = HttpClients.custom();
        // Don't bother setting a SSLContext here because when we set connectionManager it will be ignored anyway
        HttpClientContext context = HttpClientContext.create();
        if (!Proxy.NO_PROXY.equals(proxy) || proxy.type() != Proxy.Type.DIRECT) {
            HttpHost httpHost = new HttpHost(proxyInfo.getProxyServerAddress(), proxyInfo.getProxyServerPort());

            // Client credentials
            CredentialsProvider credentialsProvider = createCredentialsProvider(httpHost, proxyInfo);

            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            authCache.put(httpHost, new BasicScheme());
            context.setCredentialsProvider(credentialsProvider);
            context.setAuthCache(authCache);

            clientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(httpHost))
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setSSLHostnameVerifier(new NoopHostnameVerifier());
        }

        clientBuilder.setConnectionManager(connectionManager).setConnectionManagerShared(true);

        return new HttpClientProxyBuilder(clientBuilder, context);
    }

    private static CredentialsProvider createCredentialsProvider(HttpHost httpProxy, ProxyInformation proxyInfo) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        String username = proxyInfo.getUsername();
        String password = proxyInfo.getPassword();
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            credentialsProvider.setCredentials(new AuthScope(httpProxy),
                    new UsernamePasswordCredentials(username, password));
        }
        return credentialsProvider;
    }

    private static HttpClientConnectionManager getSystemConnectionManager(Proxy proxy) {
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(SSLContexts.createSystemDefault()) {
                    @Override
                    public Socket createSocket(final HttpContext context) {
                        return new Socket(proxy);
                    }
                })
                .build();
        return new PoolingHttpClientConnectionManager(reg);
    }
}
