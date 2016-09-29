package com.kms.katalon.integration.kobiton.entity;

import java.util.Arrays;

public class KobitonDeviceCapabilities {
    public class Resolution {
        private int width;

        private int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public String toString() {
            return "Resolution [width=" + width + ", height=" + height + "]";
        }
    }

    public class Browser {
        private String name;

        private String version;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        @Override
        public String toString() {
            return "Browser [name=" + name + ", version=" + version + "]";
        }
    }

    private String sdk;

    private String udid;

    private String state;

    private String codeName;

    private String brandName;

    private String modelName;

    private String deviceName;

    private boolean isEmulator;

    private Resolution resolution;

    private String platformName;

    private String serialNumber;

    private String cpuArchitecture;

    private String platformVersion;

    private Browser[] installedBrowsers;

    public String getSdk() {
        return sdk;
    }

    public void setSdk(String sdk) {
        this.sdk = sdk;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getBrandName() {
        if (brandName == null) {
            return "";
        }
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isEmulator() {
        return isEmulator;
    }

    public void setEmulator(boolean isEmulator) {
        this.isEmulator = isEmulator;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void setResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCpuArchitecture() {
        return cpuArchitecture;
    }

    public void setCpuArchitecture(String cpuArchitecture) {
        this.cpuArchitecture = cpuArchitecture;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    public Browser[] getInstalledBrowsers() {
        return installedBrowsers;
    }

    public void setInstalledBrowsers(Browser[] installedBrowsers) {
        this.installedBrowsers = installedBrowsers;
    }

    @Override
    public String toString() {
        return "KobitonDeviceCapabilities [sdk=" + sdk + ", udid=" + udid + ", state=" + state + ", codeName="
                + codeName + ", brandName=" + brandName + ", modelName=" + modelName + ", deviceName=" + deviceName
                + ", isEmulator=" + isEmulator + ", resolution=" + resolution + ", platformName=" + platformName
                + ", serialNumber=" + serialNumber + ", cpuArchitecture=" + cpuArchitecture + ", platformVersion="
                + platformVersion + ", installedBrowsers=" + Arrays.toString(installedBrowsers) + "]";
    }
}
