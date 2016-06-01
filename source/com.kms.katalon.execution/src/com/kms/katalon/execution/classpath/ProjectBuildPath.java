package com.kms.katalon.execution.classpath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.core.appium.driver.AppiumDriverManager;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.keyword.KeywordContributorCollection;
import com.kms.katalon.entity.project.ProjectEntity;

public class ProjectBuildPath {
    private static final String EXTERNAL_DRIVERS_FOLDER = "Drivers";

    public static final String DF_OUT_PUT_LOC = "bin";

    private ProjectEntity project;

    public ProjectBuildPath(ProjectEntity project) {
        this.project = project;
    }

    public FolderBuildPath getLibBuildPath() {
        return new FolderBuildPath(project.getFolderLocation()) {

            @Override
            public String getInputLocation() {
                return new File(project.getFolderLocation(), "Libs").getAbsolutePath();
            }

            @Override
            public File[] getBuildableFiles() {
                List<File> builtFiles = new ArrayList<File>();
                for (File f : new File(getInputLocation()).listFiles()) {
                    if (!f.getName().startsWith("Temp")) {
                        builtFiles.add(f);
                    }
                }

                return builtFiles.toArray(new File[builtFiles.size()]);
            }
        };
    }

    public FolderBuildPath getKeywordBuildPath() {
        return new FolderBuildPath(project.getFolderLocation()) {

            @Override
            public String getInputLocation() {
                return new File(project.getFolderLocation(), "Keywords").getAbsolutePath();
            }

            @Override
            public File[] getBuildableFiles() {
                return FileUtils.listFiles(new File(getInputLocation()), new String[] { "groovy" }, false)
                        .toArray(new File[0]);
            }
        };
    }

    public List<BundleBuildPath> getBundleBuildpaths() {
        List<BundleBuildPath> bundlePaths = new ArrayList<BundleBuildPath>();

        bundlePaths.add(new BundleBuildPath(Platform.getBundle(IdConstants.KATALON_CORE_BUNDLE_ID)));

        for (IKeywordContributor contributor : KeywordContributorCollection.getKeywordContributors()) {
            bundlePaths.add(new BundleBuildPath(FrameworkUtil.getBundle(contributor.getClass())));
        }
        bundlePaths.add(new BundleBuildPath(FrameworkUtil.getBundle(AppiumDriverManager.class)));
        return bundlePaths;
    }
    
    private List<IBuildPath> getExternalBuildPaths() throws IOException {
        File externalDriversFolder = new File(project.getFolderLocation(), EXTERNAL_DRIVERS_FOLDER);

        if (!externalDriversFolder.exists()) {
            return Collections.emptyList();
        }

        List<IBuildPath> externalLibBuildPaths = new ArrayList<IBuildPath>();
        for (File jarFile : externalDriversFolder.listFiles()) {
            externalLibBuildPaths.add(new BuildPathEntry(jarFile.getAbsolutePath()));
        }
        return externalLibBuildPaths;
    }
    
    private List<String> getExternalBuildPathLoc() throws IOException {
        List<String> bundleBpLocs = new ArrayList<String>();
        for (IBuildPath buildPath : getExternalBuildPaths()) {
            bundleBpLocs.add(buildPath.getBuildPathLocation());
        }
        return bundleBpLocs;
    }
    
    public List<String> getClassPaths() throws IOException {
        List<String> classPaths = new ArrayList<String>();
        classPaths.addAll(getBundleBuildPathLoc());
        classPaths.addAll(getExternalBuildPathLoc());
        return classPaths;
    }

    private List<String> getBundleBuildPathLoc() throws IOException {
        List<String> bundleBpLocs = new ArrayList<String>();

        for (BundleBuildPath bd : getBundleBuildpaths()) {
            bundleBpLocs.add(bd.getBuildPathLocation());

            for (IBuildPath requiredBp : bd.getChildBuildPaths()) {
                String bpLoc = requiredBp.getBuildPathLocation();

                if (!bundleBpLocs.contains(bpLoc)) {
                    bundleBpLocs.add(bpLoc);
                }
            }
        }

        return bundleBpLocs;
    }
}
