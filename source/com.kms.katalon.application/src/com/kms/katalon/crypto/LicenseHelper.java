package com.kms.katalon.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import com.kms.katalon.license.constants.LicenseConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class LicenseHelper {
    private static LicenseHelper serviceInstance;

    private PublicKey publicKey;
    
    private LicenseHelper() {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public static LicenseHelper getInstance() {
        if (serviceInstance == null) {
            serviceInstance = new LicenseHelper();
        }
        return serviceInstance;
    }
    
    public Claims parseJwsFromFile(String filename) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        prepareKeys();
        String jws = new String(Files.readAllBytes(Paths.get(filename)));
        Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jws).getBody();
        return claims;
    }

    public Claims parseJws(String jws) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        prepareKeys();
        Claims claims = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(jws).getBody();
        return claims;
    }
    
    private void prepareKeys() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        if (publicKey != null) {
            return;
        }
        loadPublicKey();
    }

    public void loadPublicKey() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        publicKey = readPublicKey();
    }

    public PublicKey readPublicKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        InputStream inputStream = new ByteArrayInputStream(
                LicenseConstants.LICENSE_PUBLIC_KEY.getBytes(Charset.forName("UTF-8")));
        return readPublicKey(inputStream);
    }
    
    public PublicKey readPublicKey(InputStream inputStream) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        PemReader pemReader = new PemReader(new InputStreamReader(inputStream));
        PemObject pemObject = pemReader.readPemObject();
        try {
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(content);
            return KeyFactory.getInstance("RSA", "BC") .generatePublic(publicKeySpec);
        } finally {
            pemReader.close();
        }
    }
}
