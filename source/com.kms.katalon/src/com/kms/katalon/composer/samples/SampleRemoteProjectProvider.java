package com.kms.katalon.composer.samples;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.widgets.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.integration.analytics.providers.HttpClientProxyBuilder;

public class SampleRemoteProjectProvider {

    private static final String SAMPLE_REMOTE_PROJECT_DESCRIPTION_URL =
            "http://download.katalon.com/resources/sample_projects.json";

    public List<SampleRemoteProject> getSampleProjects() {
        try {
            String sampleProjectsJson = IOUtils.toString(getInputStream(SAMPLE_REMOTE_PROJECT_DESCRIPTION_URL));
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(SampleRemoteProject.class, new SampleRemoteProjectDeserializer()).create();
            Type type = new TypeToken<List<SampleRemoteProject>>() {}.getType();
            return new ArrayList<>(gson.fromJson(sampleProjectsJson, type));
        } catch (IOException | URISyntaxException | GeneralSecurityException e) {
            return Collections.emptyList();
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

    public Image getThumbnail(Display display, SampleRemoteProject project) {
        Map<Integer, Image> allImages = project.getThumbnails()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    try {
                        return new Image(display, getInputStream(e.getValue()));
                    } catch (URISyntaxException | IOException | GeneralSecurityException e1) {
                        return ImageConstants.IMG_SAMPLE_REMOTE;
                    }
                }));
        ImageDataProvider dateProvider = new ImageDataProvider() {

            @Override
            public ImageData getImageData(int zoom) {
                return allImages.getOrDefault(allImages.get(zoom), allImages.get(100)).getImageData();
            }
        };
        return new Image(display, dateProvider);
    }

    public InputStream getInputStream(String url) throws URISyntaxException, IOException, GeneralSecurityException {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(15000).setSocketTimeout(15000).build();
        HttpClientProxyBuilder builder = HttpClientProxyBuilder.create(ProxyPreferences.getProxyInformation());
        HttpClient httpClient = builder.getClientBuilder().setDefaultRequestConfig(requestConfig).build();
        HttpGet get = new HttpGet(new URL(url).toURI());
        return httpClient.execute(get).getEntity().getContent();
    }
}
