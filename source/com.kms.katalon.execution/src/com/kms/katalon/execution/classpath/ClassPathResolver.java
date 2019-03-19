package com.kms.katalon.execution.classpath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.project.ProjectEntity;

public class ClassPathResolver {
    private static final String LIB_BIN_FOLDER = "lib";
    private static final String KEYWORD_BIN_FOLDER = "keyword";
    private static final String GROOVY_SOURCE_BIN_FOLDER = "groovy";
    private static final String BIN_FOLDER= "bin";

    private ClassPathResolver() {
        // Disable default constructor
    }

    private static List<IBuildPath> getPlatformBuildPaths() throws IOException {
        File configurationFolder = getConfigurationFolder();
        File resourceLib = new File(configurationFolder, "resources/lib");

        if (!resourceLib.exists()) {
            return Collections.emptyList();
        }

        List<IBuildPath> pfBuildPaths = new ArrayList<IBuildPath>();
        for (File jarFile : resourceLib.listFiles()) {
            pfBuildPaths.add(new BuildPathEntry(jarFile.getAbsolutePath()));
        }
        return pfBuildPaths;
    }

    public static File getConfigurationFolder() throws IOException {
        return new File(FileLocator.resolve(Platform.getConfigurationLocation().getURL()).getFile());
    }

    private static List<String> getPlatformBuildPathLocs() throws IOException {
        List<String> pfBuildPathLocs = new ArrayList<String>();
        for (IBuildPath jarFileBuildPath : getPlatformBuildPaths()) {
            pfBuildPathLocs.add(jarFileBuildPath.getBuildPathLocation());
        }

        return pfBuildPathLocs;
    }
    
    public static String[] getClassPaths(ProjectEntity project) throws IOException, ControllerException {
        ProjectBuildPath prjBuildpath = new ProjectBuildPath(project);
        List<String> classPathLocs = prjBuildpath.getClassPaths();
        classPathLocs.addAll(getPlatformBuildPathLocs());
        
        String binFolderRelativePath = project.getFolderLocation() + File.separator + BIN_FOLDER + File.separator;
        classPathLocs.add(binFolderRelativePath + KEYWORD_BIN_FOLDER);
        classPathLocs.add(binFolderRelativePath + LIB_BIN_FOLDER);
        classPathLocs.add(binFolderRelativePath + GROOVY_SOURCE_BIN_FOLDER);

        return classPathLocs.toArray(new String[classPathLocs.size()]);
    }

    public static String getBundleLocation(Bundle bundle) throws IOException {
        return FileLocator.getBundleFile(bundle).getAbsolutePath();
    }
}
