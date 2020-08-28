package com.kms.katalon.composer.testsuite.filters;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.testcase.TestCaseEntity;

/**
 * This class implements logic (and optional progress monitor) to filter test
 * cases and folders already added to Test Suites. A folder is considered "added
 * to Test Suites" if all of its test cases and folders are "added to Test
 * Suites"
 *
 */
public class AlreadyAddedTestCaseFilter {
    private Map<String, Boolean> alreadyAddedEntitiesCached;

    public AlreadyAddedTestCaseFilter() {
        alreadyAddedEntitiesCached = new HashMap<>();
    }

    public int getResultSize() {
        return alreadyAddedEntitiesCached.size();
    }

    public Map<String, Boolean> getResult() {
        return alreadyAddedEntitiesCached;
    }

    /**
     * Filter the given entities and store the filtered results. The results are
     * available via {@link AlreadyAddedTestCaseFilter#getResult()}
     * 
     * @param entities
     * @param monitor
     */
    public void filterElements(ITreeEntity[] entities, IProgressMonitor monitor) {
        for (ITreeEntity treeEntity : entities) {
            filterElement(treeEntity, monitor);
        }
    }

    /**
     * Recursively check if element is already added to a Test Suite and cache
     * the result.
     * 
     * @param element
     * @param monitor
     * @return
     */
    private boolean filterElement(Object element, IProgressMonitor monitor) {
        SubMonitor subMonitor = SubMonitor.convert(monitor);
        try {
            if (element instanceof FolderTreeEntity) {
                FolderEntity folderEntity = ((FolderTreeEntity) element).getObject();
                if (folderEntity.getFolderType() == FolderType.TESTCASE) {
                    subMonitor.beginTask("", folderEntity.getChildrenEntities().size());
                    boolean folderStatus = isFolderFullOfTestCasesAlreadyAddedToTestSuites(folderEntity, subMonitor);
                    alreadyAddedEntitiesCached.put(folderEntity.getIdForDisplay(), new Boolean(folderStatus));
                    return folderStatus;
                }
            }
            if (element instanceof TestCaseTreeEntity) {
                TestCaseEntity tcEntity = ((TestCaseTreeEntity) element).getObject();
                boolean testCaseStatus = isTestCaseAlreadyAddedInTestSuites(tcEntity);
                alreadyAddedEntitiesCached.put(tcEntity.getIdForDisplay(), new Boolean(testCaseStatus));
                return testCaseStatus;
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    /**
     * Return true if all nested Test Cases in this folder are already added to
     * Test Suites, return false otherwise. Note that this method also has a
     * side-effect of putting "Already added to Test Suites" status of test
     * cases and folders into cache
     * 
     * @param folderEntity
     * @param subMonitor
     * @return
     * @throws ControllerException
     */
    private boolean isFolderFullOfTestCasesAlreadyAddedToTestSuites(FolderEntity folderEntity, SubMonitor subMonitor)
            throws ControllerException {
        boolean isNotFull = false;
        FolderController folderController = FolderController.getInstance();
        for (Object childObject : folderController.getChildren(folderEntity)) {
            SubMonitor subSubMonitor = subMonitor.split(1, SubMonitor.SUPPRESS_NONE);
            subSubMonitor.beginTask("Check " + folderEntity.getIdForDisplay() + "...", 1);
            if (childObject instanceof TestCaseEntity) {
                boolean addedToTestSuites = isTestCaseAlreadyAddedInTestSuites((TestCaseEntity) childObject);
                if (!addedToTestSuites) {
                    isNotFull = true;
                }
                alreadyAddedEntitiesCached.put(((TestCaseEntity) childObject).getIdForDisplay(),
                        new Boolean(addedToTestSuites));
            } else if (childObject instanceof FolderEntity) {
                boolean addedToTestSuites = isFolderFullOfTestCasesAlreadyAddedToTestSuites((FolderEntity) childObject,
                        subMonitor);
                if (!addedToTestSuites) {
                    isNotFull = true;
                }
                alreadyAddedEntitiesCached.put(((FolderEntity) childObject).getIdForDisplay(),
                        new Boolean(addedToTestSuites));
            }
        }
        return !isNotFull;
    }

    /**
     * Return true if the given Test Case is added in any Test Suite
     * 
     * @param tcEntity
     * @return
     */
    private boolean isTestCaseAlreadyAddedInTestSuites(TestCaseEntity tcEntity) {
        return TestCaseController.getInstance().getTestCaseReferences(tcEntity).size() > 0;
    }

    /**
     * Checks if this entity is added to any Test Suites. This method
     * <b>must</b> be called after
     * {@link AlreadyAddedTestCaseFilter#filterElements(ITreeEntity[], IProgressMonitor)}
     * 
     * @param entityId
     * @return
     */
    public boolean isEntityAlreadyAddedToTestSuites(String entityId) {
        return alreadyAddedEntitiesCached.getOrDefault(entityId, false).booleanValue();
    }

    public boolean isResultNotEmpty() {
        return getResultSize() > 0;
    }
}
