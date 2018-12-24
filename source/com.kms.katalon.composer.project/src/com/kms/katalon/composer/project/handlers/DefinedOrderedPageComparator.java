package com.kms.katalon.composer.project.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.kms.katalon.composer.project.constants.StringConstants;

public class DefinedOrderedPageComparator extends ViewerComparator {
    
    List<String> predefinedOrder;
    
    public DefinedOrderedPageComparator() {
        predefinedOrder = new ArrayList<>();
        predefinedOrder.add(StringConstants.PROJECT_INFORMATION_SETTINGS_PAGE_ID);
        
        predefinedOrder.add(StringConstants.TEST_DESIGN_SETTINGS_PAGE_ID);
        predefinedOrder.add(StringConstants.WEB_LOCATORS_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.TEST_CASE_SETTING_PAGE_ID);
        
        predefinedOrder.add(StringConstants.EXTERNAL_LIBRARIES_SETTING_PAGE_ID);
        
        predefinedOrder.add(StringConstants.EMAIL_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.TEMPLATE_SETTING_PAGE_ID);
        
        predefinedOrder.add(StringConstants.PROJECT_EXECUTION_SETTINGS_PAGE_ID);
        predefinedOrder.add(StringConstants.PROJECT_EXECUTION_SETTINGS_DEFAULT_PAGE_ID);
            
        predefinedOrder.add(StringConstants.INTEGRATION_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.KATALON_ANALYTICS_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.JIRA_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.QTEST_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.TEST_CASE_REPOSITORIES_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.TEST_SUITE_REPOSITORIES_SETTING_PAGE_ID);
                
        predefinedOrder.add(StringConstants.REPORT_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.DATABASE_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.NETWORK_SETTING_PAGE_ID);
        
        predefinedOrder.add(StringConstants.DESIRED_CAPABILITIES_SETTING_PAGE_ID); 
        predefinedOrder.add(StringConstants.CUSTOM_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.WEB_UI_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.CHROME_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.FIREFOX_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.IE_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.SAFARI_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.EDGE_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.REMOTE_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.CHROME_HEADLESS_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.FIREFOX_HEADLESS_SETTING_PAGE_ID);
        
        predefinedOrder.add(StringConstants.PROJECT_EXECUTION_SETTINGS_DEFAULT_MOBILE_PAGE_ID);
        predefinedOrder.add(StringConstants.ANDROID_SETTING_PAGE_ID);
        predefinedOrder.add(StringConstants.PROJECT_EXECUTION_SETTINGS_DEFAULT_MOBILE_IOS_PAGE_ID);
    }
    
    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof PreferenceNode && e2 instanceof PreferenceNode) {
            return predefinedOrder.indexOf(((PreferenceNode) e1).getId()) - predefinedOrder.indexOf(((PreferenceNode) e2).getId()); 
        }
        return super.compare(viewer, e1, e2);
    }

}

