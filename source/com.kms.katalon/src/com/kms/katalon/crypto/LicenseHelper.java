package com.kms.katalon.crypto;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class LicenseHelper {
	private static LicenseHelper serviceInstance;
	
	private LicenseHelper() { }

    private String defaultKeyStoreFolder = ".";

    private String publicKeyFileName = "public-key.pem";

    private KeyFactory keyFactory;

    private PublicKey publicKey;
    
    public static LicenseHelper getInstance() {
    	if (serviceInstance == null) {
    		serviceInstance = new LicenseHelper();
    	}
    	return serviceInstance;
    }

    public String getDefaultKeyStoreFolder() {
        return defaultKeyStoreFolder;
    }

    public void setDefaultKeyStoreFolder(String defaultKeyStoreFolder) {
        this.defaultKeyStoreFolder = defaultKeyStoreFolder;
    }

    public String getPublicKeyFileName() {
        return publicKeyFileName;
    }

    public void setPublicKeyFileName(String publicKeyFileName) {
        this.publicKeyFileName = publicKeyFileName;
    }
    
    
    public Claims parseJwsFromFile(String filename) throws IOException, InvalidKeySpecException {
        prepareKeys();
        String jws = new String(Files.readAllBytes(Paths.get(filename)));
        Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jws).getBody();
        return claims;
    }

    public Claims parseJws(String jws) throws IOException, InvalidKeySpecException {
        prepareKeys();
        Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jws).getBody();
        return claims;
    }
    
    private void prepareKeys() throws IOException, InvalidKeySpecException {
        if (publicKey != null) {
            return;
        }
        try {
            loadPublicKey();
        } catch (IOException exception) {
        }
    }

    public void loadPublicKey() throws IOException, InvalidKeySpecException {
    	loadPublicKey(getDefaultKeyStoreFolder());
    }

    public void loadPublicKey(String keyStoreFolder) throws IOException, InvalidKeySpecException {
        String publicKeyFileName = Paths.get(keyStoreFolder).resolve(getPublicKeyFileName()).toString();
        publicKey = readPublicKey(publicKeyFileName);
    }

    public PublicKey readPublicKey(String filename) throws IOException, InvalidKeySpecException {
        return readPublicKey(new FileInputStream(filename));
    }
    
    public PublicKey readPublicKey(InputStream inputStream) throws IOException, InvalidKeySpecException {
        PemReader pemReader = new PemReader(new InputStreamReader(inputStream));
        PemObject pemObject = pemReader.readPemObject();
        try {
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(content);
            return keyFactory.generatePublic(publicKeySpec);
        } finally {
            pemReader.close();
        }
    }
}
