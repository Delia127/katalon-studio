package com.kms.katalon.composer.webservice.constants;

public interface HttpHeaderConstants {

    /**
     * List of HTTP header request fields (standard and non-standard)
     * 
     * @see <a href="https://en.wikipedia.org/wiki/List_of_HTTP_header_fields">List of HTTP header fields</a>
     */
    public static final String[] PRE_DEFINDED_HTTP_HEADER_FIELD_NAMES = new String[] { "Accept", "Accept-Charset",
            "Accept-Encoding", "Accept-Language", "Authorization", "Cache-Control", "Connection", "Content-Length",
            "Content-Type", "Cookie", "DNT", "Date", "Expect", "From", "Front-End-Https", "Host", "If-Match",
            "If-Modified-Since", "If-None-Match", "If-Range", "If-Unmodified-Since", "Max-Forwards", "Origin", "Pragma",
            "Proxy-Authorization", "Proxy-Connection", "Range", "Referer", "TE", "Upgrade", "User-Agent", "Via",
            "Warning", "X-ATT-DeviceId", "X-Csrf-Token", "X-Forwarded-For", "X-Forwarded-Host", "X-Forwarded-Proto",
            "X-Http-Method-Override", "X-Requested-With", "X-UIDH", "X-Wap-Profile" };

    /**
     * @see <a href="https://en.wikipedia.org/wiki/HTTP_compression#Content-Encoding_tokens">HTTP compression</a>
     * @see <a href="http://www.iana.org/assignments/media-types/media-types.xhtml">Media Types</a>
     */
    public static final String[] PRE_DEFINDED_HTTP_HEADER_FIELD_VALUES = new String[] { "utf-8", "utf-16", "iso-8859-1",
            "keep-alive", "close", "compress", "deflate", "exi", "gzip", "identity", "pack200-gzip", "br",
            "application/json", "application/javascript", "application/xhtml+xml", "application/xml",
            "application/x-www-form-urlencoded", "application/zip", "application/soap+xml", "application/gzip",
            "multipart/form-data", "text/html", "text/plain", "text/css", "text/csv", "text/javascript", "text/sgml",
            "text/xml" };

}
