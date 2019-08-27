package com.kms.katalon.license;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kms.katalon.crypto.CryptoService;
import com.kms.katalon.license.models.License;

import io.jsonwebtoken.Claims;

public class LicenseService {
	public static LicenseService serviceInstance;
	
	private LicenseService() { }
	
	public static LicenseService getInstance() {
		return serviceInstance;
	}
	
	public License parseJws(String jws) throws InvalidKeySpecException, IOException {
		CryptoService crypto = CryptoService.getInstance();
		Claims claims = crypto.parseJws(jws);
		return getLicenseFromClaims(claims);
	}
	
	public License parseJwsFromFile(String filename) throws InvalidKeySpecException, IOException {
		CryptoService crypto = CryptoService.getInstance();
		Claims claims = crypto.parseJwsFromFile(filename);
		return getLicenseFromClaims(claims);
	}
	
	private License getLicenseFromClaims(Claims claims) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
        String serializedLicense = objectMapper.writeValueAsString(claims);
        License license = objectMapper.readValue(serializedLicense, License.class);
		return license;
	}
}
