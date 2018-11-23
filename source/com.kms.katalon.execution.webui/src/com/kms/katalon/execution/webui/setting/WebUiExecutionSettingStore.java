package com.kms.katalon.execution.webui.setting;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.setting.BundleSettingStore;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.webui.constants.StringConstants;
import com.kms.katalon.execution.webui.constants.WebUiExecutionSettingConstants;
import com.kms.katalon.util.collections.Pair;

public class WebUiExecutionSettingStore extends BundleSettingStore {

    public static final boolean EXECUTION_DEFAULT_IGNORE_PAGELOAD_TIMEOUT_EXCEPTION = false;

    public static final boolean EXECUTION_DEFAULT_ENABLE_PAGE_LOAD_TIMEOUT = false;

    public static final int EXECUTION_DEFAULT_ACTION_DELAY = 0;

    public static final int EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT = 30;

    public static final int EXECUTION_DEFAULT_WAIT_FOR_IE_HANGING = 600;

    /**
     * In the format of pair value: <code>property-name-1,is-selected-1;property-name-2,is-selected-2;...</code>
     */
    public static final String DEFAULT_SELECTING_CAPTURED_OBJECT_PROPERTIES = "id,true;name,true;alt,true;checked,true;form,true;href,true;placeholder,true;selected,true;src,true;title,true;type,true;text,true;linked_text,true";
    
    public static final String DEFAULT_SELECTING_CAPTURED_OBJECT_XPATHS = "xpath:attributes,true;xpath:idRelative,true;dom:name,true;xpath:link,true;xpath:neighbor,true;xpath:href,true;xpath:img,true;xpath:position,true;";
    

    public static final String DEFAULT_SELECTING_CAPTURED_OBJECT_SELECTOR_METHOD = "BASIC";

    
    public static WebUiExecutionSettingStore getStore() {
        ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
        if (projectEntity == null) {
            return null;
        }
        return new WebUiExecutionSettingStore(projectEntity);
    }

    public WebUiExecutionSettingStore(ProjectEntity projectEntity) {
        super(projectEntity.getFolderLocation(),
                FrameworkUtil.getBundle(WebUiExecutionSettingStore.class).getSymbolicName(), false);
    }

    public boolean getEnablePageLoadTimeout() throws IOException {
        return getBoolean(WebUiExecutionSettingConstants.WEBUI_EXECUTION_ENABLE_PAGE_LOAD_TIMEOUT,
                EXECUTION_DEFAULT_ENABLE_PAGE_LOAD_TIMEOUT);
    }

