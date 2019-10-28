package com.kms.katalon.composer.testsuite.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.testsuite.constants.ComposerTestsuiteMessageConstants;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.link.TestCaseTestDataLink;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class TestDataTableDropListener extends TableDropTargetEffect {
    private TableViewer tableViewer;

    private TestSuitePartDataBindingView part;

    public TestDataTableDropListener(TableViewer tableViewer, TestSuitePartDataBindingView view) {
        super(tableViewer.getTable());
        this.tableViewer = tableViewer;
        this.part = view;
    }

    @Override
    public void drop(DropTargetEvent event) {
        event.detail = DND.DROP_COPY;
        boolean isEnterpriseAccount = LicenseUtil.isNotFreeLicense();
        if (part != null && part.getSelectedTestCaseLink() != null
                && part.getSelectedTestCaseLink().getTestDataLinks() != null) {
            List<TestCaseTestDataLink> inputs = part.getSelectedTestCaseLink().getTestDataLinks();
            Point pt = Display.getCurrent().map(null, tableViewer.getTable(), event.x, event.y);
            TableItem tableItem = tableViewer.getTable().getItem(pt);
            TestCaseTestDataLink destItem = (tableItem != null && tableItem.getData() instanceof TestCaseTestDataLink) ? (TestCaseTestDataLink) tableItem
                    .getData() : null;
            // int destIndex = (destItem != null) ? inputs.indexOf(destItem) : inputs.size() - 1;
            int destIndex = (destItem != null) ? inputs.indexOf(destItem) : inputs.size();
            if (event.data instanceof String) {
                List<TestCaseTestDataLink> movedItems = new ArrayList<TestCaseTestDataLink>();
                List<String> testDataIds = Arrays.asList(String.valueOf(event.data).split("\n"));
                
                if (!isEnterpriseAccount && (testDataIds.size() + inputs.size()) > 1) {
                    MessageDialog.openWarning(tableViewer.getTable().getShell(), GlobalStringConstants.INFO,
                            ComposerTestsuiteMessageConstants.DIA_INFO_KSE_COMBINE_MULTI_DATASOURCE);
                    return;
                }
                
                for (TestCaseTestDataLink link : inputs) {
                    if (testDataIds.contains(link.getTestDataId())) {
                        movedItems.add(link);
                    }
                }
                if (movedItems.size() > 0) {
                    inputs.removeAll(movedItems);
                    for (int i = 0; i < movedItems.size(); i++) {
                        inputs.add(destIndex + i, movedItems.get(i));
                    }
                }
                tableViewer.refresh();
                part.refreshVariableTable();
                tableViewer.setSelection(new StructuredSelection(movedItems.get(0)));
                part.setDirty(true);
            } else if (event.data instanceof ITreeEntity[]) {
                try {
                    List<TestCaseTestDataLink> addedTestDataLinkTreeNodes = new ArrayList<TestCaseTestDataLink>();
                    for (ITreeEntity iTreeEntity : (ITreeEntity[]) event.data) {
                        if (iTreeEntity instanceof TestDataTreeEntity) {
                            DataFileEntity testData = (DataFileEntity) ((TestDataTreeEntity) iTreeEntity).getObject();
                            TestCaseTestDataLink testDataLink = new TestCaseTestDataLink();
                            testDataLink.setTestDataId(testData.getIdForDisplay());
                            testDataLink.getId();
                            addedTestDataLinkTreeNodes.add(testDataLink);
                        } else if (iTreeEntity instanceof FolderTreeEntity
                                && ((FolderEntity) ((FolderTreeEntity) iTreeEntity).getObject()).getFolderType() == FolderType.DATAFILE) {
                            collectTestCaseTestDataLinksRecursively(iTreeEntity, addedTestDataLinkTreeNodes);
                        }
                    }

                    if (!isEnterpriseAccount && (addedTestDataLinkTreeNodes.size() + inputs.size()) > 1) {
                        MessageDialog.openWarning(tableViewer.getTable().getShell(), GlobalStringConstants.INFO,
                                ComposerTestsuiteMessageConstants.DIA_INFO_KSE_COMBINE_MULTI_DATASOURCE);
                        return;
                    }
                    
                    for (int i = 0; i < addedTestDataLinkTreeNodes.size(); i++) {
                        inputs.add(destIndex + i, addedTestDataLinkTreeNodes.get(i));
                    }
                    
                    if (addedTestDataLinkTreeNodes.size() > 0) {
                        tableViewer.refresh();
                        tableViewer.setSelection(new StructuredSelection(addedTestDataLinkTreeNodes));
                        part.refreshVariableTable();
                        part.setDirty(true);
                    }
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        }
    }
    
    private List<TestCaseTestDataLink> getTableItems() {
        return part.getSelectedTestCaseLink().getTestDataLinks();
    }

    private void collectTestCaseTestDataLinksRecursively(ITreeEntity iTreeEntity, List<TestCaseTestDataLink> list)
            throws Exception {
        if (iTreeEntity instanceof TestDataTreeEntity) {
            DataFileEntity testData = (DataFileEntity) ((TestDataTreeEntity) iTreeEntity).getObject();
            TestCaseTestDataLink testDataLink = new TestCaseTestDataLink();
            testDataLink.setTestDataId(testData.getIdForDisplay());
            testDataLink.getId();
            list.add(testDataLink);
        } else if (iTreeEntity instanceof FolderTreeEntity
                && ((FolderEntity) ((FolderTreeEntity) iTreeEntity).getObject()).getFolderType() == FolderType.DATAFILE) {
            for (Object obj : ((FolderTreeEntity) iTreeEntity).getChildren()) {
                collectTestCaseTestDataLinksRecursively((ITreeEntity) obj, list);
            }
        }
    }
}
