package com.kms.katalon.execution.webui.configuration;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.core.network.ProxyInformation;
import com.kms.katalon.core.network.ProxyOption;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.preferences.ProxyPreferences;

public class WebDriverManagerRunConfiguration {

    private static final String GECKO_RELEASES_JSON = "https://raw.githubusercontent.com/katalon-studio/katalon-studio/master/gecko-releases.json";
    
    private File logFile;

    private File errorLogFile;

    public File getWebDriverManagerFatJar() throws IOException {
        if (Platform.inDevelopmentMode()) {
            File parentFolder = new File(ClassPathResolver
                    .getBundleLocation(FrameworkUtil.getBundle(WebDriverManagerRunConfiguration.class)));
            return new File(parentFolder, "resources/tools/webdriver/webdrivermanager-3.6.2-fat.jar");
        } else {
            File parentFolder = ClassPathResolver.getConfigurationFolder();
            return new File(parentFolder, "resources/tools/webdriver/webdrivermanager-3.6.2-fat.jar");
        }
    }

    public void downloadDriver(WebUIDriverType webUIDriverType, File driverLocation)
            throws IOException, InterruptedException {
        if (!driverLocation.exists()) {
            driverLocation.mkdirs();
        }
        File webdriverFatJarFile = getWebDriverManagerFatJar();
        String proxyCommand = getProxyCommand();
        List<String> commands = new ArrayList<>();
        commands.add(ClassPathResolver.getInstalledJRE());
        commands.add(String.format("-Dwdm.targetPath=%s", driverLocation.getCanonicalPath()));
        commands.add(String.format("-Dwdm.geckoDriverUrl=%s", GECKO_RELEASES_JSON));
        if (StringUtils.isNotEmpty(proxyCommand)) {
            commands.add(proxyCommand);
        }
        String architecture = getArchitecture(webUIDriverType);
        if (StringUtils.isNotEmpty(architecture)) {
            commands.add(architecture);
        }
        commands.add("-jar");
        commands.add(webdriverFatJarFile.getName());
        commands.add(getDriverName(webUIDriverType));
        ProcessBuilder builder = new ProcessBuilder(commands).directory(new File(webdriverFatJarFile.getParent()));
        if (getLogFile() != null) {
            builder.redirectOutput(Redirect.appendTo(getLogFile()));
        }
        if (getErrorLogFile() != null) {
            builder.redirectError(Redirect.appendTo(getErrorLogFile()));
        }
        if (!builder.start().waitFor(120, TimeUnit.SECONDS)) {
            throw new IOException("Process Timeout");
        }
    }

    private String getArchitecture(WebUIDriverType webUIDriverType) {
        if (webUIDriverType == WebUIDriverType.IE_DRIVER) {
            return "-Dwdm.architecture=32";
        }
        return "";
    }

    private String getDriverName(WebUIDriverType webUIDriverType) {
        switch (webUIDriverType) {
            case CHROME_DRIVER:
            case HEADLESS_DRIVER:
                return "chrome";
            case EDGE_DRIVER:
                return "edge";
            case FIREFOX_DRIVER:
            case FIREFOX_HEADLESS_DRIVER:
                return "firefox";
            case IE_DRIVER:
                return "iexplorer";
            default:
                throw new IllegalArgumentException("Driver is not supported");
        }
    }

    private String getProxyCommand() {
        ProxyInformation proxy = ProxyPreferences.getProxyInformation();
        switch (ProxyOption.valueOf(proxy.getProxyOption())) {
            case NO_PROXY:
            case USE_SYSTEM:
                return StringUtils.EMPTY;
            case MANUAL_CONFIG:
                String url = proxy.getProxyServerAddress();
                String port = Integer.toString(proxy.getProxyServerPort());
                String userName = proxy.getUsername();
                String password = proxy.getPassword();
                if (StringUtils.isNotEmpty(userName) && StringUtils.isNotEmpty(password)) {
                    return "-Dwdm.proxy=" + userName + ":" + password + "@" + url + ":" + port;
                } else {
                    return "-Dwdm.proxy=" + url + ":" + port;
                }
        }
        return StringUtils.EMPTY;
    }

    public File getErrorLogFile() {
        return errorLogFile;
    }

    public void setErrorLogFile(File errorLogFile) {
        this.errorLogFile = errorLogFile;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }
}
