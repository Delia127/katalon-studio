package com.kms.katalon.integration.qtest.helper;

import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.ComposerActivationInfoCollector;

public class QTestActivationHelper {

    public static boolean qTestactivate(String activationCode, StringBuilder errorMessage) {
        try {
            String requestCode = ComposerActivationInfoCollector.genRequestActivationInfo();
            RSAPublicKey publicKey = (RSAPublicKey) getPublicKey();
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(QTestStringConstants.KMS_SIGNED_THE_TOKEN_ISSUER)
                    .withClaim(QTestStringConstants.REQUEST_CODE_PAYLOAD_PROPERTY, requestCode)
                    .acceptExpiresAt(0)
                    .build();

            verifier.verify(activationCode);
            markActivated(activationCode);
            return true;
        } catch (TokenExpiredException tkExpiredException) {
            errorMessage.append(QTestMessageConstants.EXPIRED_ACTIVATION_CODE_ERROR_MESSAGE);
        } catch (JWTVerificationException verificationException) {
            errorMessage.append(QTestMessageConstants.INVALID_ACTIVATION_CODE_ERROR_MESSAGE);
        } catch (Exception ex) {
            LogUtil.logError(ex);
            if (errorMessage != null) {
                errorMessage.append(QTestMessageConstants.INVALID_ACTIVATION_CODE_ERROR_MESSAGE);
            }
        }
        return false;
    }

    public static boolean qTestOnlineActivate(String qTestUserName, String qTestCode, StringBuilder errorMessage) {
        return false;
    }

    private static int getHostNameHashValue() throws Exception {
        String hostName = InetAddress.getLocalHost().getHostName();
        String ipAddress = InetAddress.getLocalHost().getHostAddress();

        if (hostName.equals(ipAddress)) {
            hostName = QTestStringConstants.DEFAULT_HOST_NAME;
        }

        return Objects.hash(hostName);
    }

    private static void markActivated(String userName) throws Exception {
        String activatedVal = Integer.toString(getHostNameHashValue());
        String curVersion = new StringBuilder(ApplicationInfo.versionNo().replaceAll("\\.", "")).reverse().toString();
        ApplicationInfo.removeAppProperty(ApplicationStringConstants.REQUEST_CODE_PROP_NAME);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ACTIVATED_PROP_NAME, curVersion + "_" + activatedVal,
                true);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ARG_EMAIL, userName, true);
    }

    private static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = null;

        String keyString = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClVi5Gck1X4uD9EfpQvPNe4utl"
                + "Rgw9GWhCQNa4gcya6SG3UKwgDvNGyiD3J6w4xM97NUGmDJw7INTKNikVmBjXpTRK"
                + "q/rWlSUpSkOYB9XT/pNvPf2G8U5qOMnXokGmW5r6GIVuYu6OrhAcAh0nSKYxt/vy" + "LtIvt1SFDkD320fOBwIDAQAB";

        KeyFactory kf = KeyFactory.getInstance("RSA");
        EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(keyString));
        publicKey = kf.generatePublic(keySpec);

        return publicKey;
    }

}
