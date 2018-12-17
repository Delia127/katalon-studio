package com.kms.katalon.composer.search.view.provider;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.search.internal.ui.text.DecoratingFileSearchLabelProvider;
import org.eclipse.search.internal.ui.text.FileLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.search.constants.ImageConstants;
import com.kms.katalon.composer.search.constants.StringConstants;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;

import static com.kms.katalon.constants.GlobalStringConstants.DF_CHARSET;

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
                String elementName = ((IFolder) element).getName();
                if (StringConstants.ROOT_FOLDER_NAME_TEST_CASE.equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_TEST_CASE;
                }

                if (StringConstants.ROOT_FOLDER_NAME_TEST_SUITE.equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_TEST_SUITE;
                }

                if (StringConstants.ROOT_FOLDER_NAME_OBJECT_REPOSITORY.equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_OBJECT;
                }

                if (StringConstants.ROOT_FOLDER_NAME_DATA_FILE.equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_DATA;
                }

                if (StringConstants.ROOT_FOLDER_NAME_KEYWORD.equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_KEYWORD;
                }

                if (StringConstants.ROOT_FOLDER_NAME_REPORT.equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_REPORT;
                }

                if (StringConstants.ROOT_FOLDER_NAME_CHECKPOINT.equals(elementName)) {
                    return ImageConstants.IMG_16_FOLDER_CHECKPOINT;
                }

                return ImageConstants.IMG_16_FOLDER;
            } else if (element instanceof IFile) {
                IFile file = (IFile) element;
                String fileExtension = "." + file.getFileExtension();

                if (fileExtension.equals(TestCaseEntity.getTestCaseFileExtension())) {
                    return ImageConstants.IMG_16_TEST_CASE;
                }

                if (fileExtension.equals(WebElementEntity.getWebElementFileExtension())) {
                    if (IOUtils.toString(file.getContents(), 
                                    DF_CHARSET).contains(WebServiceRequestEntity.class.getSimpleName())) {
                        return ImageConstants.IMG_16_TEST_OBJECT_WS;
                    }
                    return ImageConstants.IMG_16_TEST_OBJECT;
                }

                if (fileExtension.equals(TestSuiteEntity.getTestSuiteFileExtension())) {
                    if (IOUtils.toString(file.getContents(), DF_CHARSET)
                            .contains(TestSuiteCollectionEntity.class.getSimpleName())) {
                        return ImageConstants.IMG_16_TEST_SUITE_COLLECTION;
                    }
                    return ImageConstants.IMG_16_TEST_SUITE;
                }

                if (fileExtension.equals(DataFileEntity.getTestDataFileExtension())) {
                    return ImageConstants.IMG_16_TEST_DATA;
                }

                if (fileExtension.equals(CheckpointEntity.getCheckpointFileExtension())) {
                    return ImageConstants.IMG_16_CHECKPOINT;
                }

                if (fileExtension.equals(GroovyConstants.GROOVY_FILE_EXTENSION)) {
                    if (isTestCaseScript(FilenameUtils.getBaseName(file.getName()))) {
                        return ImageConstants.IMG_16_TEST_CASE;
                    }
                    return ImageConstants.IMG_16_KEYWORD;
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
