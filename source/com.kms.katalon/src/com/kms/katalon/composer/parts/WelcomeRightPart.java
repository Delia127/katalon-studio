package com.kms.katalon.composer.parts;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;

public class WelcomeRightPart extends Composite implements EventHandler {

//    private static final Color BACKGROUND_COLOR = ColorUtil.getCompositeBackgroundColor();
    private static final Color BACKGROUND_COLOR = ColorUtil.getColor("#FAFAFA");

    private static final Color BLUE_COLOR = ColorUtil.getColor("#00A9FF");

    private static final Color TEXT_COLOR = ColorUtil.getTextBlackColor();
    
    private static final Color STEP_DESCRIPTION_COLOR = ColorUtil.getColor("#212121");

    private static final String KEY_CONTENT = "CONTENT";

    private static final int FONT_SIZE_SMALL = 10;

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private Composite gettingStartedContent;
    
    private Composite testingTypeTabGroup;
    
//    private Composite testingTypeContentHolder;
    
    private StackLayout testingTypeStackLayout;
    
    private CLabel selectedTestingTypeTab;

    private CLabel tabWebUi;

    private CLabel tabMobile;

    private CLabel tabApi;

    private CLabel tabScripting;

    private Composite webUiTabContent;

    private Composite mobileTabContent;

    private Composite apiTabContent;

    private Composite scriptingTabContent;

