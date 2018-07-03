package com.kms.katalon.composer.samples;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.integration.analytics.providers.HttpClientProxyBuilder;

public class SampleRemoteProjectProvider {

    private static final String GITHUB_REPO_URL = "https://github.com";

    private static final String GITHUB_CONTENT_URL = "https://raw.githubusercontent.com";

    private static final String GITHUB_ORGS = "katalon-studio-samples";

    public List<SampleProject> getSampleProjects() {
        try {
            Document orgPageDoc = Jsoup.parse(IOUtils.toString(getInputStream(GITHUB_REPO_URL + "/" + GITHUB_ORGS)));
            return orgPageDoc.getElementsByAttributeValueContaining("itemtype", "Code").stream().map(e -> {
                SampleProject sample = new SampleProject();
                Element projectNameElement = e.getElementsByAttributeValueStarting("itemprop", "name").get(0);
                sample.setName(projectNameElement.text().trim());
                sample.setHref(GITHUB_REPO_URL + projectNameElement.attr("href"));

                Elements descriptionElements = e.getElementsByAttributeValueStarting("itemprop", "description");
                if (descriptionElements.size() > 0) {
                    Element projectDescriptionElement = descriptionElements.get(0);
                    sample.setDescription(projectDescriptionElement.text().trim());
                }
                // sample.setThumbnail(getThumbnail(sample));

                return sample;
            }).collect(Collectors.toList());
        } catch (IOException | URISyntaxException | GeneralSecurityException e) {
            return Collections.emptyList();
        }
    }

    public List<File> getThumbnailStreams(SampleProject project) {
        try {
            File tempFileFolder = new File(ProjectController.getInstance().getTempDir(), "welcome/samples/images");
            tempFileFolder.mkdirs();
            File fileSmall = new File(tempFileFolder, UUID.randomUUID() + ".png");
            fileSmall.createNewFile();
            FileUtils.copyInputStreamToFile(getInputStream(GITHUB_CONTENT_URL + "/" + GITHUB_ORGS + "/"
                    + project.getName() + "/master/thumbnail.png?raw=true"), fileSmall);

            File fileLarge = new File(tempFileFolder, UUID.randomUUID() + ".png");
            fileLarge.createNewFile();
            FileUtils.copyInputStreamToFile(getInputStream(GITHUB_CONTENT_URL + "/" + GITHUB_ORGS + "/"
                    + project.getName() + "/master/thumbnail@2x.png?raw=true"), fileLarge);
            return Arrays.asList(fileSmall, fileLarge);
        } catch (IOException | URISyntaxException | GeneralSecurityException e) {
            return Collections.emptyList();
        }
    }

    public Image getThumbnail(Display display, SampleProject project) {
        try {
            Image thumbnailSmall = new Image(display, getInputStream(
                    GITHUB_CONTENT_URL + "/" + GITHUB_ORGS + "/" + project.getName() + "/master/thumbnail.png"));

            Image thumbnailLarge = new Image(display, getInputStream(
                    GITHUB_CONTENT_URL + "/" + GITHUB_ORGS + "/" + project.getName() + "/master/thumbnail@2x.png"));

            ImageDataProvider dateProvider = new ImageDataProvider() {

                @Override
                public ImageData getImageData(int zoom) {
                    if (zoom == 200) {
                        return thumbnailLarge.getImageData();
                    }
                    return thumbnailSmall.getImageData();
                }
            };
            return new Image(display, dateProvider);
        } catch (URISyntaxException | IOException | GeneralSecurityException ex) {
            return ImageConstants.IMG_SAMPLE_REMOTE;
        }
    }

    public InputStream getInputStream(String url) throws URISyntaxException, IOException, GeneralSecurityException {
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(15000).setSocketTimeout(15000).build();
        HttpClientProxyBuilder builder = HttpClientProxyBuilder.create(ProxyPreferences.getProxyInformation());
        HttpClient httpClient = builder.getClientBuilder().setDefaultRequestConfig(requestConfig).build();
        HttpGet get = new HttpGet(new URL(url).toURI());
        return httpClient.execute(get).getEntity().getContent();
    }
}
