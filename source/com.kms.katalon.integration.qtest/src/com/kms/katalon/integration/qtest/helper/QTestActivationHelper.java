package com.kms.katalon.integration.qtest.helper;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.util.PemReader;
import com.google.gson.JsonObject;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.ServerAPICommunicationUtil;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.integration.qtest.constants.QTestMessageConstants;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

public class QTestActivationHelper {

    public static boolean activate(String activationCode, String requestCode, StringBuilder errorMessage) {
        try {
            RSAPublicKey publicKey = (RSAPublicKey) getPublicKey();
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(QTestStringConstants.KMS_SIGNED_THE_TOKEN_ISSUER)
                    .withClaim(QTestStringConstants.PAYLOAD_PROP_REQUEST_CODE, requestCode)
                    .withClaim(QTestStringConstants.PAYLOAD_PROP_IS_OFFLINE, Boolean.TRUE)
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

    public static boolean qTestOnlineActivate(String username, String activationCode, StringBuilder errorMessage) {
        try {
            JsonObject activationObject = new JsonObject();
            activationObject.addProperty(QTestStringConstants.USERNAME,
                    StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(username)));
            activationObject.addProperty(QTestStringConstants.REQUEST_PROP_ACTIVATION_CODE,
                    StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(activationCode)));
            String result = ServerAPICommunicationUtil.post("/qtest-licenses/activate", activationObject.toString());

            ResponseActivation responeOb = JsonUtil.fromJson(result, ResponseActivation.class);

            if (ResponseActivation.VERIFIED_SUCCESS.equalsIgnoreCase(responeOb.getVerified())) {
                markActivated(activationCode);
                return true;
            }

            if (ResponseActivation.VERIFIED_FAIL.equalsIgnoreCase(responeOb.getVerified())) {
                getErrorMessage(errorMessage, responeOb.getMessage());
                return false;
            }

            errorMessage.append(QTestMessageConstants.ONLINE_ACTIVATION_LICENSE_NOT_FOUND_MSG_ERR);
            return false;

        } catch (Exception ex) {
            errorMessage.append(QTestMessageConstants.ONLINE_ACTIVATION_INTERNAL_SERVER_ERROR_MSG_ERROR);
            return false;
        }
    }

    public static ActivationStatus checkActivationStatus() {
        try {
            return getDecodedJwt() != null ? ActivationStatus.VALIDATED : ActivationStatus.NOT_ACTIVATED;
        } catch (TokenExpiredException tkExpiredException) {
            return ActivationStatus.EXPIRED;
        } catch (IOException | GeneralSecurityException e) {
            return ActivationStatus.NOT_ACTIVATED;
        }
    }

    private static DecodedJWT getDecodedJwt()
            throws IOException, GeneralSecurityException{
        String activationCode = getApplicationActivationCode();

        if (StringUtils.isEmpty(activationCode)) {
            return null;
        }

        RSAPublicKey publicKey = (RSAPublicKey) getPublicKey();
        Algorithm algorithm = Algorithm.RSA256(publicKey, null);

        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(QTestStringConstants.KMS_SIGNED_THE_TOKEN_ISSUER)
                .build();
        return verifier.verify(activationCode);
    }

    public static ActivationPayload getActivationPayload() throws IOException, GeneralSecurityException {
        DecodedJWT jwt = getDecodedJwt();
        if (jwt == null) {
            return null;
        }
        ActivationPayload payload = new ActivationPayload();
        payload.setExp(jwt.getExpiresAt());
        return payload;
    }

    private static void getErrorMessage(StringBuilder errorMessage, String errorResponse) {
        switch (errorResponse) {
            case ResponseActivation.LICENSE_ALREADY_ACTIVATED_FAIL_MSG:
                errorMessage.append(QTestMessageConstants.ONLINE_ACTIVATION_LICENSE_ALREADY_ACTIVTED_MSG_ERR);
                break;

            case ResponseActivation.LICENSE_EXPIRED_FAIL_MSG:
                errorMessage.append(QTestMessageConstants.EXPIRED_ACTIVATION_CODE_ERROR_MESSAGE);
                break;

            default:
                errorMessage.append(QTestMessageConstants.ONLINE_ACTIVATION_LICENSE_NOT_FOUND_MSG_ERR);
                break;
        }
    }

    private static int getHostNameHashValue() throws IOException {
        String hostName = InetAddress.getLocalHost().getHostName();
        String ipAddress = InetAddress.getLocalHost().getHostAddress();

        if (hostName.equals(ipAddress)) {
            hostName = QTestStringConstants.DEFAULT_HOST_NAME;
        }

        return Objects.hash(hostName);
    }

    private static void markActivated(String activationCode) throws IOException, GeneralSecurityException {
        String activatedVal = Integer.toString(getHostNameHashValue());
        String curVersion = new StringBuilder(ApplicationInfo.versionNo().replaceAll("\\.", "")).reverse().toString();
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ACTIVATED_PROP_NAME, curVersion + "_" + activatedVal,
                true);
        String encodedActivationCode = CryptoUtil
                .encode(CryptoUtil.getDefault(Integer.toString(getHostNameHashValue()), activationCode));
        ApplicationInfo.setAppProperty(QTestStringConstants.APP_PROP_ACTIVATION_CODE, encodedActivationCode, true);
        ApplicationInfo.removeAppProperty(ApplicationStringConstants.REQUEST_CODE_PROP_NAME);
    }

    private static String getApplicationActivationCode() throws IOException, GeneralSecurityException {
        String encodedActivationCode = ApplicationInfo.getAppProperty(QTestStringConstants.APP_PROP_ACTIVATION_CODE);
        if (encodedActivationCode != null) {
            return CryptoUtil
                    .decode(CryptoUtil.getDefault(Integer.toString(getHostNameHashValue()), encodedActivationCode));
        }
        return null;
    }

    private static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        PublicKey publicKey = null;

        KeyFactory kf = KeyFactory.getInstance("RSA");
        EncodedKeySpec keySpec = new X509EncodedKeySpec(
                readPemString(QTestStringConstants.QTEST_ACTIVATION_PUBLIC_KEY));
        publicKey = kf.generatePublic(keySpec);

        return publicKey;
    }

    private static byte[] readPemString(String string) throws IOException {
        return PemReader.readFirstSectionAndClose(new StringReader(string)).getBase64DecodedBytes();
    }

    public static enum ActivationStatus {
        NOT_ACTIVATED, VALIDATED, EXPIRED
    }
}
