package com.kms.katalon.composer.artifact.core.util;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.katalon.platform.api.model.ProjectEntity;

public class KeywordUtil {

    public static String getKeywordRootFolder(ProjectEntity project) {
        return project.getFolderLocation() + File.separator + "Keywords";
    }

    public static String getKeywordParentRelativePath(ProjectEntity project, File keyword) {
        String keywordRootFolderLocation = getKeywordRootFolder(project);
        String keywordFolderLocation = StringUtils.replace(keyword.getParentFile().getAbsolutePath(), "%", File.separator);
        if (keywordFolderLocation.equals(keywordRootFolderLocation)) {
            return StringUtils.EMPTY;
        } else {
            String parentRelativePath = keywordFolderLocation
                    .substring((keywordRootFolderLocation + File.separator).length());
            return parentRelativePath;
        }
    }
}
