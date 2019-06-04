package com.kms.katalon.composer.parts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageDataProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.application.preference.ProjectSettingPreference;
import com.kms.katalon.composer.components.ComponentBundleActivator;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.project.dialog.NewProjectDialog;
import com.kms.katalon.composer.project.handlers.NewSampleLocalProjectHandler;
import com.kms.katalon.composer.project.menu.ProjectParameterizedCommandBuilder;
import com.kms.katalon.composer.project.sample.SampleLocalProject;
import com.kms.katalon.composer.project.sample.SampleProject;
import com.kms.katalon.composer.project.sample.SampleRemoteProject;
import com.kms.katalon.composer.project.sample.SampleRemoteProjectProvider;
import com.kms.katalon.composer.project.template.SampleProjectProvider;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.PreferenceConstants;
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

    private static final String KEY_ID = "ID";

    private static final String KEY_CONTENT = "CONTENT";

    private static final String KEY_BG_ACTIVE = "BG-ACTIVE";

    private static final String KEY_BG_INACTIVE = "BG-INACTIVE";

    private static final String TAB_LBL_RECENTS = MessageConstants.TAB_LBL_RECENTS;

    private static final String TAB_LBL_SAMPLES = MessageConstants.TAB_LBL_SAMPLE_PROJECTS;

    private static final String TAB_LBL_GETTING_STARTED = MessageConstants.TAB_LBL_GETTING_STARTED;

    private static final Cursor CURSOR_HAND = Display.getDefault().getSystemCursor(SWT.CURSOR_HAND);

    private static final int FONT_SIZE_SMALL = 10;

    private static final int FONT_SIZE_MEDIUM = 12;

    private static final int FONT_SIZE_LARGE = 14;

    private static final int TAB_WIDTH = 153;

    private static final int TAB_HEIGHT = 30;

    private static final int TAB_GETTING_STARTED_ID = 1;

    private static final int TAB_SAMPLES_ID = 2;

    private static final int TAB_RECENTS_ID = 3;

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private StackLayout stackLayout;

    private Composite tabComposite;

    private Composite tabGroup;

    private Composite tabContentCompositeStack;

    private Composite gettingStartedContent;

    private Composite samplesContent;

    private Composite recentsContent;

    private Composite recentsProjectHolder;
    
    private Composite testingTypeTabGroup;
    
