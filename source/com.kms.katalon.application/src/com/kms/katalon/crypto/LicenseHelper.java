package com.kms.katalon.crypto;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.kms.katalon.license.constants.LicenseConstants;
import com.google.api.client.util.PemReader;
import com.google.api.client.util.PemReader.Section;

public class LicenseHelper {
    private static LicenseHelper serviceInstance;
    
    private LicenseHelper() {
    }
    
    public static LicenseHelper getInstance() {
        if (serviceInstance == null) {
            serviceInstance = new LicenseHelper();
        }
        return serviceInstance;
    }
    
    
    public Map<String, Claim> parseJws(String jws) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        if (jws.isEmpty()) {
            return null;
        }

        RSAPublicKey publicKey = (RSAPublicKey) getPublicKey();
        Algorithm algorithm = Algorithm.RSA256(publicKey, null);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        return verifier.verify(jws).getClaims();
    }
    
    public Map<String, Claim> parseJwsFromFile(String filename) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        String jws = new String(Files.readAllBytes(Paths.get(filename)));
        return parseJws(jws);
    }
    
    public PublicKey getPublicKey() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException  {
        PublicKey publicKey = null;

        KeyFactory kf = KeyFactory.getInstance("RSA");
        byte[] bytes = readPemString(LicenseConstants.LICENSE_PUBLIC_KEY);
        EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        publicKey = kf.generatePublic(keySpec);

        return publicKey;
    }

    private byte[] readPemString(String string) throws IOException {
    	Section section = PemReader.readFirstSectionAndClose(new StringReader(string)); 
        return section.getBase64DecodedBytes();
    }
}
