package com.kms.katalon.composer.testsuite.providers;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.controller.FilterController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseTableViewerFilter extends ViewerFilter {

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element == null || !(element instanceof TestSuiteTestCaseLink)) return false;
        TestSuiteTestCaseLink testCaseLink = (TestSuiteTestCaseLink) element;
        TestCaseTableViewer tableViewer = (TestCaseTableViewer) viewer;
        return filter(tableViewer.getSearchedString(), testCaseLink);
    }

    public boolean filter(String searchedString, TestSuiteTestCaseLink testCaseLink) {
        if (searchedString == null || searchedString.isEmpty()) return true;
        String contentString = searchedString.trim().toLowerCase();

        String testCaseId = testCaseLink.getTestCaseId();
        if (testCaseId.contains(searchedString)) return true;
        try {
            TestCaseEntity testCaseEntity = TestCaseController.getInstance().getTestCaseByDisplayId(testCaseId);
            if (testCaseEntity == null) return false;

            Map<String, String> tagMap = EntityViewerFilter.parseSearchedString(getSearchTags(),
                    contentString);
            if (tagMap != null && !tagMap.isEmpty()) {
                return FilterController.getInstance().isMatched(testCaseEntity, contentString);
            }
           
            for (String tag : TestCaseTreeEntity.SEARCH_TAGS) {
                String entityValue = getEntityValueBySearchTag(tag, testCaseEntity).toLowerCase();
                if (entityValue != null && entityValue.toLowerCase().contains(contentString)) return true;
            }
           

        } catch (Exception e) {
            return false;
        }

        return false;
    }
    
    private String[] getSearchTags() {
        List<String> searchTags = FilterController.getInstance().getDefaultKeywords();
        return searchTags.toArray(new String[searchTags.size()]);
    }

    private String getEntityValueBySearchTag(String searchTag, TestCaseEntity testCase) throws Exception {
        switch (searchTag) {
            case "id":
                return testCase.getIdForDisplay();
            case "name":
                return testCase.getName();
            case "description":
                return testCase.getDescription();
            case "tag":
                return testCase.getTag();
            default:
                break;
        }
        return "";
    }

}
