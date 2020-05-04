package com.kms.katalon.application.utils;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.application.constants.GnuPGConstants;
import com.kms.katalon.application.preference.ProxyPreferences;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.util.internal.ProxyUtil;
import com.kms.katalon.license.models.AwsKatalonAmi;
import com.kms.katalon.logging.LogUtil;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.callbacks.KeyringConfigCallbacks;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.InMemoryKeyring;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs;

public class AwsKatalonUtil {

    private static final String URL_KATALON_AMI_ID = "https://download.katalon.com/ami-id.json.asc";

    private static String decrypGpg(InputStream in) {
        BouncyGPG.registerProvider();
        try {
            InMemoryKeyring keyringConfig = KeyringConfigs.forGpgExportedKeys(
                    KeyringConfigCallbacks.withPassword(GnuPGConstants.gpgPassphrase));
            keyringConfig.addSecretKey(GnuPGConstants.gpgSecretKey.getBytes("US-ASCII"));

            InputStream out = BouncyGPG
                    .decryptAndVerifyStream()
                    .withConfig(keyringConfig)
                    .andIgnoreSignatures()
                    .fromEncryptedInputStream(in);
            String result = IOUtils.toString(out, "UTF-8");
            out.close();
            return result;
        } catch (Exception e) {
            LogUtil.logError(e);
        }
        return null;
    }
    
    public static AwsKatalonAmi getAwsKatalonAmi() {
        try {
            URL url = new URL(URL_KATALON_AMI_ID);
            InputStream is = url.openConnection(ProxyUtil.getProxy(ProxyPreferences.getAuthProxyInformation())).getInputStream();
            String responseBody = decrypGpg(is);
            is.close();
            if (StringUtils.isEmpty(responseBody)) {
                return null;
            }
            return JsonUtil.fromJson(responseBody, AwsKatalonAmi.class);
        } catch (Exception e) {
            LogUtil.logError(e);
        } 
        return null;
    }
}
