package com.kms.katalon.execution.webui.setting.test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;

import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.execution.webui.constants.StringConstants;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.util.collections.Pair;

public class WebUiExecutionSettingStoreTest {

    @Test
    public void testWebUiSettingStoreInitializedCorrectly() throws IOException {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setFolderLocation(getExtensionsDirectory("resources/test1").getAbsolutePath());
        WebUiExecutionSettingStore store = new WebUiExecutionSettingStore(projectEntity);
        assertThat("Action delay default is 0", store.getActionDelay() == 0);
        assertThat("Action delay default is in second", store.getUseDelayActionTimeUnit().equals(TimeUnit.SECONDS));
        assertThat("Selector method default is BASIC", store.getCapturedTestObjectSelectorMethod()
                .toString()
                .equals(WebUiExecutionSettingStore.DEFAULT_SELECTING_CAPTURED_OBJECT_SELECTOR_METHOD));
        assertThat("Page load timeout default is 30", store.getPageLoadTimeout() == 30);
        assertThat("Disable page load timeout by default", store.getEnablePageLoadTimeout() == false);
        assertThat("Disable ignore page load timeout by default", store.getIgnorePageLoadTimeout() == false);
        assertThat("Disable Self Healing by default", store.isEnableSelfHealing() == false);
        assertThat("Default exclude keywords must be verifyElementPresent and verifyElementNotPresent",
                store.getExcludeKeywordList() == Arrays.asList("verifyElementPresent", "verifyElementNotPresent"));
        String actualMethodsPriorityOrder = convertMethodsPriorityOrderToString(store);
        assertThat("Default methods priority must be all true, and along with this order: XPATH, BASIC, CSS, IMAGE",
                actualMethodsPriorityOrder.equals(WebUiExecutionSettingStore.DEFAULT_METHODS_PRIORITY_ORDER));
        assertThat("List of captured object properties is initialized correctly",
                flattenStringBooleanList(store.getCapturedTestObjectAttributeLocators())
                        .equals(WebUiExecutionSettingStore.DEFAULT_SELECTING_CAPTURED_OBJECT_PROPERTIES));
        assertThat("List of captured XPath types is initialized correctly",
                flattenStringBooleanList(store.getDefaultCapturedObjectXpathLocators())
                        .equals(WebUiExecutionSettingStore.DEFAULT_SELECTING_CAPTURED_OBJECT_XPATHS));
    }

    @Test
    public void testWebUiSettingStoreCanPersistValuesCorrectly() throws IOException {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setFolderLocation(getExtensionsDirectory("resources/test2").getAbsolutePath());
        WebUiExecutionSettingStore store = new WebUiExecutionSettingStore(projectEntity);
        store.setUseDelayActionTimeUnit(TimeUnit.SECONDS);
        store.setActionDelay(250);
        store.setIEHangTimeout(22);
        store.setEnablePageLoadTimeout(false);
        store.setCapturedTestObjectSelectorMethod(SelectorMethod.XPATH);
        store.setIgnorePageLoadTimeout(true);
        store.setEnableSelfHealing(true);
        store.setExcludeKeywordList(new ArrayList<>());
        store.setMethodsPritorityOrder(new ArrayList<>());

        WebUiExecutionSettingStore anotherStore = new WebUiExecutionSettingStore(projectEntity);
        assertThat("User can specify use delay action in second",
                anotherStore.getUseDelayActionTimeUnit().equals(TimeUnit.SECONDS));
        assertThat("User can specify the amount of action delay", anotherStore.getActionDelay() == 250);
        assertThat("User can specify the IE hang timeout", anotherStore.getIEHangTimeout() == 22);
        assertThat("User can change page load timeout", anotherStore.getEnablePageLoadTimeout() == false);
        assertThat("User can change selector method",
                anotherStore.getCapturedTestObjectSelectorMethod().toString().equals("XPATH"));
        assertThat("User can change option to ignore page load timeout",
                anotherStore.getIgnorePageLoadTimeout() == true);
        assertThat("User can toggle Self Healing status", anotherStore.isEnableSelfHealing() == true);
        assertThat("User can change excluded Keyword List", anotherStore.getExcludeKeywordList().size() == 0);
        assertThat("User can change methods priority order", anotherStore.getMethodsPriorityOrder().size() == 0);
        
    }

    private File getExtensionsDirectory(String path) throws IOException {
        File bundleFile = FileLocator.getBundleFile(Platform.getBundle("com.kms.katalon.execution.webui.test"));
        if (bundleFile.isDirectory()) {
            return new File(bundleFile + File.separator + path);
        } else {
            return new File(ClassPathResolver.getConfigurationFolder(), path);
        }
    }

    private String convertMethodsPriorityOrderToString(WebUiExecutionSettingStore store) throws IOException {
        List<Pair<SelectorMethod, Boolean>> methodsPriorityOrder = store.getMethodsPriorityOrder();
        List<Pair<String, Boolean>> convertedList = new ArrayList<>();
        for (Pair<SelectorMethod, Boolean> element : methodsPriorityOrder) {
            Pair<String, Boolean> convertedElement = new Pair<String, Boolean>(element.getLeft().toString(),
                    (boolean) element.getRight());
            convertedList.add(convertedElement);
        }
        return flattenStringBooleanList(convertedList);
    }

    private String flattenStringBooleanList(List<Pair<String, Boolean>> list) {
        if (list == null || list.isEmpty()) {
            return StringConstants.EMPTY;
        }
        return list.stream().map(i -> i.getLeft() + "," + i.getRight()).collect(Collectors.joining(";"));
    }
}
