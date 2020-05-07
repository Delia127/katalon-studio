package com.kms.katalon.groovy.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager;
import org.codehaus.groovy.tools.groovydoc.FileOutputTool;
import org.codehaus.groovy.tools.groovydoc.GroovyDocTool;
import org.codehaus.groovy.tools.groovydoc.LinkArgument;
import org.codehaus.groovy.tools.groovydoc.gstringTemplates.GroovyDocTemplateInfo;

import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.project.ProjectEntity;

public class CustomKeywordJavadocGenerator {
    
    private ProjectEntity project;
    
    public CustomKeywordJavadocGenerator(ProjectEntity project) {
        this.project = project;
    }

    public void generate(String destination) throws Exception {
        prepareDestinationFolder(destination);
        
        Properties properties = new Properties();
        properties.put("windowTitle", "Custom Keywords");
        properties.put("docTitle", "Custom Keywords");

        List<LinkArgument> links = getLinks();
        
        List<String> sourceFileToDocs = collectCustomKeywordSourceFileNames();
        
        String[] sourcePath = new String[]{getCustomKeywordFolder(project).getAbsolutePath()};
        
        GroovyDocTool htmlTool = new GroovyDocTool(
            new ClasspathResourceManager(),
            sourcePath,
            GroovyDocTemplateInfo.DEFAULT_DOC_TEMPLATES,
            GroovyDocTemplateInfo.DEFAULT_PACKAGE_TEMPLATES,
            GroovyDocTemplateInfo.DEFAULT_CLASS_TEMPLATES,
            links,
            properties
        );
        
        htmlTool.add(sourceFileToDocs);
        FileOutputTool output = new FileOutputTool();
        htmlTool.renderToOutput(output, destination);
    }
    
    private void prepareDestinationFolder(String destination) throws IOException {
        File destinationFolder = new File(destination);
        destinationFolder.mkdir();
        FileUtils.cleanDirectory(destinationFolder);
    }
    
    private List<LinkArgument> getLinks() {
        Map<String, String> linkMap = new HashMap<>();
        linkMap.put("java.,org.xml.,javax.,org.xml.", "http://docs.oracle.com/javase/8/docs/api/");
        linkMap.put("org.apache.tools.ant.", "http://docs.groovy-lang.org/docs/ant/api/");
        linkMap.put("org.junit.,junit.framework.", "http://junit.org/junit4/javadoc/latest/");
        linkMap.put("groovy.,org.codehaus.groovy.", "http://docs.groovy-lang.org/latest/html/api/");
        linkMap.put("org.codehaus.gmaven.", "http://groovy.github.io/gmaven/apidocs/");
        return linkMap.entrySet().stream()
            .map(e -> {
                LinkArgument linkArgument = new LinkArgument();
                linkArgument.setPackages(e.getKey());
                linkArgument.setHref(e.getValue());
                return linkArgument;
            }).collect(Collectors.toList());
    }
    
    private List<String> collectCustomKeywordSourceFileNames() throws IOException {
        File keywordFolder = getCustomKeywordFolder(project);
        List<String> sourceFilesToDoc = Files.walk(Paths.get(keywordFolder.toURI()))
            .filter(Files::isRegularFile)
            .filter(p -> "groovy".equals(FilenameUtils.getExtension(p.toFile().getAbsolutePath())))
            .map(p -> getKeywordRelativePath(p.toFile()))
            .collect(Collectors.toList());
        return sourceFilesToDoc;
    }
    
    private String getKeywordRelativePath(File sourceFile) {
        File keywordFolder = getCustomKeywordFolder(project);
        return PathUtil.absoluteToRelativePath(sourceFile.getAbsolutePath(), keywordFolder.getAbsolutePath());
    }
    
    private static File getCustomKeywordFolder(ProjectEntity project) {
        File keywordFolder = new File(project.getFolderLocation(), "Keywords");
        return keywordFolder;
    }
    
    public static File getDefaultKeywordJavadocFolder(ProjectEntity project) {
        File javadocFolder = new File(project.getFolderLocation(), "Keywords Javadoc");
        return javadocFolder;
    }
}
