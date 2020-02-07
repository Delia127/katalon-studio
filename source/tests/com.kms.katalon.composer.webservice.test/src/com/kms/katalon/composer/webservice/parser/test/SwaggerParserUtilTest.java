package com.kms.katalon.composer.webservice.parser.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.composer.webservice.parser.SwaggerParserUtil;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.execution.classpath.ClassPathResolver;

public class SwaggerParserUtilTest {
    String pathToSwaggerDefinitionJson = "";

    ProjectEntity projectFolder;

    FolderEntity folderEntity;

    File tmpDir;

    @Before
    public void setUp() throws Exception {
        pathToSwaggerDefinitionJson = getExtensionsDirectory("resources/swagger/Catalog-Browse.json").getAbsolutePath();
        projectFolder = new ProjectEntity();
        folderEntity = new FolderEntity();
        folderEntity.setProject(projectFolder);
    }

    @Test
    public void testParseFromFileLocationToWSTestObject() throws Exception {
        List<WebServiceRequestEntity> webServices = SwaggerParserUtil.newWSTestObjectsFromSwagger(folderEntity,
                pathToSwaggerDefinitionJson);
        assertEquals(webServices.size(), 17);
    }

    private File getExtensionsDirectory(String path) throws IOException {
        File bundleFile = FileLocator
                .getBundleFile(Platform.getBundle("com.kms.katalon.composer.webservice.test"));
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + path);
        } else { // run as product
            return new File(ClassPathResolver.getConfigurationFolder(), path);
        }
    }
}
