package com.kms.katalon.execution.mobile.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.security.auth.x500.X500Principal;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.core.util.ConsoleCommandExecutor;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.identity.IosIdentityInfo;

public class SystemCertificationUtil {
    static final String GET_CERTIFICATE_COMMAND = "security find-certificate -p -c \"%s\"";

    static public Certificate getCertificate(String name) throws IOException, InterruptedException, CertificateException {
        Map<String, String> envs = IosDeviceInfo.getIosAdditionalEnvironmentVariables();
        List<String> rawCertificates = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(
                generateCommand(String.format(GET_CERTIFICATE_COMMAND, name)), envs, true);

        List<Certificate> certificates = readCertificates(rawCertificates);

        return (certificates != null && certificates.size() > 0) ? certificates.get(0) : null;
    }

    static public List<Certificate> readCertificates(List<String> certificateLines) throws CertificateException, IOException {
        String rawCertificates = certificateLines.stream().collect(Collectors.joining("\n"));

        InputStream inputStream = new ByteArrayInputStream(rawCertificates.getBytes());
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

        List<Certificate> certificates = new ArrayList<>();
        while (inputStream.available() > 0) {
            try {
                Certificate cert = certificateFactory.generateCertificate(inputStream);
                certificates.add(cert);
            } catch (CertificateException error) {
                LoggerSingleton.logError(error);
                // Just skip invalid certificates
            }
        }
        return certificates;
    }

    static public IosIdentityInfo getTeamInfoFromCertificate(Certificate cert) {
        X500Principal subject = ((X509Certificate) cert).getSubjectX500Principal();
        String subjectString = subject.getName(X500Principal.RFC2253);

        String id = null, name = null;

        Matcher idMatcher = Pattern.compile("OU=(\\w+),").matcher(subjectString);
        if (idMatcher.find()) {
            id = idMatcher.group(1);
        }
        Matcher nameMatcher = Pattern.compile("O=(.+?),").matcher(subjectString);
        if (nameMatcher.find()) {
            name = nameMatcher.group(1);
        }
        if (id == null || name == null) {
            return null;
        }
        IosIdentityInfo team = new IosIdentityInfo(name, id);
        return team;
    }
    
    static public IosIdentityInfo getTeamInfoByCertificateName(String name) throws CertificateException, IOException, InterruptedException {
        Certificate cert = getCertificate(name);
        if (cert == null) {
            return null;
        }
        return getTeamInfoFromCertificate(cert);
    }

    static private String[] generateCommand(String command) {
        return new String[] { "/bin/sh", "-c", command };
    }
}