    private static final SelectionAdapter linkSelectionAdapter = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            Program.launch(e.text);
        }
    };

    public WelcomeRightPart(Composite parent, int style) {
        super(parent, style);
        gettingStartedContent = this;
        setLayout(new GridLayout());
        createControls();
        
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
        eventBroker.subscribe(EventConstants.WORKSPACE_CREATED, this);
    }

    private void createControls() {
        createGettingStatedTabContent();
    }

    private void createGettingStatedTabContent() {
        createTestingTypeTabHeader();
        
        createTabContentForTestingTypes();
        
        setExtraDataToTestingTypeTabs();
        
        registerListenersForTestingTypeTabs();
        
        setDefaultTestingTypeTabByProjectType();
    }
    
    private void setDefaultTestingTypeTabByProjectType() {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        if (project != null && project.getType() == ProjectType.WEBSERVICE) {
            handleSelectingTestingTypeTab(tabApi);
        } else {
            handleSelectingTestingTypeTab(tabWebUi);
        }
    }
    
    private void createTestingTypeTabHeader() {
        Composite wrapper = new Composite(gettingStartedContent, SWT.NONE);
        GridLayout glWrapper = new GridLayout(1, false);
        glWrapper.marginTop = 2;
        glWrapper.marginBottom = 2;
        wrapper.setLayout(glWrapper);
        wrapper.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        
        testingTypeTabGroup = new Composite(wrapper, SWT.NONE);
        GridLayout glTabGroup = new GridLayout();
        glTabGroup.marginHeight = 0;
        testingTypeTabGroup.setLayout(glTabGroup);
        testingTypeTabGroup.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
        
        testingTypeTabGroup.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;                
                Point size = testingTypeTabGroup.getSize();
                gc.setLineWidth(3);
                gc.drawLine(0, size.y, size.x, size.y);
            }            
        });
        
        tabWebUi = createTestingTypeTab(MessageConstants.TAB_LBL_WEB_UI);
        tabApi = createTestingTypeTab(MessageConstants.TAB_LBL_API);
        tabMobile = createTestingTypeTab(MessageConstants.TAB_LBL_MOBILE);
        tabScripting = createTestingTypeTab(MessageConstants.TAB_LBL_SCRIPTING);
    }
    
    private CLabel createTestingTypeTab(String text) {
        ((GridLayout) testingTypeTabGroup.getLayout()).numColumns++;
        CLabel tabButton = new CLabel(testingTypeTabGroup, SWT.NONE);
        tabButton.setText(text);
        tabButton.setLeftMargin(15);
        tabButton.setRightMargin(15);
        tabButton.setBottomMargin(8);
        tabButton.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
        ControlUtils.setFontSize(tabButton, FONT_SIZE_SMALL);
        tabButton.setForeground(ColorUtil.getTextColor());
        
        return tabButton;
    }
    
    private void createTabContentForTestingTypes() {
        testingTypeStackLayout = new StackLayout();
        
        Composite contentHolder = new Composite(gettingStartedContent, SWT.NONE);
        contentHolder.setLayout(testingTypeStackLayout);
        contentHolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        webUiTabContent = createWebUiTabContent(contentHolder);
        mobileTabContent = createMobileTabContent(contentHolder);
        apiTabContent = createApiTabContent(contentHolder);
        scriptingTabContent = createScriptingTabContent(contentHolder);
    }
    
    private void setExtraDataToTestingTypeTabs() {
        tabWebUi.setData(KEY_CONTENT, webUiTabContent);
        tabMobile.setData(KEY_CONTENT, mobileTabContent);        
        tabApi.setData(KEY_CONTENT, apiTabContent);        
        tabScripting.setData(KEY_CONTENT, scriptingTabContent);;
    }
    
    private void registerListenersForTestingTypeTabs() {
        registerListenersForTestingTypeTab(tabWebUi);
        registerListenersForTestingTypeTab(tabMobile);
        registerListenersForTestingTypeTab(tabApi);
        registerListenersForTestingTypeTab(tabScripting);
    }

    private void registerListenersForTestingTypeTab(CLabel tab) {
        tab.addListener(SWT.MouseDown, e -> {
            handleSelectingTestingTypeTab(tab);
        });

        tab.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;
                if (tab == selectedTestingTypeTab) {
                    Point size = tab.getSize();
                    tab.setForeground(BLUE_COLOR);
                    gc.setLineWidth(3);
                    gc.drawLine(0, size.y, size.x, size.y);
                } else {
                    Point size = tab.getSize();
                    tab.setForeground(ColorUtil.getTextColor());
                    gc.setLineWidth(3);
                    gc.drawLine(0, size.y, size.x, size.y);
                }
            }
        });
    }   
    
    private void handleSelectingTestingTypeTab(CLabel tab) {
        selectedTestingTypeTab = tab;
        
        for (Control testingTypeTab : testingTypeTabGroup.getChildren()) {
            testingTypeTab.setForeground(ColorUtil.getTextColor());
            ControlUtils.setFontStyle(testingTypeTab, SWT.NORMAL, -1);
            testingTypeTab.redraw();
        }
        
        tab.setForeground(BLUE_COLOR);
        ControlUtils.setFontToBeBold(tab);
        
        testingTypeStackLayout.topControl = (Composite) tab.getData(KEY_CONTENT);
        gettingStartedContent.layout(true, true);
        tab.layout();
    }
    
    private Composite createWebUiTabContent(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        createIntroLink(content, StringConstants.PA_URL_GETTING_STARTED);
        
        Composite main = new Composite(content, SWT.NONE);
        main.setLayout(new GridLayout(1, false));
        main.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

        addGettingStartedStep(main, ImageConstants.IMG_STEP_1, MessageConstants.URL_COMMON_STEP_RECORD,
                ImageConstants.IMG_SCREENSHOT_SCREEN_SHOT_RECORD);
        addGettingStartedStep(main, ImageConstants.IMG_STEP_2, MessageConstants.URL_COMMON_STEP_RUN,
                ImageConstants.IMG_SCREENSHOT_SCREEN_SHOT_RUN);
        addGettingStartedStep(main, ImageConstants.IMG_STEP_3, MessageConstants.URL_COMMON_STEP_VIEW_LOGGER,
                ImageConstants.IMG_SCREENSHOT_SCREEN_SHOT_LOG_VIEWER);

        return content;
    }
    
    private Composite createMobileTabContent(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

        createIntroLink(content, StringConstants.PA_URL_GETTING_STARTED);
        
        Composite main = new Composite(content, SWT.NONE);
        main.setLayout(new GridLayout(1, false));
        main.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

        addGettingStartedStep(main, ImageConstants.IMG_STEP_1, MessageConstants.URL_MOBILE_RECORD,
                ImageConstants.IMG_SCREEN_SHOT_MOBILE_RECORD);
        
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            addGettingStartedStep(main, ImageConstants.IMG_STEP_2,
                    MessageConstants.URL_MOBILE_CONFIG_AND_RECORD_STEP_MAC,
                    ImageConstants.IMG_SCREEN_SHOT_MOBILE_CONFIG_AND_RECORD_STEP);
        } else {
            addGettingStartedStep(main, ImageConstants.IMG_STEP_2,
                    MessageConstants.URL_MOBILE_CONFIG_AND_RECORD_STEP_WINDOWS,
                    ImageConstants.IMG_SCREEN_SHOT_MOBILE_CONFIG_AND_RECORD_STEP);
        }
        
        addGettingStartedStep(main, ImageConstants.IMG_STEP_3, MessageConstants.URL_COMMON_STEP_RUN,
                ImageConstants.IMG_SCREENSHOT_SCREEN_SHOT_RUN);
        addGettingStartedStep(main, ImageConstants.IMG_STEP_4, MessageConstants.URL_COMMON_STEP_VIEW_LOGGER,
                ImageConstants.IMG_SCREEN_SHOT_MOBILE_LOG_VIEWER);

        return content;
    }
    
    private Composite createApiTabContent(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

        createIntroLink(content, StringConstants.PA_URL_GETTING_STARTED);

        Composite main = new Composite(content, SWT.NONE);
        main.setLayout(new GridLayout(1, false));
        main.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));
        
        addGettingStartedStep(main, ImageConstants.IMG_STEP_1, MessageConstants.URL_USE_DRAFT_REQUEST,
                ImageConstants.IMG_SCREEN_SHOT_USE_DRAFT_REQUEST);
        addGettingStartedStep(main, ImageConstants.IMG_STEP_2, MessageConstants.URL_SAVE_DRAFT_REQUEST,
                ImageConstants.IMG_SCREEN_SHOT_SAVE_DRAFT_REQUEST);
        addGettingStartedStep(main, ImageConstants.IMG_STEP_3, MessageConstants.URL_USE_WEB_SERVICE_IN_TEST_CASE,
                ImageConstants.IMG_SCREEN_SHOT_ADD_REQUEST_TO_TEST_CASE);
        addGettingStartedStep(main, ImageConstants.IMG_STEP_4, MessageConstants.URL_API_EXECUTE_TEST_CASE,
                ImageConstants.IMG_SCREENSHOT_SCREEN_SHOT_RUN);
        addGettingStartedStep(main, ImageConstants.IMG_STEP_5, MessageConstants.URL_COMMON_STEP_VIEW_LOGGER,
                ImageConstants.IMG_SCREEN_SHOT_API_LOG_VIEWER);

        return content;
    }
    
    private Composite createScriptingTabContent(Composite parent) {
        Composite content = new Composite(parent, SWT.NONE);
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

        createIntroLink(content, StringConstants.PA_URL_GETTING_STARTED);
        
        Composite main = new Composite(content, SWT.NONE);
        main.setLayout(new GridLayout(1, false));
        main.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

        addGettingStartedStep(main, ImageConstants.IMG_SCRIPT_BULLET, MessageConstants.URL_SCRIPT_NEW_TEST_CASE,
                ImageConstants.IMG_SCREEN_SHOT_SCRIPT_NEW_TEST_CASE);
        addGettingStartedStep(main, ImageConstants.IMG_SCRIPT_BULLET, MessageConstants.URL_ADD_OR_IMPORT_KEYWORDS,
                ImageConstants.IMG_SCREEN_SHOT_ADD_OR_IMPORT_KEYWORDS);
        addGettingStartedStep(main, ImageConstants.IMG_SCRIPT_BULLET, MessageConstants.URL_CREATE_TEST_LISTENER,
                ImageConstants.IMG_SCREEN_SHOT_CREATE_TEST_LISTENER);
        addGettingStartedStep(main, ImageConstants.IMG_SCRIPT_BULLET, MessageConstants.URL_BUILD_CMD,
                ImageConstants.IMG_SCREEN_SHOT_BUILD_CMD);

        return content;
    }
    
    private void createIntroLink(Composite parent, String link) { 
        Link introText = new Link(parent, SWT.NONE);
        introText.setText(link);
        introText.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
        introText.setForeground(TEXT_COLOR);
        introText.setLinkForeground(TEXT_COLOR);
        ControlUtils.setFontStyle(introText, SWT.ITALIC, FONT_SIZE_SMALL);
        introText.addSelectionListener(linkSelectionAdapter);
    }

    private void addGettingStartedStep(Composite parent, Image stepNumberImage, String text, Image stepDetailsImage) {
        Composite c = new Composite(parent, SWT.NONE);
        GridLayout gl = new GridLayout(2, false);
        gl.marginHeight = 0;
        gl.marginWidth = 0;
        gl.marginTop = 25;
        c.setLayout(gl);
        c.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Label stepNumber = new Label(c, SWT.NONE);
        stepNumber.setImage(stepNumberImage);
        stepNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));

        Link stepText = new Link(c, SWT.NONE);
        stepText.setText(text);
        stepText.setForeground(STEP_DESCRIPTION_COLOR);
        stepText.setLinkForeground(BLUE_COLOR);
        stepText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        ControlUtils.setFontStyle(stepText, SWT.NORMAL, 12);
        stepText.addSelectionListener(linkSelectionAdapter);

        Label stepDetails = new Label(parent, SWT.NONE);
        stepDetails.setImage(stepDetailsImage);
        GridData ldStepDetails = new GridData(SWT.FILL, SWT.FILL, true, false);
        ldStepDetails.verticalIndent = 10;
        stepDetails.setLayoutData(ldStepDetails);
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.PROJECT_OPENED:
                setDefaultTestingTypeTabByProjectType();
                break;
            default:
                break;
        }
    }

    public void onPartClosed() {
        eventBroker.unsubscribe(this);
    }
}
