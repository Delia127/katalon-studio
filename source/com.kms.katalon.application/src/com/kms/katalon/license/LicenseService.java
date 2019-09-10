package com.kms.katalon.license;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import com.kms.katalon.crypto.LicenseHelper;
import com.kms.katalon.license.models.Feature;
import com.kms.katalon.license.models.License;
import com.auth0.jwt.interfaces.Claim;

import com.kms.katalon.license.constants.LicenseConstants;

public class LicenseService {
    public static LicenseService serviceInstance;
    
    private LicenseService() { }
    
    public static LicenseService getInstance() {
        if (serviceInstance == null) {
            serviceInstance = new LicenseService();
        }
        return serviceInstance;
    }
    
    public License parseJws(String jws) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        LicenseHelper licenseHelper = LicenseHelper.getInstance();
        Map<String, Claim> claims = licenseHelper.parseJws(jws);
        return getLicenseFromClaims(claims);
    }
    
    public License parseJwsFromFile(String filename) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        LicenseHelper licenseHelper = LicenseHelper.getInstance();
        Map<String, Claim> claims = licenseHelper.parseJwsFromFile(filename);
        return getLicenseFromClaims(claims);
    }
    
    private License getLicenseFromClaims(Map<String, Claim> claims) throws IOException {
        License license = new License();
        license.setExpirationDate(claims.get(LicenseConstants.EXPIRATION_DATE).asDate());
        license.setMachineId(claims.get(LicenseConstants.MACHINE_ID).asString());
        license.setOrganizationId(claims.get(LicenseConstants.ORGANIZATION_ID).asLong());
        license.setFeatures(claims.get(LicenseConstants.FEATURES).asList(Feature.class));
        return license;
    }
}