    public void setEnablePageLoadTimeout(boolean pageLoadTimeoutEnabled) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_EXECUTION_ENABLE_PAGE_LOAD_TIMEOUT, pageLoadTimeoutEnabled);
    }

    public boolean getIgnorePageLoadTimeout() throws IOException {
        return getBoolean(WebUiExecutionSettingConstants.WEBUI_EXECUTION_IGNORE_PAGE_LOAD_TIMEOUT_EXCEPTION,
                EXECUTION_DEFAULT_IGNORE_PAGELOAD_TIMEOUT_EXCEPTION);
    }

    public void setIgnorePageLoadTimeout(boolean pageLoadTimeoutIgnored) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_EXECUTION_IGNORE_PAGE_LOAD_TIMEOUT_EXCEPTION,
                pageLoadTimeoutIgnored);
    }

    public int getPageLoadTimeout() throws IOException {
        return getInt(WebUiExecutionSettingConstants.WEBUI_EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT,
                EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT);
    }

    public void setPageLoadTimeout(int pageLoadTimeout) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT, pageLoadTimeout);
    }

    public int getActionDelay() throws IOException {
        return getInt(WebUiExecutionSettingConstants.WEBUI_EXECUTION_ACTION_DELAY, EXECUTION_DEFAULT_ACTION_DELAY);
    }

    public void setActionDelay(int actionDelay) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_EXECUTION_ACTION_DELAY, actionDelay);
    }

    public int getIEHangTimeout() throws IOException {
        return getInt(WebUiExecutionSettingConstants.WEBUI_EXECUTION_WAIT_FOR_IE_HANGING,
                EXECUTION_DEFAULT_WAIT_FOR_IE_HANGING);
    }

    public void setIEHangTimeout(int timeout) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_EXECUTION_WAIT_FOR_IE_HANGING, timeout);
    }

    public void setDefaultIEHangTimeout() throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_EXECUTION_WAIT_FOR_IE_HANGING,
                EXECUTION_DEFAULT_WAIT_FOR_IE_HANGING);
    }

    
    // TestObjectAttributeLocators
    public void setDefaultCapturedTestObjectAttributeLocators() throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_DEFAULT_SELECTING_CAPTURED_OBJECT_PROPERTIES,
                DEFAULT_SELECTING_CAPTURED_OBJECT_PROPERTIES);
    }

    public void setCapturedTestObjectAttributeLocators(List<Pair<String, Boolean>> locators) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_DEFAULT_SELECTING_CAPTURED_OBJECT_PROPERTIES,
                flattenStringBooleanList(locators));
    }

    public List<Pair<String, Boolean>> getCapturedTestObjectAttributeLocators() throws IOException {
        return parseStringBooleanString(
                getString(WebUiExecutionSettingConstants.WEBUI_DEFAULT_SELECTING_CAPTURED_OBJECT_PROPERTIES,
                        DEFAULT_SELECTING_CAPTURED_OBJECT_PROPERTIES));
    }
    
    // TestObjectXpathLocators - has a getDefault function for resetDefault button

    public List<Pair<String, Boolean>> getCapturedTestObjectXpathLocators() throws IOException {
        return parseStringBooleanString(
                getString(WebUiExecutionSettingConstants.WEBUI_DEFAULT_SELECTING_CAPTURED_OBJECT_XPATHS,
                        DEFAULT_SELECTING_CAPTURED_OBJECT_XPATHS));
    }
    
    public List<Pair<String,Boolean>> getDefaultCapturedObjectXpathLocators() throws IOException{
    	 return parseStringBooleanString(
                 getString(DEFAULT_SELECTING_CAPTURED_OBJECT_XPATHS, DEFAULT_SELECTING_CAPTURED_OBJECT_XPATHS));
    }
    
    public void setDefaultCapturedTestObjectXpathLocators() throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_DEFAULT_SELECTING_CAPTURED_OBJECT_XPATHS,
        		DEFAULT_SELECTING_CAPTURED_OBJECT_XPATHS);
    }

    public void setCapturedTestObjectXpathLocators(List<Pair<String, Boolean>> locators) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_DEFAULT_SELECTING_CAPTURED_OBJECT_XPATHS,
                flattenStringBooleanList(locators));
    }

    // TestObjectSelectorMethod
    
    public void setDefaultCapturedTestObjectSelectorMethods() throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_DEFAULT_SELECTING_CAPTURED_OBJECT_SELECTOR_METHOD,
        		DEFAULT_SELECTING_CAPTURED_OBJECT_SELECTOR_METHOD);
    }

    public void setCapturedTestObjectSelectorMethod(SelectorMethod selectorMethod) throws IOException {
        setProperty(WebUiExecutionSettingConstants.WEBUI_DEFAULT_SELECTING_CAPTURED_OBJECT_SELECTOR_METHOD,
        		selectorMethod.toString());
    }

    public SelectorMethod getCapturedTestObjectSelectorMethod() throws IOException {
        return parseSelectorMethodString(
                getString(WebUiExecutionSettingConstants.WEBUI_DEFAULT_SELECTING_CAPTURED_OBJECT_SELECTOR_METHOD,
                		DEFAULT_SELECTING_CAPTURED_OBJECT_SELECTOR_METHOD));
    }

    /**
     * @param list List&lt;Pair&lt;String, Boolean>>
     * @return a string of left-1,right-1;left-2,right-2;...
     * @see #parseStringList
     * @see com.kms.katalon.util.collections.Pair
     */
    private String flattenStringBooleanList(List<Pair<String, Boolean>> list) {
        if (list == null || list.isEmpty()) {
            return StringConstants.EMPTY;
        }
        return list.stream().map(i -> i.getLeft() + "," + i.getRight()).collect(Collectors.joining(";"));
    }

    /**
     * @param str String in the format of key1,value1;key2,value2;...
     * @return List&lt;Pair&lt;String, Boolean>>
     * @see #flatList
     * @see com.kms.katalon.util.collections.Pair
     */
    private List<Pair<String, Boolean>> parseStringBooleanString(String str) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        return Stream.of(str.split(";"))
                .map(i -> i.split(","))
                .map(i -> new Pair<String, Boolean>(i[0], Boolean.valueOf(i[1])))
                .collect(Collectors.toList());
    }    
    
    private SelectorMethod parseSelectorMethodString(String str) {
        if (str == null || str.isEmpty()) {
        	return SelectorMethod.BASIC;
        }
        return SelectorMethod.valueOf(str);
    }    

}
