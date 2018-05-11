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

import org.apache.commons.lang.StringEscapeUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.google.gson.JsonObject;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.ServerAPICommunicationUtil;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.integration.qtest.activation.response.ResponseActivation;
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
                    .withClaim(QTestStringConstants.IS_OFFLINE_ACTIVATION_CODE_PAYLOAY_PROPERTY, Boolean.TRUE)
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
        try {
            JsonObject activationObject = new JsonObject();
            activationObject.addProperty(QTestStringConstants.USERNAME, StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(qTestUserName)));
            activationObject.addProperty(QTestStringConstants.ACTIVATIONCODE, StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(qTestCode)));
            String result = ServerAPICommunicationUtil.put("/api/qtest-licenses/activate", activationObject.toString());

            ResponseActivation responeOb = JsonUtil.fromJson(result, ResponseActivation.class);

            if (ResponseActivation.VERIFIED_SUCCESS.equalsIgnoreCase(responeOb.getVerified())) {
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

    private static void getErrorMessage(StringBuilder errorMessage, String errorResponse) throws Exception {
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

    private static int getHostNameHashValue() throws Exception {
        String hostName = InetAddress.getLocalHost().getHostName();
        String ipAddress = InetAddress.getLocalHost().getHostAddress();

        if (hostName.equals(ipAddress)) {
            hostName = QTestStringConstants.DEFAULT_HOST_NAME;
        }

        return Objects.hash(hostName);
    }

    private static void markActivated(String activationCode) throws Exception {
        String activatedVal = Integer.toString(getHostNameHashValue());
        String curVersion = new StringBuilder(ApplicationInfo.versionNo().replaceAll("\\.", "")).reverse().toString();
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ACTIVATED_PROP_NAME, curVersion + "_" + activatedVal,
                true);
        ApplicationInfo.setAppProperty(ApplicationStringConstants.ACTIVATION_CODE, activationCode, true);
    }

    private static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = null;

        KeyFactory kf = KeyFactory.getInstance("RSA");
        EncodedKeySpec keySpec = new X509EncodedKeySpec(
                Base64.getDecoder().decode(QTestStringConstants.QTEST_ACTIVATION_PUBLIC_KEY));
        publicKey = kf.generatePublic(keySpec);

        return publicKey;
    }

}
