package com.kms.katalon.objectsly.util.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;
import org.testng.Assert;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.util.WebElementUtils;

public class WebElementUtilsTest {
    private File getExtensionsDirectory(String path) throws IOException {
        File bundleFile = FileLocator
                .getBundleFile(Platform.getBundle("com.kms.katalon.composer.webui.objectspy.test"));
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + path);
        } else { // run as product
            return new File(ClassPathResolver.getConfigurationFolder(), path);
        }
    }

    @Test
    public void canConvertWebElementWithPeriodsInNameToTestObjectWithTheSameName() throws Exception {
        WebElement webElement = new WebElement("This.is.my.name");
        FolderEntity parentFolder = new FolderEntity();
        ProjectEntity projectEntity = new ProjectEntity();
        File file = getExtensionsDirectory("/test_project");
        projectEntity.setProjectFile(file);
        projectEntity.setName("test_project_name");
        parentFolder.setProject(projectEntity);
        parentFolder.setName("parent_folder");
        WebElementEntity entity = WebElementUtils.convertWebElementToTestObject(webElement, null, parentFolder);
        Assert.assertEquals(entity.getName(), webElement.getName());
    }
}
