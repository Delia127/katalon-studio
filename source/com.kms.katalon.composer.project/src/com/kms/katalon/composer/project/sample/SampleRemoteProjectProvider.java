package com.kms.katalon.composer.project.sample;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.network.HttpClientProxyBuilder;
import com.kms.katalon.execution.preferences.ProxyPreferences;

public class SampleRemoteProjectProvider {

    private static final String SAMPLE_REMOTE_PROJECT_DESCRIPTION_URL =
            "http://download.katalon.com/resources/sample_projects.json";

    private static List<SampleRemoteProject> cachedProjects;

    public static List<SampleRemoteProject> getCachedProjects() {
        if (cachedProjects == null) {
            cachedProjects = getSampleProjects();
            return cachedProjects;
        }
        return cachedProjects;
    }

    public static List<SampleRemoteProject> getSampleProjects() {
        try {
            String sampleProjectsJson = IOUtils.toString(getInputStream(SAMPLE_REMOTE_PROJECT_DESCRIPTION_URL));
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(SampleRemoteProject.class, new SampleRemoteProjectDeserializer()).create();
            Type type = new TypeToken<List<SampleRemoteProject>>() {}.getType();
            cachedProjects = new ArrayList<>(gson.fromJson(sampleProjectsJson, type));
            
            return cachedProjects;
        } catch (IOException | URISyntaxException | GeneralSecurityException e) {
            cachedProjects = Collections.emptyList();
            return cachedProjects;
        }
    }

    public Map<Integer, File> getThumbnailFiles(SampleRemoteProject project) {
        File tempFileFolder = new File(ProjectController.getInstance().getTempDir(), "welcome/samples/images");
        tempFileFolder.mkdirs();
        return project.getThumbnails().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> {
            try {
                File imageFile = new File(tempFileFolder, UUID.randomUUID() + ".png");
                imageFile.createNewFile();
                FileUtils.copyInputStreamToFile(getInputStream(e.getValue() + "?raw=true"), imageFile);

                return imageFile;
            } catch (IOException | URISyntaxException | GeneralSecurityException ex) {
                return null;
            }
        }));
    }

    public static InputStream getInputStream(String url) throws URISyntaxException, IOException, GeneralSecurityException {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(15000).setSocketTimeout(15000).build();
        HttpClientProxyBuilder builder = HttpClientProxyBuilder.create(ProxyPreferences.getProxyInformation());
        HttpClient httpClient = builder.getAcceptedSelfSignedCertClientBuilder().disableCookieManagement().setDefaultRequestConfig(requestConfig).build();
        HttpGet get = new HttpGet(new URL(url).toURI());
        return httpClient.execute(get).getEntity().getContent();
    }
}
