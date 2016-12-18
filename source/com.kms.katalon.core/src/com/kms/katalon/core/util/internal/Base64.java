package com.kms.katalon.core.util.internal;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.nio.charset.Charset;

import javax.xml.bind.DatatypeConverter;

/**
 * This class works around <code>base64</code> function for Java 7.
 * <p>
 * It still works correctly on Java 8 or later. Once code base upgraded to Java 8, {@link java.util.Base64} can be used
 * as a feature replacement.
 * </p>
 *
 */
public class Base64 {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    /**
     * Encode string
     * 
     * @param plainText
     * @return encoded string
     * @see #decode(String)
     */
    public static String encode(String plainText) {
        if (isBlank(plainText)) {
            return plainText;
        }

        return DatatypeConverter.printBase64Binary(plainText.getBytes(UTF8));
    }

    /**
     * Decode encoded string
     * 
     * @param encodedText
     * @return decoded string
     * @see #encode(String)
     */
    public static String decode(String encodedText) {
        if (isBlank(encodedText)) {
            return encodedText;
        }

        return new String(DatatypeConverter.parseBase64Binary(encodedText), UTF8);
    }
}
