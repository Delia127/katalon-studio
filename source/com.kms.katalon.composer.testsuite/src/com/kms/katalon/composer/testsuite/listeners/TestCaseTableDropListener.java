package com.kms.katalon.composer.testsuite.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.composer.testsuite.transfer.TestSuiteTestCaseLinkTransferData;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseTableDropListener extends TableDropTargetEffect {

    private TestCaseTableViewer viewer;

    public TestCaseTableDropListener(TestCaseTableViewer viewer) {
        super(viewer.getTable());
        this.viewer = viewer;
    }

    @Override
    public void drop(DropTargetEvent event) {
        try {
            event.detail = DND.DROP_COPY;
            Point pt = Display.getCurrent().map(null, viewer.getTable(), event.x, event.y);
            TableItem tableItem = viewer.getTable().getItem(pt);
            TestSuiteTestCaseLink selectedItem = (tableItem != null && tableItem.getData() instanceof TestSuiteTestCaseLink) ? (TestSuiteTestCaseLink) tableItem
                    .getData() : null;

            List<TestSuiteTestCaseLink> addedTestCaseLinked = new ArrayList<TestSuiteTestCaseLink>();
            if (event.data instanceof ITreeEntity[]) {
                int selectedIndex = (selectedItem != null) ? viewer.getIndex(selectedItem) : viewer.getInput().size();
                ITreeEntity[] treeEntities = (ITreeEntity[]) event.data;
                for (int i = treeEntities.length - 1; i >= 0; i--) {
                    if (treeEntities[i] instanceof TestCaseTreeEntity) {
                        TestSuiteTestCaseLink link = viewer.insertTestCase(
                                (TestCaseEntity) ((TestCaseTreeEntity) treeEntities[i]).getObject(), selectedIndex);
                        addedTestCaseLinked.add(link);
                    } else if (treeEntities[i] instanceof FolderTreeEntity) {
                        for (TestCaseEntity testCase : getTestCasesFromFolderTree((FolderTreeEntity) treeEntities[i])) {
                            addedTestCaseLinked.add(viewer.insertTestCase(testCase, selectedIndex));
                        }
                    }
                }
            } else if (event.data instanceof TestSuiteTestCaseLinkTransferData[]) {
                int selectedIndex = (selectedItem != null) ? viewer.getIndex(selectedItem)
                        : viewer.getInput().size() - 1;
                TestSuiteTestCaseLinkTransferData[] testSuiteTestCaseLinkTransferDatas = (TestSuiteTestCaseLinkTransferData[]) event.data;
                if (testSuiteTestCaseLinkTransferDatas.length != 1) {
                    return;
                }

                int previousIndex = viewer.getIndex(testSuiteTestCaseLinkTransferDatas[0].getTestSuiteTestCaseLink());
                if (previousIndex == selectedIndex) {
                    return;
                }
                Collections.swap(viewer.getInput(), previousIndex, selectedIndex);
                addedTestCaseLinked.add(testSuiteTestCaseLinkTransferDatas[0].getTestSuiteTestCaseLink());
                viewer.updateDirty(true);
            }

            // Force focus to the test suite part
            viewer.getTable().forceFocus();
            viewer.setSelection(new StructuredSelection(addedTestCaseLinked));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private List<TestCaseEntity> getTestCasesFromFolderTree(FolderTreeEntity folderTree) {
        List<TestCaseEntity> lstTestCases = new ArrayList<TestCaseEntity>();
        try {
            for (Object child : folderTree.getChildren()) {
                if (child instanceof TestCaseTreeEntity) {
                    lstTestCases.add((TestCaseEntity) ((TestCaseTreeEntity) child).getObject());
                } else if (child instanceof FolderTreeEntity) {
                    lstTestCases.addAll(getTestCasesFromFolderTree((FolderTreeEntity) child));
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return lstTestCases;
    }
}
