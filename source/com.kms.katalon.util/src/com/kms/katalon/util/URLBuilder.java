package com.kms.katalon.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.ParserCursor;
import org.apache.http.message.TokenParser;
import org.apache.http.util.CharArrayBuffer;

import com.kms.katalon.util.collections.NameValuePair;

public class URLBuilder {

    private static final char QP_SEP_A = '&';
    private static final char QP_SEP_S = ';';
    private static final char NAME_VALUE_SEPARATOR = '=';
    
    private String protocol;
    private String authority;
    private String userInfo;
    private String host;
    private int port;
    private String path;
    private List<NameValuePair> queryParams;
    private String query;
    private String ref;
    
    public URLBuilder() {
        this.port = -1;
    }
    
    public URLBuilder(final String string) throws MalformedURLException {
        digestURL(new URL(string));
    }
    
    private void digestURL(final URL url) {
        this.protocol = url.getProtocol();
        this.authority = url.getAuthority();
        this.host = url.getHost();
        this.port = url.getPort();
        this.userInfo = url.getUserInfo();
        this.path = url.getPath();
        this.query = url.getQuery();
        this.queryParams = parseQuery(url.getQuery());
        this.ref = url.getRef();
    }
    
    private List<NameValuePair> parseQuery(final String query) {
        if (!StringUtils.isBlank(query)) {
            final CharArrayBuffer buffer = new CharArrayBuffer(query.length());
            buffer.append(query);
            return parse(buffer, QP_SEP_A, QP_SEP_S);
        }
        return queryParams;
    }
    
    private List<NameValuePair> parse(final CharArrayBuffer buf, final char... separators) {
        
        final TokenParser tokenParser = TokenParser.INSTANCE;
        final BitSet delimSet = new BitSet();
        for (char separator: separators) {
            delimSet.set(separator);
        }
        final ParserCursor cursor = new ParserCursor(0, buf.length());
        final List<NameValuePair> list = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            delimSet.set('=');
            final String name = tokenParser.parseToken(buf, cursor, delimSet);
            String value = null;
            if (!cursor.atEnd()) {
                final int delim = buf.charAt(cursor.getPos());
                cursor.updatePos(cursor.getPos() + 1);
                if (delim == '=') {
                    delimSet.clear('=');
                    value = tokenParser.parseValue(buf, cursor, delimSet);
                    if (!cursor.atEnd()) {
                        cursor.updatePos(cursor.getPos() + 1);
                    }
                }
            }
//            if (!name.isEmpty()) {
//                list.add(new NameValuePair(name, value));
//            }
            list.add(new NameValuePair(name, value));
        }
        return list;
    }
    
    public URLBuilder setParameters(final List<NameValuePair> nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        } else {
            this.queryParams.clear();
        }
        this.queryParams.addAll(nvps);
        this.query = null;
        return this;
    }
    
    public URLBuilder addParameters(final List <NameValuePair> nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        this.queryParams.addAll(nvps);
        this.query = null;
        return this;
    }
    
    public List<NameValuePair> getQueryParams() {
        if (this.queryParams != null) {
            return new ArrayList<NameValuePair>(this.queryParams);
        } else {
            return new ArrayList<NameValuePair>();
        }
    }
    
    public URL build() throws MalformedURLException {
       return new URL(buildString());
    }
    
    private String buildString() {
        final StringBuilder sb = new StringBuilder();
        if (this.protocol != null) {
            sb.append(this.protocol).append(':');
        }
       
        if (this.authority != null) {
            sb.append("//").append(this.authority);
        } else if (this.host != null) {
            sb.append("//");
            if (this.userInfo != null) {
                sb.append(this.userInfo).append("@");
            } 
           
            sb.append(this.host);
            
            if (this.port >= 0) {
                sb.append(":").append(this.port);
            }
        }
        
        sb.append(normalizePath(this.path));
        
        if (this.query != null) {
            sb.append("?").append(this.query);
        } else if (this.queryParams != null) {
            sb.append("?").append(toQueryString(this.queryParams));
        }
       
        if (this.ref != null) {
            sb.append("#").append(this.ref);
        }
        return sb.toString();
    }
    
    private String toQueryString(List<NameValuePair> queryParams) {
        final StringBuilder result = new StringBuilder();
        for (final NameValuePair parameter : queryParams) {
            final String name = parameter.getName();
            final String value = parameter.getValue();
            if (result.length() > 0) {
                result.append(QP_SEP_A);
            }
            result.append(name);
            if (value != null) {
                result.append(NAME_VALUE_SEPARATOR);
                result.append(value);
            }
        }
        return result.toString();
    }

    private static String normalizePath(final String path) {
        String s = path;
        if (s == null) {
            return null;
        }
        int n = 0;
        for (; n < s.length(); n++) {
            if (s.charAt(n) != '/') {
                break;
            }
        }
        if (n > 1) {
            s = s.substring(n - 1);
        }
        return s;
    }
}
