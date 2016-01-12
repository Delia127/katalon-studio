package com.kms.katalon.composer.testsuite.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testsuite.parts.TestSuitePartTestCaseView;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCaseTableViewer extends TableViewer {

    private List<String> testCasesPKs;

    private List<TestSuiteTestCaseLink> data;

    private boolean isRunAll;

    private TestSuitePartTestCaseView parentView;

    private String searchedString;

    public TestCaseTableViewer(Composite parent, int style, TestSuitePartTestCaseView parentView) {
        super(parent, style);
        testCasesPKs = new ArrayList<String>();
        data = new ArrayList<TestSuiteTestCaseLink>();
        this.parentView = parentView;
        searchedString = StringUtils.EMPTY;
    }

    public void setInput(List<TestSuiteTestCaseLink> data) throws Exception {
        this.data.clear();

        isRunAll = true;
        testCasesPKs.clear();
        for (Object object : data) {
            TestSuiteTestCaseLink link = (TestSuiteTestCaseLink) object;
            TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(link.getTestCaseId());
            if (testCase != null && testCase.getId() != null) {
                testCasesPKs.add(testCase.getId());
            } else {
                testCasesPKs.add(link.getTestCaseId());
            }

            this.data.add(link);

            if (!link.getIsRun()) isRunAll = false;
        }
        super.setInput(this.data);
        // update image header of isRun Column
        parentView.updateIsRunColumnHeader();
    }

    public List<TestSuiteTestCaseLink> getInput() {
        return data;
    }

    public void addTestCase(TestCaseEntity testCase) throws Exception {
        // check testCase is in list or not
        if (testCasesPKs.contains(testCase.getId())) return;

        TestSuiteTestCaseLink link = createNewTestSuiteTestCaseLink(testCase);

        testCasesPKs.add(testCase.getId());
        data.add(link);

        this.refresh();
        this.setSelection(new StructuredSelection(link));
        parentView.setDirty(true);
    }

    public TestSuiteTestCaseLink insertTestCase(TestCaseEntity testCase, int index) throws Exception {
        if (index < 0 || index > data.size()) {
            addTestCase(testCase);
            return null;
        }
        // check testCase is in list or not
        if (testCasesPKs.contains(testCase.getId())) {
            return null;
        }

        TestSuiteTestCaseLink link = createNewTestSuiteTestCaseLink(testCase);

        testCasesPKs.add(testCase.getId());
        data.add(index, link);

        this.refresh();
        this.setSelection(new StructuredSelection(link));
        parentView.setDirty(true);
        return link;
    }

    protected TestSuiteTestCaseLink createNewTestSuiteTestCaseLink(TestCaseEntity testCase) throws Exception {
        TestSuiteTestCaseLink link = new TestSuiteTestCaseLink();
        link.setIsRun(true);
        link.setTestCaseId(testCase.getIdForDisplay());

        for (VariableEntity variable : testCase.getVariables()) {
            VariableLink variableLink = new VariableLink();
            variableLink.setVariableId(variable.getId());
            link.getVariableLinks().add(variableLink);
        }
        return link;
    }

    public void upTestCase(List<TestSuiteTestCaseLink> selectedObjects) {
        if (selectedObjects != null && selectedObjects.size() >= 1) {

            Collections.sort(selectedObjects, new Comparator<TestSuiteTestCaseLink>() {

                @Override
                public int compare(TestSuiteTestCaseLink arg0, TestSuiteTestCaseLink arg1) {
                    return data.indexOf(arg0) > data.indexOf(arg1) ? 1 : -1;
                }

            });

            for (TestSuiteTestCaseLink selectedLink : selectedObjects) {

                int selectedIndex = data.indexOf(selectedLink) - 1;
                if (selectedIndex >= 0) {
                    TestSuiteTestCaseLink linkBefore = (TestSuiteTestCaseLink) data.get(selectedIndex);

                    // Avoid swap 2 objects that are both selected
                    if (selectedObjects.contains(linkBefore)) {
                        continue;
                    }

                    data.remove(selectedLink);
                    data.add(selectedIndex, selectedLink);

                    this.update(selectedLink, null);
                    this.update(linkBefore, null);

                    this.refresh();
                    this.getTable().select(selectedIndex);
                    parentView.setDirty(true);
                }
            }
        }
    }

    public void downTestCase(List<TestSuiteTestCaseLink> selectedObjects) {
        if (selectedObjects != null && selectedObjects.size() >= 1) {
            Collections.sort(selectedObjects, new Comparator<TestSuiteTestCaseLink>() {

                @Override
                public int compare(TestSuiteTestCaseLink arg0, TestSuiteTestCaseLink arg1) {
                    return data.indexOf(arg0) < data.indexOf(arg1) ? 1 : -1;
                }

            });

            for (TestSuiteTestCaseLink selectedLink : selectedObjects) {
                int selectedIndex = data.indexOf(selectedLink) + 1;
                if (selectedIndex < data.size()) {
                    TestSuiteTestCaseLink linkAfter = (TestSuiteTestCaseLink) data.get(selectedIndex);

                    // Avoid swap 2 objects that are both selected
                    if (selectedObjects.contains(linkAfter)) {
                        continue;
                    }

                    data.remove(selectedLink);
                    data.add(selectedIndex, selectedLink);

                    this.update(selectedLink, null);
                    this.update(linkAfter, null);

                    this.refresh();
                    this.getTable().select(selectedIndex);
                    parentView.setDirty(true);
                }
            }
        }
    }

    public void removeTestCases(List<TestSuiteTestCaseLink> selectedObjects) throws Exception {
        if (selectedObjects != null && selectedObjects.size() > 0) {
            for (TestSuiteTestCaseLink link : selectedObjects) {
                TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(link.getTestCaseId());
                if (testCase != null) {
                    testCasesPKs.remove(testCase.getId());
                } else {
                    testCasesPKs.remove(link.getTestCaseId());
                }

                data.remove(data.indexOf(link));
            }
            refreshIsRunAll();

            this.refresh();
            parentView.setDirty(true);
        }
    }

    public int getIndex(TestSuiteTestCaseLink testSuiteTestCaseLink) {
        return data.indexOf(testSuiteTestCaseLink);
    }

    public void setIsRunValueAllTestCases() {
        isRunAll = !isRunAll;
        for (Object object : data) {
            TestSuiteTestCaseLink link = (TestSuiteTestCaseLink) object;
            link.setIsRun(isRunAll);
            this.update(link, null);
        }
        parentView.setDirty(true);
        parentView.updateIsRunColumnHeader();
    }

    public boolean getIsRunAll() {
        return isRunAll;
    }

    public void refreshIsRunAll() {
        boolean check = true;
        for (Object object : data) {
            TestSuiteTestCaseLink link = (TestSuiteTestCaseLink) object;
            if (!link.getIsRun()) check = false;
        }

        if (check != isRunAll) {
            isRunAll = check;
            parentView.updateIsRunColumnHeader();
        }
    }

    public void updateTestCaseProperties(String oldPk, TestCaseEntity testCase) throws Exception {
        if (testCasesPKs.contains(oldPk)) {
            int index = testCasesPKs.indexOf(oldPk);
            TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) data.get(index);
            testCaseLink.setTestCaseId(testCase.getIdForDisplay());
            List<VariableLink> retainedVariableLinks = new ArrayList<VariableLink>();
            for (VariableEntity variable : testCase.getVariables()) {
                boolean isNewVariable = true;

                for (VariableLink variableLink : testCaseLink.getVariableLinks()) {
                    if (variable.getId().equals(variableLink.getVariableId())) {
                        isNewVariable = false;
                        retainedVariableLinks.add(variableLink);
                        break;
                    }
                }

                if (isNewVariable) {
                    VariableLink newVariableLink = new VariableLink();
                    newVariableLink.setVariableId(variable.getId());
                    testCaseLink.getVariableLinks().add(newVariableLink);
                    retainedVariableLinks.add(newVariableLink);
                }
            }
            testCaseLink.getVariableLinks().retainAll(retainedVariableLinks);

            testCasesPKs.remove(index);
            testCasesPKs.add(index, testCase.getId());

            this.update(testCaseLink, null);
        }
    }

    public TestSuiteTestCaseLink getSelectedTestCaseLink() {
        IStructuredSelection selection = (IStructuredSelection) getSelection();
        if (selection != null && selection.size() == 1) {
            return (TestSuiteTestCaseLink) selection.getFirstElement();
        }
        return null;
    }

    public boolean containTestCasePk(String testCasePk) {
        return testCasesPKs.contains(testCasePk);
    }

    public void updatePk(TestSuiteTestCaseLink link) throws Exception {
        int index = data.indexOf(link);
        testCasesPKs.remove(index);

        TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(link.getTestCaseId());
        if (testCasesPKs.size() <= index) {
            testCasesPKs.add(index, testCase.getId());
        } else {
            testCasesPKs.add(testCase.getId());
        }
    }

    public String getSearchedString() {
        return searchedString;
    }

    public void setSearchedString(String searchedString) {
        this.searchedString = searchedString;
    }

    public void updateDirty(boolean dirty) {
        parentView.setDirty(dirty);
    }

    public List<String> getTestCasesPKs() {
        return testCasesPKs;
    }
}
