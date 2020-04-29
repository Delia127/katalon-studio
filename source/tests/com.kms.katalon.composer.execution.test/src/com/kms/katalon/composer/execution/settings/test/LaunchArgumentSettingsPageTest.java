package com.kms.katalon.composer.execution.settings.test;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.kms.katalon.application.helper.LicenseHelper;
import com.kms.katalon.application.helper.LicenseHelperFactory;

public class LaunchArgumentSettingsPageTest {
    
    @Mock
    private LicenseHelper licenseHelper;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        LicenseHelperFactory.set(licenseHelper);
    }
    
//    @Test
//    public void testLaunchArgumentsContentShouldNotBeRenderedForFreeUser() {
//        when(licenseHelper.isNotFreeLicense()).thenReturn(false);
//        
//        ProjectEntity projectEntity = new ProjectEntity();
//        ExecutionDefaultSettingStore settingStore = new ExecutionDefaultSettingStore(projectEntity);
//        LaunchArgumentsSettingPage settingsPage = new LaunchArgumentsSettingPage();
//        settingsPage.setStore(settingStore);
//        
//        UISynchronizeService.syncExec(() -> {
//            Display display = Display.getCurrent();
//            Shell shell = new Shell(display);
//            Composite composite = new Composite(shell, SWT.NONE);
//            Composite container =  (Composite) settingsPage.createContents(composite);
//            
//            assertThat("The container content should be empty", container.getChildren().length == 0);
//        });
//    }
//    
//    @Test
//    public void testLaunchArgumentsContentShouldBeRenderedForPaidUser() {
//        when(licenseHelper.isNotFreeLicense()).thenReturn(true);
//        
//        ProjectEntity projectEntity = new ProjectEntity();
//        ExecutionDefaultSettingStore settingStore = new ExecutionDefaultSettingStore(projectEntity);
//        LaunchArgumentsSettingPage settingsPage = new LaunchArgumentsSettingPage();
//        settingsPage.setStore(settingStore);
//        
//        UISynchronizeService.syncExec(() -> {
//            Display display = Display.getCurrent();
//            Shell shell = new Shell(display);
//            Composite composite = new Composite(shell, SWT.NONE);
//            Composite container =  (Composite) settingsPage.createContents(composite);
//            
//            Control[] children = container.getChildren();
//            assertThat("The container content should not be empty", children.length > 0);
//        });
//    }
}
