package com.kms.katalon.composer.webservice.soapui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kms.katalon.entity.folder.FolderEntity;

public class SoapUIProjectImportResult extends SoapUIImportNode {

    private FolderEntity projectFolder;

    private List<SoapUIRestServiceImportResult> restServiceImportResults = new ArrayList<>();

    private Set<String> serviceFolderNames = new HashSet<>();
    
    private List<SoapUIOAuth1Credential> oAuth1Credentials = new ArrayList<>();
    
    private List<SoapUIOAuth2Credential> oauth2Credentials = new ArrayList<>();

    public SoapUIProjectImportResult(FolderEntity folder) {
        this.projectFolder = folder;
    }

    @Override
    public FolderEntity getFileEntity() {
        return projectFolder;
    }

    public SoapUIRestServiceImportResult[] getServiceImportResults() {
        return restServiceImportResults.toArray(new SoapUIRestServiceImportResult[restServiceImportResults.size()]);
    }

    public SoapUIRestServiceImportResult newService(String name) {
        if (!isServiceFolderNameAvailable(name)) {
            throw new IllegalArgumentException("Service folder name already exists.");
        }
        serviceFolderNames.add(name);

        FolderEntity folder = newFolder(name, projectFolder);
        SoapUIRestServiceImportResult serviceResult = new SoapUIRestServiceImportResult(this, folder);
        restServiceImportResults.add(serviceResult);
        return serviceResult;
    }
    
    public SoapUIOAuth1Credential newOAuth1Credential(String profileName) {
        SoapUIOAuth1Credential credential = new SoapUIOAuth1Credential(profileName);
        oAuth1Credentials.add(credential);
        return credential;
    }
    
    public SoapUIOAuth1Credential getOAuth1CredentialByProfile(String profileName) {
        return oAuth1Credentials.stream()
                .filter(c -> c.getProfileName().equals(profileName))
                .findAny()
                .orElse(null);
    }
    
    public SoapUIOAuth2Credential newOAuth2Credential(String profileName) {
        SoapUIOAuth2Credential credential = new SoapUIOAuth2Credential(profileName);
        oauth2Credentials.add(credential);
        return credential;
    }
    
    public SoapUIOAuth2Credential getOAuth2CredentialByProfile(String profileName) {
        return oauth2Credentials.stream()
                .filter(c -> c.getProfileName().equals(profileName))
                .findAny()
                .orElse(null);
    }

    public boolean isServiceFolderNameAvailable(String folderName) {
        return !serviceFolderNames.contains(folderName);
    }

    @Override
    public SoapUIImportNode[] getChildImportNodes() {
        return restServiceImportResults.toArray(new SoapUIImportNode[restServiceImportResults.size()]);
    }
}
