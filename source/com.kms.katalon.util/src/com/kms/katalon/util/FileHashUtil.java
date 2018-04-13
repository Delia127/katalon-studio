package com.kms.katalon.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class FileHashUtil {
    public static String getFileHash(Path filePath, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(Files.readAllBytes(filePath));
        return DatatypeConverter
                .printHexBinary(md.digest())
                .toUpperCase();
    }
}
