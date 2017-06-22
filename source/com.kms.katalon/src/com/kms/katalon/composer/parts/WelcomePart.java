package com.kms.katalon.composer.parts;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.ResizableBackgroundImageComposite;
import com.kms.katalon.composer.components.impl.control.ScrollableComposite;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.components.impl.util.DesktopUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.project.handlers.NewSampleProjectHandler;
import com.kms.katalon.composer.project.menu.RecentProjectParameterizedCommandBuilder;
import com.kms.katalon.composer.project.template.SampleProjectProvider;
import com.kms.katalon.console.utils.ApplicationInfo;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class WelcomePart {
    private static final int MINIMUM_LEFT_SECTION_SIZE = 350;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    private MPart welcomePart;

    private ResizableBackgroundImageComposite mainComposite;

    private CommandCaller commandCaller;

    private Cursor handCursor;

    private Composite recentProjectDetails;

    private Font largestFont, largerFont, largerBoldFont, normalFont, normalBoldFont, smallFont;

    @PostConstruct
    public void initialize(final Composite parentComposite, MPart welcomePart) {
        commandCaller = new CommandCaller();
        this.welcomePart = welcomePart;
        createControls(parentComposite);
        registerEventListeners();
        mainComposite.layout();
    }

    private void showThisPart() {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        if (!prefStore.contains(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP)) {
            prefStore.setDefault(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP, true);
        }
        if (!prefStore.getBoolean(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP)) {
            partService.hidePart(welcomePart);
        }
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventHandler() {
            @Override
            public void handleEvent(org.osgi.service.event.Event event) {
                showThisPart();
                refreshRecentProjectComposite();
            }
        });
    }

    private void createControls(final Composite parentComposite) {
        handCursor = new Cursor(getCurrentDisplay(), SWT.CURSOR_HAND);
        parentComposite.setLayout(new FillLayout());

        final ScrollableComposite wrappedComposite = new ScrollableComposite(parentComposite,
                SWT.H_SCROLL | SWT.V_SCROLL);

        mainComposite = new ResizableBackgroundImageComposite(wrappedComposite, SWT.NONE, null);
        mainComposite.setBackground(ColorUtil.getWhiteBackgroundColor());
        mainComposite.setBackgroundMode(SWT.INHERIT_FORCE);
        GridLayout mainGridLayout = new GridLayout();
        mainGridLayout.verticalSpacing = 0;
        mainGridLayout.horizontalSpacing = 0;
        mainComposite.setLayout(mainGridLayout);

        wrappedComposite.setContent(mainComposite);
        wrappedComposite.setMinSize(new Point(1000, 630));
        wrappedComposite.setExpandHorizontal(true);
        wrappedComposite.setExpandVertical(true);

        Composite bottomComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout bottomCompositeLayout = new GridLayout(3, false);
        bottomComposite.setLayout(bottomCompositeLayout);
        GridData bottomLayoutData = new GridData(SWT.CENTER, SWT.TOP, true, true, 1, 1);
        bottomLayoutData.verticalIndent = 50;
        bottomComposite.setLayoutData(bottomLayoutData);

        createLeftSectionComposite(bottomComposite);

        createSeparatorComposite(bottomComposite);

        createRightSectionComposite(bottomComposite);
    }

    private Composite createSeparatorComposite(Composite bottomComposite) {
        Composite separatorComposite = new Composite(bottomComposite, SWT.NONE);
        separatorComposite.setLayout(new GridLayout(1, false));
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        layoutData.horizontalIndent = 25;
        separatorComposite.setLayoutData(layoutData);

        Label separator = new Label(separatorComposite, SWT.SEPARATOR | SWT.VERTICAL);
        separator.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
        return separatorComposite;
    }

    private Composite createRightSectionComposite(Composite bottomComposite) {
        final Composite rightSections = new Composite(bottomComposite, SWT.NONE);
        final GridLayout glRIghtSection = new GridLayout(1, false);
        glRIghtSection.verticalSpacing = 40;
        rightSections.setLayout(glRIghtSection);
        final GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1);
        int minimumWidth = isOnMacOS() ? 480 : 530;
        layoutData.widthHint = minimumWidth;
        layoutData.minimumWidth = minimumWidth;
        rightSections.setLayoutData(layoutData);

        createWelcomeComposite(rightSections);

        createFeaturesComposite(rightSections);

        createProjectComponentComposites(rightSections);

        return rightSections;
    }

    protected void createFeaturesComposite(final Composite rightSections) {
        Composite featureComposite = new Composite(rightSections, SWT.NONE);
        GridLayout glFeatureComposite = new GridLayout(3, true);
        glFeatureComposite.marginHeight = 0;
        glFeatureComposite.marginWidth = 0;
        glFeatureComposite.verticalSpacing = 50;
        glFeatureComposite.horizontalSpacing = 0;
        featureComposite.setLayout(glFeatureComposite);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        layoutData.minimumWidth = 400;
        layoutData.widthHint = 400;
        featureComposite.setLayoutData(layoutData);

        createFeatureComponentComposite(featureComposite, MessageConstants.PA_LBL_SAMPLE_WEB_UI_PROJECT,
                MessageConstants.PA_TOOLTIP_SAMPLE_WEB_UI_PROJECT, ImageConstants.IMG_SAMPLE_WEB_UI_PROJECT,
                new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent e) {
                        try {
                            NewSampleProjectHandler.doCreateNewSampleProject(Display.getCurrent().getActiveShell(),
                                    SampleProjectProvider.SAMPLE_WEB_UI, eventBroker);
                        } catch (Exception ex) {
                            MessageDialog.openError(null, StringConstants.ERROR, ex.getMessage());
                        }
                    }
                });

        createFeatureComponentComposite(featureComposite, MessageConstants.PA_LBL_SAMPLE_MOBILE_PROJECT,
                MessageConstants.PA_TOOLTIP_SAMPLE_MOBILE_PROJECT, ImageConstants.IMG_SAMPLE_MOBILE_PROJECT,
                new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent e) {
                        try {
                            NewSampleProjectHandler.doCreateNewSampleProject(Display.getCurrent().getActiveShell(),
                                    SampleProjectProvider.SAMPLE_MOBILE, eventBroker);
                        } catch (Exception ex) {
                            MessageDialog.openError(null, StringConstants.ERROR, ex.getMessage());
                        }
                    }
                });

        createFeatureComponentComposite(featureComposite, MessageConstants.PA_LBL_SAMPLE_WEB_SERVICE_PROJECT,
                MessageConstants.PA_TOOLTIP_SAMPLE_WEB_SERVICE_PROJECT, ImageConstants.IMG_SAMPLE_WEB_SERVICE_PROJECT,
                new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent e) {
                        try {
                            NewSampleProjectHandler.doCreateNewSampleProject(Display.getCurrent().getActiveShell(),
                                    SampleProjectProvider.SAMPLE_WEB_SERVICE, eventBroker);
                        } catch (Exception ex) {
                            MessageDialog.openError(null, StringConstants.ERROR, ex.getMessage());
                        }
                    }
                });

        createFeatureComponentComposite(featureComposite, StringConstants.PA_LBL_FAQ,
                StringConstants.PA_LBL_FAQ_DESCRIPTION, ImageConstants.IMG_FAQ, new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent e) {
                        openURL(StringConstants.PA_URL_FAQ);
                    }
                });

        createFeatureComponentComposite(featureComposite, StringConstants.PA_LBL_GETTING_STARTED,
                StringConstants.PA_LBL_GETTING_STARTED_DESCRIPTION, ImageConstants.IMG_GETTING_STARTED,
                new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent e) {
                        openURL(StringConstants.PA_URL_GETTING_STARTED);
                    }
                });

        createFeatureComponentComposite(featureComposite, StringConstants.PA_LBL_ARTICLES,
                StringConstants.PA_LBL_ARTICLES_DESCRIPTION, ImageConstants.IMG_HOW_TO_ARTICLES, new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent e) {
                        openURL(StringConstants.PA_URL_ARTICLES_STARTED);
                    }
                });
    }

    private void openURL(String url) {
        try {
            DesktopUtils.openUri(new URL(url).toURI());
        } catch (IOException | URISyntaxException ex) {
            LoggerSingleton.logError(ex);
        }
    }

    private void createFeatureComponentComposite(Composite featureComposite, String header, String description,
            Image image, MouseListener mouseListener) {
        Composite componentComposite = new Composite(featureComposite, SWT.NONE);
        componentComposite.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
        GridLayout glComponentComposite = new GridLayout(1, false);
        glComponentComposite.marginHeight = 0;
        glComponentComposite.marginWidth = 0;
        glComponentComposite.verticalSpacing = 10;
        componentComposite.setLayout(glComponentComposite);

        Label imgLabel = new Label(componentComposite, SWT.NONE);
        imgLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
        imgLabel.setImage(image);
        imgLabel.setCursor(handCursor);

        HyperLinkStyledText txtLabel = new HyperLinkStyledText(componentComposite, SWT.WRAP | SWT.CENTER);
        final GridData gdTxtLabel = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        txtLabel.setLayoutData(gdTxtLabel);
        txtLabel.setText(header);
        txtLabel.setFont(getNormalFont());
        GC gc = new GC(txtLabel);
        gdTxtLabel.widthHint = Math.max(130, gc.textExtent(txtLabel.getText()).x * 2 / 3);
        gc.dispose();
        txtLabel.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                HyperLinkStyledText label = (HyperLinkStyledText) event.widget;
                GC gc = new GC(label);
                gdTxtLabel.widthHint = Math.max(130, gc.textExtent(label.getText()).x * 2 / 3);
                gc.dispose();
            }
        });

        if (mouseListener != null) {
            imgLabel.addMouseListener(mouseListener);
            txtLabel.addMouseListener(mouseListener);
        }

        if (StringUtils.isNotEmpty(description)) {
            imgLabel.setToolTipText(description);
            txtLabel.setToolTipText(description);
        }
    }

    private Composite createLeftSectionComposite(Composite bottomComposite) {
        final Composite leftSections = new Composite(bottomComposite, SWT.NONE);
        leftSections.setLayout(new GridLayout(1, false));
        final GridData layoutData = new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1);
        int minimumWidth = isOnMacOS() ? 480 : 530;
        layoutData.widthHint = minimumWidth;
        layoutData.minimumWidth = minimumWidth;
        leftSections.setLayoutData(layoutData);

        createCommonStepComposite(leftSections);
        return leftSections;
    }

    private void createCommonStepComposite(Composite leftSections) {
        final Composite commonStepComposite = new Composite(leftSections, SWT.NONE);
        GridLayout commonStepCompositeLayout = new GridLayout(1, false);
        commonStepCompositeLayout.verticalSpacing = 40;
        commonStepComposite.setLayout(commonStepCompositeLayout);
        GridData commonStepCompositeGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        commonStepCompositeGridData.horizontalIndent = 5;
        commonStepCompositeGridData.verticalIndent = 7;
        leftSections.setLayoutData(commonStepCompositeGridData);

        createCommonStepChildComposite(commonStepComposite, MessageConstants.LBL_COMMON_STEP_RECORD_PREFIX,
                MessageConstants.LBL_COMMON_STEP_RECORD_URL_PREFIX, MessageConstants.LBL_COMMON_STEP_RECORD_DESCRIPTION,
                MessageConstants.URL_COMMON_STEP_RECORD, ImageConstants.IMG_SCREENSHOT_SCREEN_SHOT_RECORD);
        createCommonStepChildComposite(commonStepComposite, MessageConstants.LBL_COMMON_STEP_RUN_PREFIX,
                MessageConstants.LBL_COMMON_STEP_RUN_URL_PREFIX, MessageConstants.LBL_COMMON_STEP_RUN_DESCRIPTION,
                MessageConstants.URL_COMMON_STEP_RUN, ImageConstants.IMG_SCREENSHOT_SCREEN_SHOT_RUN);
        createCommonStepChildComposite(commonStepComposite, MessageConstants.LBL_COMMON_STEP_VIEW_LOGGER_PREFIX,
                MessageConstants.LBL_COMMON_STEP_VIEW_LOGGER_URL_PREFIX,
                MessageConstants.LBL_COMMON_STEP_VIEW_LOGGER_DESCRIPTION, MessageConstants.URL_COMMON_STEP_VIEW_LOGGER,
                ImageConstants.IMG_SCREENSHOT_SCREEN_SHOT_LOG_VIEWER);
    }

    private void createCommonStepChildComposite(Composite parentComposite, final String stepPrefixText,
            final String stepPrefixHyperLinkText, final String stepDetailsContentText, final String stepURL,
            final Image screenshotImage) {
        Composite stepComposite = new Composite(parentComposite, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        if (isOnRetinaDisplay()) {
            layout.horizontalSpacing = 0;
        }
        stepComposite.setLayout(layout);
        stepComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Label stepText = new Label(stepComposite, SWT.NONE);
        stepText.setText(stepPrefixText);
        stepText.setFont(getLargerBoldFont());

        HyperLinkStyledText stepPrefixHyperLinkStyledText = new HyperLinkStyledText(stepComposite, SWT.NONE) {
            @Override
            protected Color getDefaultForeground() {
                return ColorUtil.getHighlightForegroundColor();
            }
        };
        GridData styleTextLayoutData = new GridData(SWT.FILL, SWT.TOP, false, true);
        if (isOnRetinaDisplay()) {
            styleTextLayoutData.verticalIndent = 1;
            styleTextLayoutData.horizontalIndent = 5;
        }
        stepPrefixHyperLinkStyledText.setLayoutData(styleTextLayoutData);
        stepPrefixHyperLinkStyledText.setFont(getLargerFont());
        stepPrefixHyperLinkStyledText.setText(stepPrefixHyperLinkText);
        stepPrefixHyperLinkStyledText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                openURL(stepURL);
            }
        });

        Label stepDescriptionText = new Label(stepComposite, SWT.NONE);
        GridData descriptionTextLayoutData = new GridData(SWT.LEFT, SWT.TOP, true, true);
        stepDescriptionText.setLayoutData(descriptionTextLayoutData);
        if (isOnRetinaDisplay()) {
            descriptionTextLayoutData.horizontalIndent = 2;
        }
        stepDescriptionText.setText(stepDetailsContentText);
        stepDescriptionText.setFont(getLargerFont());

        Label logoLabel = new Label(stepComposite, SWT.NONE);
        logoLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
        logoLabel.setImage(screenshotImage);
    }

    private boolean isOnRetinaDisplay() {
        return isOnMacOS() && isOnHiDpiDisplay();
    }

    private boolean isOnMacOS() {
        return Platform.getOS().equals(Platform.OS_MACOSX);
    }

    private boolean isOnHiDpiDisplay() {
        final Point dpiValue = Display.getDefault().getDPI();
        return dpiValue.x == 72 && dpiValue.y == 72;
    }

    private void createProjectComponentComposites(final Composite parentSections) {
        Composite projectComposite = new Composite(parentSections, SWT.NONE);
        projectComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        projectComposite.setLayout(new GridLayout());

        createRecentProjectComposite(projectComposite);
    }

    private void createWelcomeComposite(final Composite leftSections) {
        Composite welcomeComposite = new Composite(leftSections, SWT.NONE);
        final GridLayout glWelcomeComposite = new GridLayout(2, false);
        welcomeComposite.setLayout(glWelcomeComposite);
        welcomeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label logoLabel = new Label(welcomeComposite, SWT.NONE);
        GridData brandingImagelayoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        brandingImagelayoutData.horizontalIndent = isOnMacOS() ? 47 : 61;
        logoLabel.setLayoutData(brandingImagelayoutData);
        logoLabel.setImage(ImageConstants.IMG_BRANDING);
        logoLabel.setCursor(handCursor);
        logoLabel.setToolTipText(MessageConstants.PA_TOOLTIP_KATALON_HOME_PAGE);
        logoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                openURL(MessageConstants.PA_URL_KATALON_HOME_PAGE);
            }
        });

        Composite welcomeTextComposite = new Composite(welcomeComposite, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        welcomeTextComposite.setLayout(layout);
        welcomeTextComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));

        Label welcomeLabel = new Label(welcomeTextComposite, SWT.NONE);
        welcomeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, true, 1, 1));
        welcomeLabel.setText(MessageConstants.PA_WELCOME_TO_KATALON);
        welcomeLabel.setFont(getLargestFont());

        Label versionLabel = new Label(welcomeTextComposite, SWT.NONE);
        versionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
        versionLabel
                .setText(MessageFormat.format(MessageConstants.PA_LBL_KATALON_VERSION, ApplicationInfo.versionNo()));
        versionLabel.setFont(getSmallFont());
    }

    private void createRecentProjectComposite(Composite projectComposite) {
        Composite recentProjectComposite = new Composite(projectComposite, SWT.NONE);

        GridLayout glComponentProjectComposite = new GridLayout(2, false);
        glComponentProjectComposite.horizontalSpacing = 15;
        glComponentProjectComposite.marginLeft = isOnMacOS() ? 43 : 58;
        recentProjectComposite.setLayout(glComponentProjectComposite);

        GridData gdComponentProjectComposite = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdComponentProjectComposite.widthHint = MINIMUM_LEFT_SECTION_SIZE;
        recentProjectComposite.setLayoutData(gdComponentProjectComposite);

        Label lblComponent = new Label(recentProjectComposite, SWT.NONE);
        lblComponent.setImage(ImageConstants.IMG_RECENT_PROJECT);

        Label txtComponent = new Label(recentProjectComposite, SWT.NONE);
        txtComponent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtComponent.setText(StringConstants.PA_LBL_RECENT_PROJECT);
        txtComponent.setFont(getNormalBoldFont());

        recentProjectDetails = new Composite(recentProjectComposite, SWT.NONE);
        GridData recentProjectDetailsLayoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
        recentProjectDetailsLayoutData.horizontalIndent = 40;
        recentProjectDetails.setLayoutData(recentProjectDetailsLayoutData);
        GridLayout gdRecentProjectDetails = new GridLayout(2, true);
        gdRecentProjectDetails.horizontalSpacing = 70;
        recentProjectDetails.setLayout(gdRecentProjectDetails);

        fillRecentProjectComposite(recentProjectDetails);
    }

    private void clearCompositeChildren(Composite parent) {
        for (Control control : parent.getChildren()) {
            if (control.isDisposed()) {
                continue;
            }
            control.dispose();
        }
    }

    private void fillRecentProjectComposite(Composite parent) {
        final RecentProjectParameterizedCommandBuilder commandBuilder = new RecentProjectParameterizedCommandBuilder();

        for (final ProjectEntity project : getRecentProjects()) {
            Composite cpRecentProject = new Composite(parent, SWT.NONE);
            cpRecentProject.setLayout(new GridLayout(2, false));
            cpRecentProject.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

            Label imgRecentProjectFile = new Label(cpRecentProject, SWT.NONE);
            imgRecentProjectFile.setImage(ImageConstants.IMG_RECENT_PROJECT_FILE);
            imgRecentProjectFile.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

            HyperLinkStyledText txtRecentProjectName = new HyperLinkStyledText(cpRecentProject, SWT.WRAP) {
                @Override
                protected Color getDefaultForeground() {
                    return ColorUtil.getHighlightForegroundColor();
                }
            };
            GridData gdRecentProjectName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
            gdRecentProjectName.horizontalIndent = 5;
            txtRecentProjectName.setFont(getNormalFont());
            txtRecentProjectName.setLayoutData(gdRecentProjectName);
            txtRecentProjectName.setText(StringUtils.abbreviate(project.getName(), 19));
            txtRecentProjectName.setToolTipText(
                    MessageFormat.format(StringConstants.PA_TOOLTIP_OPEN_RECENT_PROJECT, project.getName()));
            txtRecentProjectName.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDown(MouseEvent e) {
                    try {
                        commandCaller.call(commandBuilder.createRecentProjectParameterizedCommand(project));
                    } catch (CommandException ex) {
                        LoggerSingleton.logError(ex);
                    }
                }
            });
        }
    }

    private Display getCurrentDisplay() {
        if (mainComposite == null || mainComposite.isDisposed()) {
            return Display.getCurrent();
        }
        return mainComposite.getDisplay();
    }

    private Font getLargestFont() {
        if (largestFont == null) {
            largestFont = getHeaderFont(20, SWT.BOLD);
        }
        return largestFont;
    }

    private Font getLargerFont() {
        if (largerFont == null) {
            largerFont = getHeaderFont(14, SWT.NONE);
        }
        return largerFont;
    }

    private Font getLargerBoldFont() {
        if (largerBoldFont == null) {
            largerBoldFont = getHeaderFont(14, SWT.BOLD);
        }
        return largerBoldFont;
    }

    private Font getNormalFont() {
        if (normalFont == null) {
            normalFont = getHeaderFont(12, SWT.NONE);
        }
        return normalFont;
    }

    private Font getNormalBoldFont() {
        if (normalBoldFont == null) {
            normalBoldFont = getHeaderFont(12, SWT.BOLD);
        }
        return normalBoldFont;
    }

    private Font getSmallFont() {
        if (smallFont == null) {
            smallFont = getHeaderFont(11, SWT.ITALIC);
        }
        return smallFont;
    }

    private Font getHeaderFont(int size, int style) {
        return new Font(getCurrentDisplay(), new FontData(getFontName(), size, style));
    }

    private String getFontName() {
        return Platform.OS_WIN32.equals(Platform.getOS()) ? "Segoe UI"
                : JFaceResources.getHeaderFont().getFontData()[0].getName();
    }

    @PreDestroy
    public void dispose() {
        dispostFont();
    }

    private void dispostFont() {
        largestFont.dispose();
        largerFont.dispose();
        largerBoldFont.dispose();
        normalFont.dispose();
        normalBoldFont.dispose();
        smallFont.dispose();
    }

    @Focus
    public void setFocus() {
        mainComposite.forceFocus();
    }

    private void refreshRecentProjectComposite() {
        clearCompositeChildren(recentProjectDetails);
        fillRecentProjectComposite(recentProjectDetails);
        recentProjectDetails.layout(true);
    }

    private List<ProjectEntity> getRecentProjects() {
        try {
            return ProjectController.getInstance().getRecentProjects();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return Collections.emptyList();
        }
    }

    private class HyperLinkStyledText extends StyledText implements MouseTrackListener {

        private StyleRange hyperLinkStyleRange;

        public HyperLinkStyledText(Composite parent, int style) {
            super(parent, style | SWT.READ_ONLY);

            addMouseTrackListener(this);

            addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    e.doit = false;
                    setSelection(StringUtils.defaultString(getText()).length());
                }
            });

            setCaret(null);
        }

        @Override
        public void setText(String text) {
            super.setText(text);
            hyperLinkStyleRange = new StyleRange(0, text.length(), getDefaultForeground(), getDefaultBackground());
            hyperLinkStyleRange.underlineStyle = SWT.UNDERLINE_LINK;
            setStyleRange(hyperLinkStyleRange);
        }

        protected Color getDefaultForeground() {
            return null;
        }

        protected Color getDefaultBackground() {
            return null;
        }

        private void changeUnderlineMode(boolean underline) {
            if (hyperLinkStyleRange == null) {
                return;
            }
            setRedraw(false);
            hyperLinkStyleRange.underline = underline;
            setStyleRange(hyperLinkStyleRange);
            setRedraw(true);
        }

        @Override
        public void mouseEnter(MouseEvent e) {
            changeUnderlineMode(true);
        }

        @Override
        public void mouseExit(MouseEvent e) {
            changeUnderlineMode(false);
        }

        @Override
        public void mouseHover(MouseEvent e) {
            changeUnderlineMode(true);
        }
    }
}