//    private Composite testingTypeContentHolder;
    
    private Composite contentHolder;
    
    private StackLayout testingTypeStackLayout;
    
    private CLabel selectedTestingTypeTab;

    private CLabel tabGettingStarted;

    private CLabel tabSamples;

    private CLabel tabRecents;

    private CommandCaller commandCaller = new CommandCaller();

    private Thread thread;

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
        setLayout(new GridLayout());
        stackLayout = new StackLayout();
        createControls();
        postConstruct();
        
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
        eventBroker.subscribe(EventConstants.ACTIVATION_CHECKED, this);
    }

    public void reloadRecentProjects() {
        if (recentsContent == null || recentsContent.isDisposed()) {
            return;
        }
        clearRecentProjectBlocks();
        populateRecentProjects();
        recentsContent.layout();
    }

    private void createControls() {
        createTabHeader();
        createTabContent();
        setExtraDataToTabs();
        addTabButtonSelectionListeners();
    }

    private void setExtraDataToTabs() {
        tabGettingStarted.setData(KEY_ID, TAB_GETTING_STARTED_ID);
        tabGettingStarted.setData(KEY_CONTENT, gettingStartedContent);

        tabSamples.setData(KEY_ID, TAB_SAMPLES_ID);
        tabSamples.setData(KEY_CONTENT, samplesContent);

        tabRecents.setData(KEY_ID, TAB_RECENTS_ID);
        tabRecents.setData(KEY_CONTENT, recentsContent);
    }

    private void createTabHeader() {
        tabComposite = new Composite(this, SWT.NONE);
        tabComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout glTabComposite = new GridLayout(3, false);
        glTabComposite.verticalSpacing = 0;
        glTabComposite.horizontalSpacing = 0;
        tabComposite.setLayout(glTabComposite);

        addSpacer(tabComposite);

        tabGroup = new Composite(tabComposite, SWT.NONE);
        //tabGroup.setBackground(BACKGROUND_COLOR);
        tabGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        GridLayout glTabGroup = new GridLayout(3, false);
        glTabGroup.marginWidth = 0;
        glTabGroup.marginHeight = 0;
        glTabGroup.horizontalSpacing = 0;
        glTabGroup.verticalSpacing = 0;
        tabGroup.setLayout(glTabGroup);

        tabGettingStarted = createTabItem(tabGroup, TAB_LBL_GETTING_STARTED, ImageConstants.TAB_FIRST_ACTIVE,
                ImageConstants.TAB_FIRST_INACTIVE);
        tabSamples = createTabItem(tabGroup, TAB_LBL_SAMPLES, ImageConstants.TAB_MIDDLE_ACTIVE,
                ImageConstants.TAB_MIDDLE_INACTIVE);
        tabRecents = createTabItem(tabGroup, TAB_LBL_RECENTS, ImageConstants.TAB_LAST_ACTIVE,
                ImageConstants.TAB_LAST_INACTIVE);

        addSpacer(tabComposite);
    }

    private CLabel createTabItem(Composite parent, String textLabel, Image bgActive, Image bgInactive) {
        CLabel tab = new CLabel(parent, SWT.NONE);
        tab.setAlignment(SWT.CENTER);
        GridData gdTab = new GridData(SWT.FILL, SWT.FILL, false, true);
        gdTab.widthHint = TAB_WIDTH;
        gdTab.heightHint = TAB_HEIGHT;
        tab.setLayoutData(gdTab);
        tab.setText(textLabel);
        tab.setBackground(bgInactive);
        tab.setData(KEY_BG_ACTIVE, bgActive);
        if (!ComponentBundleActivator.isDarkTheme(getDisplay())) {
            tab.setData(KEY_BG_INACTIVE, bgInactive);
        }
        tab.setCursor(CURSOR_HAND);
        ControlUtils.setFontSize(tab, FONT_SIZE_SMALL);
        return tab;
    }

    private void addSpacer(Composite parent) {
        Label spacer = new Label(parent, SWT.NONE);
        spacer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    private void createTabContent() {
        tabContentCompositeStack = new Composite(this, SWT.NONE);
        tabContentCompositeStack.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tabContentCompositeStack.setLayout(stackLayout);

        createGettingStatedTabContent();
        createSamplesTabContent();
        createRecentsTabContent();
    }

    private void postConstruct() {
        int lastSelectedTabId = Math.abs(PlatformUI.getPreferenceStore().getInt(PreferenceConstants.GENERAL_LAST_HELP_SELECTED_TAB));
        setActiveTab(lastSelectedTabId);
    }

    private void addTabButtonSelectionListeners() {
        MouseAdapter selectionListener = new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                selectTab((CLabel) e.getSource());
            }
        };

        tabGettingStarted.addMouseListener(selectionListener);
        tabSamples.addMouseListener(selectionListener);
        tabRecents.addMouseListener(selectionListener);
    }

    private void selectTab(CLabel tab) {
        Arrays.stream(tabGroup.getChildren())
                .filter(tabButton -> !tab.getData(KEY_ID).equals(tabButton.getData(KEY_ID)))
                .forEach(tabButton -> {
                    tabButton.setEnabled(true);
                    ((CLabel) tabButton).setBackground((Image) tabButton.getData(KEY_BG_INACTIVE));
                    //tabButton.setForeground(ColorUtil.getTextBlackColor());
                    ((CLabel) tabButton).layout();
                });
        tab.setEnabled(false);
        tab.setBackground((Image) tab.getData(KEY_BG_ACTIVE));
        //tab.setForeground(ColorUtil.getTextWhiteColor());
        stackLayout.topControl = (Composite) tab.getData(KEY_CONTENT);
        tabContentCompositeStack.layout();
        tab.layout();
        PlatformUI.getPreferenceStore().setValue(PreferenceConstants.GENERAL_LAST_HELP_SELECTED_TAB,
                (int) tab.getData(KEY_ID));
    }

    public CLabel getActiveTab() {
        Optional<Control> activeTab = Arrays.stream(tabGroup.getChildren()).filter(tab -> !tab.isEnabled()).findFirst();
        if (activeTab.isPresent()) {
            return (CLabel) activeTab.get();
        }
        return tabGettingStarted;
    }

    public void setActiveTab(int tabId) {
        Optional<Control> targetTab = Arrays.stream(tabGroup.getChildren())
                .filter(tab -> tab.getData(KEY_ID) != null && tab.getData(KEY_ID).equals(tabId))
                .findFirst();
        if (targetTab.isPresent()) {
            selectTab((CLabel) targetTab.get());
        }
    }

    private void createGettingStatedTabContent() {
        gettingStartedContent = createContentComposite(tabContentCompositeStack);
        ((GridLayout) gettingStartedContent.getLayout()).marginHeight = 0;
        
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
        wrapper.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        testingTypeTabGroup = new Composite(wrapper, SWT.NONE);
        GridLayout glTabGroup = new GridLayout();
        glTabGroup.marginHeight = 0;
        testingTypeTabGroup.setLayout(glTabGroup);
        testingTypeTabGroup.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
        
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
        content.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));

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
    
    private void createSamplesTabContent() {
        samplesContent = createContentComposite(tabContentCompositeStack);

        Composite holder = new Composite(samplesContent, SWT.NONE);
        GridLayout glHolder = new GridLayout(3, true);
        glHolder.horizontalSpacing = 30;
        glHolder.verticalSpacing = 30;
        holder.setLayout(glHolder);
        holder.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

        thread = new Thread(() -> {
            SampleRemoteProjectProvider sampleRemoteProjectProvider = new SampleRemoteProjectProvider();
            List<SampleRemoteProject> sampleProjects = sampleRemoteProjectProvider.getSampleProjects();

            List<Composite> composites = new ArrayList<>();
            UISynchronizeService.syncExec(() -> {
                if (samplesContent == null || samplesContent.isDisposed()) {
                    return;
                }
                samplesContent.setRedraw(false);
                if (sampleProjects.size() <= 0) {
                    addDefaultSampleProjects(holder);
                }
                Composite latestComposite = null;
                for (SampleRemoteProject sample : sampleProjects) {
                    latestComposite = addProjectBlock(holder, getDefaultImage(sample),
                            ImageConstants.IMG_GITHUB_LOGO, sample.getName(), sample.getName(), new MouseAdapter() {

                                @Override
                                public void mouseUp(MouseEvent e) {
                                    NewProjectDialog dialog = new NewProjectDialog(getShell(),
                                            sample);
                                    dialog.open();
                                }
                            });
                    composites.add(latestComposite);
                }

                addProjectBlock(holder, ImageConstants.IMG_SAMPLE_MORE, MessageConstants.LBL_MORE_PROJECT,
                        MessageConstants.LBL_MORE_PROJECT_TOOLTIP, new MouseAdapter() {

                            @Override
                            public void mouseUp(MouseEvent e) {
                                Program.launch(MessageConstants.LBL_MORE_PROJECT_URL);
                            }
                        });

                samplesContent.layout(true, true);
                samplesContent.setRedraw(true);
            });

            sampleProjects.parallelStream().forEach(sample -> {
                Map<Integer, File> imageFiles = sampleRemoteProjectProvider.getThumbnailFiles(sample);

                int index = sampleProjects.indexOf(sample);
                Composite composite = composites.get(index);
                UISynchronizeService.asyncExec(() -> {
                    if (isDisposed()) {
                        return;
                    }
                    Label lblIcon = (Label) composite.getData("icon");
                    if (imageFiles != null && imageFiles.size() > 0) {
                        composite.setRedraw(false);
                        lblIcon.setImage(createImage(getDisplay(), imageFiles));
                        composite.setRedraw(true);

                        lblIcon.addDisposeListener(new DisposeListener() {
                            @Override
                            public void widgetDisposed(DisposeEvent e) {
                                imageFiles.entrySet().forEach(entry -> FileUtils.deleteQuietly(entry.getValue()));
                            }
                        });
                    }
                });
            });
        });
        thread.start();

        this.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (thread != null && !thread.isInterrupted()) {
                    thread.interrupt();
                }
            }
        });
    }

    private Image getDefaultImage(SampleProject sampleProject) {
        switch (sampleProject.getType()) {
            case MOBILE:
                return ImageConstants.IMG_SAMPLE_MOBILE_PROJECT;
            case WEBUI:
                return ImageConstants.IMG_SAMPLE_WEB_UI_PROJECT;
            case WS:
                return ImageConstants.IMG_SAMPLE_WEB_SERVICE_PROJECT;
            default:
                return ImageConstants.IMG_SAMPLE_REMOTE;
        }
    }

    private void addDefaultSampleProjects(Composite holder) {
        List<SampleLocalProject> localProjects = SampleProjectProvider.getInstance().getSampleProjects();
        localProjects.stream()
            .forEach(project -> {
                addProjectBlock(holder, getDefaultImage(project), project.getName(),
                        getTooltipForSampleLocalProject(project), new MouseAdapter() {
                            @Override
                            public void mouseUp(MouseEvent e) {
                                try {
                                    NewSampleLocalProjectHandler.doCreateNewSampleProject(
                                            Display.getCurrent().getActiveShell(), project,
                                                EventBrokerSingleton.getInstance().getEventBroker());
                                } catch (Exception ex) {
                                    MessageDialog.openError(null, StringConstants.ERROR, ex.getMessage());
                                }
                            }
                });
            });
    }
    
    private String getTooltipForSampleLocalProject(SampleLocalProject sampleLocalProject) {
        switch (sampleLocalProject.getType()) {
            case MOBILE:
                return MessageConstants.PA_TOOLTIP_SAMPLE_MOBILE_PROJECT;
            case WEBUI:
                return MessageConstants.PA_TOOLTIP_SAMPLE_WEB_UI_PROJECT;
            case WS:
                return MessageConstants.PA_TOOLTIP_SAMPLE_WEB_SERVICE_PROJECT;
            default:
                return null;
        }
    }

    public Image createImage(Display display, Map<Integer, File> allImageFiles) {
        Map<Integer, Image> allImages = allImageFiles.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    try {
                        return new Image(display, new FileInputStream(e.getValue()));
                    } catch (IOException ex) {
                        return ImageConstants.IMG_SAMPLE_REMOTE;
                    }
                }));
        ImageDataProvider dateProvider = new ImageDataProvider() {

            @Override
            public ImageData getImageData(int zoom) {
                return allImages.get(zoom).getImageData();
            }
        };
        return new Image(display, dateProvider);
    }

    private void createRecentsTabContent() {
        recentsContent = createContentComposite(tabContentCompositeStack);

        recentsProjectHolder = new Composite(recentsContent, SWT.NONE);
        GridLayout glHolder = new GridLayout(3, true);
        glHolder.horizontalSpacing = 30;
        glHolder.verticalSpacing = 30;
        recentsProjectHolder.setLayout(glHolder);
        recentsProjectHolder.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));

        populateRecentProjects();
    }

    private Composite createContentComposite(Composite parent) {
        Composite c = new Composite(parent, SWT.NONE);
        GridLayout gl = new GridLayout();
        gl.marginHeight = 30;
        c.setLayout(gl);
        c.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return c;
    }

    private void populateRecentProjects() {
        List<ProjectEntity> recentProjects = getRecentProjects();
        if (recentProjects.isEmpty()) {
            Label lblNoProject = new Label(recentsProjectHolder, SWT.NONE);
            lblNoProject.setAlignment(SWT.CENTER);
            lblNoProject.setText(MessageConstants.MSG_NO_RECENT_PROJECT);
            lblNoProject.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 3, 1));
            lblNoProject.setForeground(TEXT_COLOR);
            ControlUtils.setFontStyle(lblNoProject, SWT.NORMAL, FONT_SIZE_MEDIUM);
            return;
        }

        if (recentProjects.size() < 4) {
            GridLayout gl = (GridLayout) recentsProjectHolder.getLayout();
            gl.numColumns = recentProjects.size();
        }

        final ProjectParameterizedCommandBuilder commandBuilder = new ProjectParameterizedCommandBuilder();
        for (ProjectEntity project : recentProjects) {
            addProjectBlock(recentsProjectHolder, ImageConstants.IMG_RECENT_PROJECT_FILE,
                    StringUtils.abbreviate(project.getName(), 36),
                    MessageFormat.format(StringConstants.PA_TOOLTIP_OPEN_RECENT_PROJECT, project.getName()),
                    new MouseAdapter() {

                        @Override
                        public void mouseUp(MouseEvent e) {
                            try {
                                commandCaller.call(commandBuilder.createRecentProjectParameterizedCommand(project));
                            } catch (CommandException ex) {
                                LoggerSingleton.logError(ex);
                            }
                        }
                    });
        }

        recentsProjectHolder.layout();
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
        stepDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    }

    private Composite addProjectBlock(Composite parent, Image icon, String label, String tooltip, MouseAdapter action) {
        return addProjectBlock(parent, icon, null, label, tooltip, action);
    }

    private Composite addProjectBlock(Composite parent, Image icon, Image subIcon, String label, String tooltip,
            MouseAdapter action) {
        if (parent == null || parent.isDisposed()) {
            return null;
        }
        Composite c = new Composite(parent, SWT.BORDER);
        GridLayout gl = new GridLayout();
        c.setLayout(gl);
        GridData ld = new GridData(SWT.FILL, SWT.TOP, true, true);
        ld.widthHint = 210;
        ld.heightHint = 190;
        c.setLayoutData(ld);
        c.setCursor(CURSOR_HAND);
        c.addMouseListener(action);
        c.setToolTipText(tooltip);

        Composite imageComposite = new Composite(c, SWT.NONE);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.heightHint = 120;
        imageComposite.setLayoutData(layoutData);
        imageComposite.setLayout(new GridLayout());
        imageComposite.addMouseListener(action);

        if (subIcon != null) {
            Label lblSubIcon = new Label(imageComposite, SWT.NONE);
            lblSubIcon.setAlignment(SWT.RIGHT);
            lblSubIcon.setImage(subIcon);
            lblSubIcon.setToolTipText(tooltip);
            lblSubIcon.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
            lblSubIcon.addMouseListener(action);
        }

        Label lblIcon = new Label(imageComposite, SWT.NONE);
        lblIcon.setAlignment(SWT.CENTER);
        lblIcon.setImage(icon);
        lblIcon.setToolTipText(tooltip);
        lblIcon.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, true, true));
        lblIcon.addMouseListener(action);
        c.setData("icon", lblIcon);

        Composite textComposite = new Composite(c, SWT.NONE);
        GridData gdTextComposite = new GridData(SWT.FILL, SWT.BOTTOM, true, true);
        gdTextComposite.heightHint = 60;
        textComposite.setLayoutData(gdTextComposite);
        textComposite.addMouseListener(action);

        GridLayout glTextComposite = new GridLayout();
        glTextComposite.marginWidth = 30;
        glTextComposite.marginBottom = 5;
        textComposite.setLayout(glTextComposite);

        Text lblText = new Text(textComposite, SWT.WRAP | SWT.CENTER | SWT.READ_ONLY);
        lblText.setText(label);
        lblText.setToolTipText(tooltip);
        lblText.setForeground(TEXT_COLOR);
        lblText.setBackground(textComposite.getBackground());
        lblText.setCursor(CURSOR_HAND);
        ControlUtils.setFontStyle(lblText, SWT.NORMAL, FONT_SIZE_MEDIUM);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gridData.heightHint = 2 * lblText.getLineHeight();
        lblText.setLayoutData(gridData);
        lblText.addMouseListener(action);
        lblText.getLineCount();
        return c;
    }

    private void clearRecentProjectBlocks() {
        if (recentsProjectHolder == null || recentsProjectHolder.isDisposed()) {
            return;
        }
        for (Control control : recentsProjectHolder.getChildren()) {
            if (control.isDisposed()) {
                continue;
            }
            control.dispose();
        }
    }

    private List<ProjectEntity> getRecentProjects() {
        try {
            return new ProjectSettingPreference().getRecentProjects();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return Collections.emptyList();
        }
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
            case EventConstants.ACTIVATION_CHECKED:
                postConstruct();
                break;
            default:
                break;
        }
    }

    public void onPartClosed() {
        eventBroker.unsubscribe(this);

        
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}
