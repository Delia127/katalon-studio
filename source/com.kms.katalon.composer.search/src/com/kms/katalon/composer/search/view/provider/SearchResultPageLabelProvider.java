package com.kms.katalon.composer.search.view.provider;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.search.internal.ui.text.DecoratingFileSearchLabelProvider;
import org.eclipse.search.internal.ui.text.FileLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.search.constants.ImageConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;

@SuppressWarnings({ "restriction" })
public class SearchResultPageLabelProvider extends DecoratingFileSearchLabelProvider {

    public SearchResultPageLabelProvider(FileLabelProvider provider) {
        super(provider);
    }

    @Override
    public Image getImage(Object element) {
        try {
            if (element instanceof IProject) {
                return null;
            } else if (element instanceof IFolder) {
                ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
                FolderEntity testCaseRoot = FolderController.getInstance().getTestCaseRoot(currentProject);
                FolderEntity testSuiteRoot = FolderController.getInstance().getTestSuiteRoot(currentProject);
                FolderEntity testDataRoot = FolderController.getInstance().getTestDataRoot(currentProject);
                FolderEntity testObjectRoot = FolderController.getInstance().getObjectRepositoryRoot(currentProject);
                FolderEntity keywordRoot = FolderController.getInstance().getKeywordRoot(currentProject);
                FolderEntity reportRoot = FolderController.getInstance().getReportRoot(currentProject);

                String elementName = ((IFolder) element).getName();

                if (testCaseRoot.getName().equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_TEST_CASE;
                } else if (testSuiteRoot.getName().equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_TEST_SUITE;
                } else if (testObjectRoot.getName().equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_OBJECT;
                } else if (testDataRoot.getName().equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_DATA;
                } else if (keywordRoot.getName().equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_KEYWORD;
                } else if (reportRoot.getName().equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_REPORT;
                }
                return ImageConstants.IMG_16_FOLDER;
            } else if (element instanceof IFile) {
                IFile file = (IFile) element;
                String fileExtension = "." + file.getFileExtension();

                if (fileExtension.equals(TestCaseEntity.getTestCaseFileExtension())) {
                    return ImageConstants.IMG_16_TEST_CASE;
                } else if (fileExtension.equals(WebElementEntity.getWebElementFileExtension())) {
                    return ImageConstants.IMG_16_TEST_OBJECT;
                } else if (fileExtension.equals(TestSuiteEntity.getTestSuiteFileExtension())) {
                    return ImageConstants.IMG_16_TEST_SUITE;
                } else if (fileExtension.equals(DataFileEntity.getTestDataFileExtension())) {
                    return ImageConstants.IMG_16_TEST_DATA;
                } else if (fileExtension.equals(GroovyConstants.GROOVY_FILE_EXTENSION)) {
                    String fileName = FilenameUtils.getBaseName(file.getName());
                    if (isTestCaseScript(fileName)) {
                        return ImageConstants.IMG_16_TEST_CASE;
                    } else {
                        return ImageConstants.IMG_16_KEYWORD;
                    }
                }

            }
            return super.getImage(element);
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean isTestCaseScript(String className) {
        return (className.matches("Script[0-9]{13}"));
    }

    @Override
    protected StyledString getStyledText(Object element) {
        StyledString styledString = super.getStyledText(element);
        if (element instanceof IProject) {
            IProject project = (IProject) element;
            return new StyledString(project.getRawLocation().toString());
        } else if (element instanceof IFile) {
            IFile file = (IFile) element;
            return new StyledString(FilenameUtils.getBaseName(file.getName()));
        }
        return styledString;
    }

}
