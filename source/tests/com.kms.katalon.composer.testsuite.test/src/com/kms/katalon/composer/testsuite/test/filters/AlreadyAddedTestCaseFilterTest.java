package com.kms.katalon.composer.testsuite.test.filters;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.junit.Assert;
import org.junit.Test;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testsuite.filters.AlreadyAddedTestCaseFilter;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.classpath.ClassPathResolver;

public class AlreadyAddedTestCaseFilterTest {
    private static String FIXTURE = "{Test Cases/Folder 1/Folder 2/Folder 3=true, Test Cases/Folder 1/Test Case=false, Test Cases/Folder 1/Folder 2/Folder 3/Added=true, Test Cases/Folder 1/Folder 2=false, Test Cases/Folder 1/Folder 2/Test Case=false, Test Cases/Folder 1=false, Test Cases/Added=true}";

    @Test
    public void testAllEntitiesAreFiltered() throws Exception {
        File testProjectFile = getExtensionsDirectory(
                "resources/already_added_test_cases_filter_project/Test Filtering Already Added Test Cases Should Work.prj");
        ProjectEntity testProject = ProjectController.getInstance().openProject(testProjectFile.getAbsolutePath(),
                false);
        ITreeEntity[] entities = TreeEntityUtil.getChildren(null,
                FolderController.getInstance().getTestCaseRoot(testProject));
        AlreadyAddedTestCaseFilter filter = new AlreadyAddedTestCaseFilter();

        filter.filterElements(entities, new NullProgressMonitor());

        Assert.assertEquals(filter.getResultSize(), 7);
    }

    @Test
    public void testEntitiesAreFilteredCorrectly() throws Exception {
        File testProjectFile = getExtensionsDirectory(
                "resources/already_added_test_cases_filter_project/Test Filtering Already Added Test Cases Should Work.prj");
        ProjectEntity testProject = ProjectController.getInstance().openProject(testProjectFile.getAbsolutePath(),
                false);
        ITreeEntity[] entities = TreeEntityUtil.getChildren(null,
                FolderController.getInstance().getTestCaseRoot(testProject));
        AlreadyAddedTestCaseFilter filter = new AlreadyAddedTestCaseFilter();
        filter.filterElements(entities, new NullProgressMonitor());
        Assert.assertEquals(FIXTURE, filter.getResult().toString());
    }

    private File getExtensionsDirectory(String path) throws IOException {
        File bundleFile = FileLocator.getBundleFile(Platform.getBundle("com.kms.katalon.composer.testsuite.test"));
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + path);
        } else { // run as product
            return new File(ClassPathResolver.getConfigurationFolder(), path);
        }
    }
}
