package com.kms.katalon.composer.samples;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.constants.ImageConstants;


public class SampleProject {

    private String name;

    private String description;

    private String href;
    
    private Image thumbnail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Image getThumbnail() {
        if (thumbnail == null) {
            thumbnail = ImageConstants.IMG_SAMPLE_REMOTE;
        }
        return thumbnail;
    }

    public void setThumbnail(Image thumbnail) {
        this.thumbnail = thumbnail;
    }
}
