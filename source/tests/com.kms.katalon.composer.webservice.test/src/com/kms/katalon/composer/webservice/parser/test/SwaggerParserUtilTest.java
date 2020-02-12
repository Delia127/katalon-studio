package com.kms.katalon.composer.webservice.parser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.composer.webservice.parser.SwaggerParserUtil;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
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

        List<WebServiceRequestEntity> getSkuDetailsWebServices = webServices.stream()
                .filter(ws -> ws.getRestUrl().equals("https://localhost/ecom/catalog/browse/v1.0/getSkuDetails"))
                .collect(Collectors.toList());

        assertEquals(getSkuDetailsWebServices.size(), 3);

        WebServiceRequestEntity post = getSkuDetailsWebServices.stream()
                .filter(ws -> ws.getRestRequestMethod().equals("POST"))
                .findAny()
                .orElse(null);

        assertNotNull(post);
        assertNotEquals(post.getName(), "Web service Object");
        WebElementPropertyEntity contentType = post.getHttpHeaderProperties().get(0);
        assertEquals(contentType.getName(), "Content-type");
        assertEquals(contentType.getValue(), "application/json");
        WebElementPropertyEntity accept = post.getHttpHeaderProperties().get(1);
        assertEquals(accept.getName(), "Accept");
        assertEquals(accept.getValue(), "application/json");

        WebServiceRequestEntity get = getSkuDetailsWebServices.stream()
                .filter(ws -> ws.getRestRequestMethod().equals("GET"))
                .findAny()
                .orElse(null);

        assertNotNull(get);
        assertEquals(get.getName(), "Web service Object");
        WebElementPropertyEntity contentType1 = get.getHttpHeaderProperties().get(0);
        assertEquals(contentType1.getName(), "Content-type");
        assertEquals(contentType1.getValue(), "application/json");
        WebElementPropertyEntity accept2 = get.getHttpHeaderProperties().get(1);
        assertEquals(accept2.getName(), "Accept");
        assertEquals(accept2.getValue(), "text/plain");
    }

    private File getExtensionsDirectory(String path) throws IOException {
        File bundleFile = FileLocator.getBundleFile(Platform.getBundle("com.kms.katalon.composer.webservice.test"));
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + path);
        } else { // run as product
            return new File(ClassPathResolver.getConfigurationFolder(), path);
        }
    }
}
