package com.kms.katalon.execution.classpath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.ManifestElement;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.core.keyword.internal.IKeywordContributor;
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection;

public class BundleBuildPath implements IBuildPath {

    private List<IBuildPath> buildPaths;
    private Bundle bundle;

    public BundleBuildPath(Bundle bundle) {
        setBundle(bundle);
    }

    @Override
    public String getBuildPathLocation() throws IOException {
        File bundleFile = new File(ClassPathResolver.getBundleLocation(bundle));
        if (bundleFile.isFile()) {
            return bundleFile.getAbsolutePath();
        } else {
            return new File(bundleFile, ProjectBuildPath.DF_OUT_PUT_LOC).getAbsolutePath();
        }
    }

    public Bundle getBundle() {
        return bundle;
    }

    private void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public static boolean isCoreBundle(Bundle bundle) {
        for (IKeywordContributor contributor : KeywordContributorCollection.getKeywordContributors()) {
            Bundle coreBundle = FrameworkUtil.getBundle(contributor.getKeywordClass());
            if (bundle.getBundleId() == coreBundle.getBundleId()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidEntry(Bundle bundle) throws IOException {
        File bundleFile = new File(ClassPathResolver.getBundleLocation(bundle));

        String bundleName = bundleFile.isDirectory() ? bundleFile.getName() : FilenameUtils.getBaseName(bundleFile
                .getName());
        if (bundleName.contains("_")) {
            bundleName = bundleName.split("_")[0];
            if (StringUtils.isBlank(bundleName)) {
                return false;
            }
        }

        if (bundleName.startsWith("org.eclipse.core") || bundleName.startsWith("com.kms.katalon.core")
                || "org.codehaus.groovy".equalsIgnoreCase(bundleName)) {
            return false;
        }

        return !isCoreBundle(bundle);
    }

    public List<IBuildPath> getChildBuildPaths() throws IOException {
        if (buildPaths == null) {
            buildPaths = new ArrayList<IBuildPath>();

            buildPaths.addAll(getRequiredBuildPaths());
            buildPaths.addAll(getResourceBuildPaths());
        }
        return buildPaths;
    }

    private List<IBuildPath> getResourceBuildPaths() throws IOException {
        try {
            File bundleFile = new File(ClassPathResolver.getBundleLocation(bundle));
            if (bundleFile.isFile()) {
                return Collections.emptyList();
            }

            List<IBuildPath> resourceBuildPaths = new ArrayList<IBuildPath>();

            ManifestElement[] elements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, bundle.getHeaders()
                    .get(Constants.BUNDLE_CLASSPATH));
            if (elements == null) {
                return Collections.emptyList();
            }

            for (ManifestElement manifestElement : elements) {
                for (String lib : manifestElement.getValueComponents()) {
                    if (".".equals(manifestElement.getValue())) {
                        continue;
                    }

                    File libFile = new File(bundleFile, lib);
                    if (libFile.exists()) {
                        resourceBuildPaths.add(new BuildPathEntry(libFile.getAbsolutePath()));
                    }
                }

            }
            return resourceBuildPaths;
        } catch (BundleException ex) {
            return Collections.emptyList();
        }
    }

    private List<IBuildPath> getRequiredBuildPaths() throws IOException {
        try {
            List<IBuildPath> requiredBuildPaths = new ArrayList<IBuildPath>();

            ManifestElement[] elements = ManifestElement.parseHeader(Constants.BUNDLE_CLASSPATH, bundle.getHeaders()
                    .get(Constants.REQUIRE_BUNDLE));

            if (elements == null) {
                return Collections.emptyList();
            }

            for (ManifestElement manifestElement : elements) {
                Bundle requiredBundle = Platform.getBundle(manifestElement.getValue());

                if (isValidEntry(requiredBundle)) {
                    File bundleFile = new File(ClassPathResolver.getBundleLocation(requiredBundle));
                    if (bundleFile.isDirectory()) {
                        BundleBuildPath bundleBuildPath = new BundleBuildPath(requiredBundle);
                        requiredBuildPaths.add(bundleBuildPath);
                        requiredBuildPaths.addAll(bundleBuildPath.getChildBuildPaths());
                    } else {
                        requiredBuildPaths.add(new BuildPathEntry(ClassPathResolver.getBundleLocation(requiredBundle)));
                    }
                }
            }

            return requiredBuildPaths;
        } catch (BundleException ex) {
            return Collections.emptyList();
        }
    }
}
