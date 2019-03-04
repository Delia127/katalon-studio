package com.kms.katalon.util;

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

    private String path;
    private List<NameValuePair> queryParams;
    private String query;
    private String ref;
    
    public URLBuilder() {
    }
    
    public URLBuilder(final String string) {
        digestURL(string);
    }
    
    private void digestURL(final String string) {
        String url = string;
        int ind = url.indexOf('#');
        ref = ind < 0 ? null: url.substring(ind + 1);
        url = ind < 0 ? url: url.substring(0, ind);
        int q = url.lastIndexOf('?');
        if (q != -1) {
            query = url.substring(q + 1);
            path = url.substring(0, q);
        } else {
            path = url;
        }
        if (!StringUtils.isBlank(query)) {
            this.queryParams = parseQuery(query);
        }
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
    
    public String buildString() {
        final StringBuilder sb = new StringBuilder();
        
        if (this.path != null) {
            sb.append(this.path);
        }
        
        if (this.query != null) {
            sb.append("?").append(this.query);
        } else if (this.queryParams != null && !this.queryParams.isEmpty()) {
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
}
