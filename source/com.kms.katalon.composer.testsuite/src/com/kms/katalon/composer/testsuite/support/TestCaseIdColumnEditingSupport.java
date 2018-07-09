package com.kms.katalon.composer.testsuite.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testsuite.editors.TestCaseCellEditor;
import com.kms.katalon.composer.testsuite.parts.TestSuitePartTestCaseView;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCaseIdColumnEditingSupport extends EditingSupport {
    private TestCaseTableViewer viewer;

    private TestSuitePartTestCaseView eventBroker;

    private static final String FIELD_NAME = "testCaseId";

    public TestCaseIdColumnEditingSupport(ColumnViewer viewer, TestSuitePartTestCaseView eventBroker) {
        super(viewer);
        this.viewer = (TestCaseTableViewer) viewer;
        this.eventBroker = eventBroker;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        if (element != null || element instanceof TestSuiteTestCaseLink) {
            TestSuiteTestCaseLink link = (TestSuiteTestCaseLink) element;
            return new TestCaseCellEditor(viewer.getTable(), link.getTestCaseId(), link.getTestCaseId());
        } else {
            return null;
        }
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof TestSuiteTestCaseLink) {
            return ((TestSuiteTestCaseLink) element).getTestCaseId();
        }
        return null;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof TestSuiteTestCaseLink && value != null
                && value instanceof TestCaseTreeEntity) {
            try {
                TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) element;
                TestCaseEntity testCaseEntity = (TestCaseEntity) ((TestCaseTreeEntity) value).getObject();

                if (testCaseEntity == null) {
                    return;
                }
                String testCaseId = testCaseEntity.getIdForDisplay();
                String oldTestCaseId = testCaseLink.getTestCaseId();

                if (testCaseId.equals(oldTestCaseId)) {
                    return;
                }

                if (viewer.containTestCasePk(testCaseEntity.getId())) {
                    MessageDialog.openWarning(null, "Warning",
                            "Test case '" + testCaseId + "' has already existed in this test suite.");
                    return;
                }

                ((TestSuiteTestCaseLink) element).setTestCaseId(testCaseId);

                boolean saveData = testCaseLink.getTestDataLinks().size() > 0 && MessageDialog.openQuestion(null,
                        "Confirmation", "Do you want to keep the current test data information?");
                if (!saveData) {
                    clearData(testCaseLink, testCaseEntity);
                } else {
                    saveData(testCaseLink, testCaseEntity, oldTestCaseId);
                }

                viewer.update(element, new String[] { FIELD_NAME });
                viewer.updatePk((TestSuiteTestCaseLink) element);

                eventBroker.setDirty(true);
                viewer.setSelection(new StructuredSelection(element));

            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }

        }
    }

    private void saveData(TestSuiteTestCaseLink testCaseLink, TestCaseEntity testCaseEntity, String oldTestCaseId) {
        if (isVariableChanges(testCaseLink, testCaseEntity)) {
            final List<VariableLink> variableLinks = testCaseLink.getVariableLinks();
            final List<VariableLink> existingVariables = new ArrayList<>(variableLinks);
            variableLinks.clear();
            String testCaseId = testCaseLink.getTestCaseId();
            for (VariableEntity variable : testCaseEntity.getVariables()) {
                VariableLink variableLink = new VariableLink();
                variableLink.setVariableId(variable.getId());
                String newVariableName = getVariableName(testCaseId, variableLink);
                for (VariableLink exisitingVariable : existingVariables) {
                    if (StringUtils.equals(newVariableName, getVariableName(oldTestCaseId, exisitingVariable))) {
                        variableLink.setTestDataLinkId(exisitingVariable.getTestDataLinkId());
                        variableLink.setType(exisitingVariable.getType());
                        variableLink.setValue(exisitingVariable.getValue());
                        break;
                    }
                }
                variableLinks.add(variableLink);
            }
        }
    }

    private void clearData(TestSuiteTestCaseLink testCaseLink, TestCaseEntity testCaseEntity) {
        testCaseLink.getTestDataLinks().clear();
        testCaseLink.getVariableLinks().clear();
        if (isVariableChanges(testCaseLink, testCaseEntity)) {
            for (VariableEntity variable : testCaseEntity.getVariables()) {
                VariableLink variableLink = new VariableLink();
                variableLink.setVariableId(variable.getId());
                testCaseLink.getVariableLinks().add(variableLink);
            }
        }
    }

    private String getVariableName(String testCaseId, VariableLink element) {
        try {
            VariableEntity variable = TestSuiteController.getInstance().getVariable(testCaseId, element);
            return variable != null ? variable.getName() : StringUtils.EMPTY;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return StringUtils.EMPTY;
        }
    }

    private boolean isVariableChanges(TestSuiteTestCaseLink testCaseLink, TestCaseEntity testCaseEntity) {
        if (testCaseLink.getVariableLinks().size() != testCaseEntity.getVariables().size())
            return true;
        for (int index = 0; index < testCaseLink.getVariableLinks().size(); index++) {
            VariableEntity testCaseVariable = testCaseEntity.getVariables().get(index);
            if (!testCaseVariable.getId().equals(testCaseLink.getVariableLinks().get(index).getVariableId())) {
                return true;
            }
        }
        return false;
    }
}
