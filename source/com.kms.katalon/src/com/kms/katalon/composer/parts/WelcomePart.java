package com.kms.katalon.composer.parts;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
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
import com.kms.katalon.composer.project.constants.CommandId;
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
    private static final int MINIMUM_RIGHT_SECTION_SIZE = 500;

    private static final int MINIMUM_LEFT_SECTION_SIZE = 350;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    private MPart welcomePart;

    private ResizableBackgroundImageComposite mainComposite;

    private CommandCaller commandCaller;

    private Cursor handCursor;

    @PostConstruct
    public void initialize(final Composite parentComposite, MPart welcomePart) {
        commandCaller = new CommandCaller();
        this.welcomePart = welcomePart;
        createControls(parentComposite);
        registerEventListeners();
        mainComposite.layout();
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventHandler() {
            @Override
            public void handleEvent(org.osgi.service.event.Event event) {
                IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
                if (!prefStore.contains(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP)) {
                    prefStore.setDefault(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP, true);
                }
                if (!prefStore.getBoolean(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP)) {
                    partService.hidePart(welcomePart);
                }
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
        mainComposite.setLayout(new GridLayout());

        wrappedComposite.setContent(mainComposite);
        wrappedComposite.setMinSize(new Point(1000, 600));
        wrappedComposite.setExpandHorizontal(true);
        wrappedComposite.setExpandVertical(true);

        Composite topComposite = new Composite(mainComposite, SWT.NONE);
        topComposite.setLayout(new GridLayout(1, false));
        final GridData topCompositeLayoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        topComposite.setLayoutData(topCompositeLayoutData);

        Composite bottomComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout bottomCompositeLayout = new GridLayout(3, false);
        bottomCompositeLayout.horizontalSpacing = 30;
        bottomCompositeLayout.marginRight = 30;
        bottomCompositeLayout.marginLeft = 30;
        bottomComposite.setLayout(bottomCompositeLayout);
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        final Composite leftSections = createLeftSectionComposite(bottomComposite);

        final Composite separatorComposite = createSeparatorComposite(bottomComposite);

        final Composite rightSections = createRightSectionComposite(bottomComposite);

        mainComposite.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                layoutPage(topCompositeLayoutData, leftSections, separatorComposite, rightSections);
            }

            private void layoutPage(final GridData topCompositeLayoutData, final Composite leftSections,
                    final Composite separatorComposite, final Composite rightSections) {
                adjustTopCompositeSize(topCompositeLayoutData);
                equalizeSectionsWidth(leftSections, rightSections);
                mainComposite.layout();
                equalizeSectionsHeight(leftSections, rightSections, separatorComposite);
                mainComposite.layout();
            }

            private void adjustTopCompositeSize(GridData topCompositeLayoutData) {
                topCompositeLayoutData.heightHint = mainComposite.getSize().y / 8;
            }

            private void equalizeSectionsWidth(Composite leftSectionComposite, Composite rightSectionComposite) {
                int totalWidth = mainComposite.getSize().x - 300;
                GridData leftSectionLayoutData = (GridData) leftSectionComposite.getLayoutData();
                GridData rightSectionLayoutData = (GridData) rightSectionComposite.getLayoutData();
                leftSectionLayoutData.widthHint = Math.max(MINIMUM_LEFT_SECTION_SIZE, totalWidth / 4);
                rightSectionLayoutData.widthHint = Math.max(MINIMUM_RIGHT_SECTION_SIZE,
                        totalWidth - leftSectionLayoutData.widthHint);
            }

            private void equalizeSectionsHeight(Composite leftSectionComposite, Composite rightSectionComposite,
                    Composite separatorComposite) {
                Point leftSectionsSize = leftSectionComposite.getSize();
                Point rightSectionsSize = rightSectionComposite.getSize();
                int maxHeight = Math.max(leftSectionsSize.y, rightSectionsSize.y);
                leftSectionComposite.setSize(leftSectionsSize.x, maxHeight);
                rightSectionComposite.setSize(rightSectionsSize.x, maxHeight);
                separatorComposite.setSize(separatorComposite.getSize().x, maxHeight);
            }
        });
    }

    private Composite createSeparatorComposite(Composite bottomComposite) {
        Composite separatorComposite = new Composite(bottomComposite, SWT.NONE);
        separatorComposite.setLayout(new GridLayout(1, false));
        separatorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        Label separator = new Label(separatorComposite, SWT.SEPARATOR | SWT.VERTICAL);
        separator.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));
        return separatorComposite;
    }

    private Composite createRightSectionComposite(Composite bottomComposite) {
        final Composite rightSections = new Composite(bottomComposite, SWT.NONE);
        rightSections.setLayout(new GridLayout(1, false));
        rightSections.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
        Composite featureComposite = new Composite(rightSections, SWT.NONE);
        GridLayout glFeatureComposite = new GridLayout(3, true);
        glFeatureComposite.marginHeight = 0;
        glFeatureComposite.verticalSpacing = 110;
        featureComposite.setLayout(glFeatureComposite);
        featureComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

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

        createFeatureComponentComposite(featureComposite, MessageConstants.PA_LBL_SAMPLE_WEB_UI_PROJECT,
                MessageConstants.PA_TOOLTIP_SAMPLE_WEB_UI_PROJECT, ImageConstants.IMG_SAMPLE_WEB_UI_PROJECT,
                new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent e) {
                        try {
                            SampleProjectProvider.getInstance().openSampleWebUIProject();
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
                            SampleProjectProvider.getInstance().openSampleMobileProject();
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
                            SampleProjectProvider.getInstance().openSampleWebServiceProject();
                        } catch (Exception ex) {
                            MessageDialog.openError(null, StringConstants.ERROR, ex.getMessage());
                        }
                    }
                });
        return rightSections;
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
        componentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glComponentComposite = new GridLayout(1, false);
        glComponentComposite.marginHeight = 0;
        glComponentComposite.verticalSpacing = 10;
        componentComposite.setLayout(glComponentComposite);

        Label imgLabel = new Label(componentComposite, SWT.NONE);
        imgLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
        imgLabel.setImage(image);
        imgLabel.setCursor(handCursor);

        HyperLinkStyledText txtLabel = new HyperLinkStyledText(componentComposite, SWT.WRAP | SWT.CENTER);
        final GridData gdTxtLabel = new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1);
        txtLabel.setLayoutData(gdTxtLabel);
        txtLabel.setText(header);
        txtLabel.setFont(getNormalFont());
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
        GridLayout leftSectionLayout = new GridLayout(1, false);
        leftSectionLayout.verticalSpacing = 30;
        leftSections.setLayout(leftSectionLayout);
        leftSections.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

        createWelcomeComposite(leftSections);

        createProjectComponentComposites(leftSections);

        return leftSections;
    }

    private void createProjectComponentComposites(final Composite leftSections) {
        Composite projectComposite = new Composite(leftSections, SWT.NONE);
        projectComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glProjectComposite = new GridLayout();
        glProjectComposite.verticalSpacing = 20;
        projectComposite.setLayout(glProjectComposite);

        createClickableComponentComposite(projectComposite, StringConstants.PA_LBL_NEW_PROJECT,
                StringConstants.PA_LBL_NEW_PROJECT_DESCRIPTION, ImageConstants.IMG_NEW_PROJECT, new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent e) {
                        try {
                            commandCaller.call(CommandId.PROJECT_ADD);
                        } catch (CommandException ex) {
                            LoggerSingleton.logError(ex);
                        }
                    }
                });

        createClickableComponentComposite(projectComposite, StringConstants.PA_LBL_OPEN_PROJECT,
                StringConstants.PA_LBL_OPEN_PROJECT_DESCRIPTION, ImageConstants.IMG_OPEN_PROJECT, new MouseAdapter() {
                    @Override
                    public void mouseDown(MouseEvent e) {
                        try {
                            commandCaller.call(CommandId.PROJECT_OPEN);
                        } catch (CommandException ex) {
                            LoggerSingleton.logError(ex);
                        }
                    }
                });

        createRecentProjectComposite(projectComposite);
    }

    private void createWelcomeComposite(final Composite leftSections) {
        Composite welcomeComposite = new Composite(leftSections, SWT.NONE);
        welcomeComposite.setLayout(new GridLayout(1, false));
        welcomeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label logoLabel = new Label(welcomeComposite, SWT.NONE);
        logoLabel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
        logoLabel.setImage(ImageConstants.IMG_BRANDING);
        logoLabel.setCursor(handCursor);
        logoLabel.setToolTipText(MessageConstants.PA_TOOLTIP_KATALON_HOME_PAGE);
        logoLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                openURL(MessageConstants.PA_URL_KATALON_HOME_PAGE);
            }
        });

        Label welcomeLabel = new Label(welcomeComposite, SWT.NONE);
        welcomeLabel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
        welcomeLabel.setText(MessageConstants.PA_WELCOME_TO_KATALON);
        welcomeLabel.setFont(getLargeFont());

        Label versionLabel = new Label(welcomeComposite, SWT.NONE);
        versionLabel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1));
        versionLabel
                .setText(MessageFormat.format(MessageConstants.PA_LBL_KATALON_VERSION, ApplicationInfo.versionNo()));
        versionLabel.setFont(getSmallFont());
    }

    private void createRecentProjectComposite(Composite projectComposite) {
        Composite recentProjectComposite = createComponentLayout(projectComposite);
        Label lblComponent = new Label(recentProjectComposite, SWT.NONE);
        lblComponent.setImage(ImageConstants.IMG_RECENT_PROJECT);

        Label txtComponent = new Label(recentProjectComposite, SWT.NONE);
        txtComponent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtComponent.setText(StringConstants.PA_LBL_RECENT_PROJECT);
        txtComponent.setFont(getNormalFont());

        Composite recentProjectDetails = new Composite(recentProjectComposite, SWT.NONE);
        GridData recentProjectDetailsLayoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
        recentProjectDetailsLayoutData.horizontalIndent = 50;
        recentProjectDetails.setLayoutData(recentProjectDetailsLayoutData);
        GridLayout gdRecentProjectDetails = new GridLayout(2, false);
        gdRecentProjectDetails.horizontalSpacing = 10;
        recentProjectDetails.setLayout(gdRecentProjectDetails);
        final RecentProjectParameterizedCommandBuilder commandBuilder = new RecentProjectParameterizedCommandBuilder();
        for (final ProjectEntity project : getRecentProjects()) {
            Label imgRecentProjectFile = new Label(recentProjectDetails, SWT.NONE);
            imgRecentProjectFile.setImage(ImageConstants.IMG_RECENT_PROJECT_FILE);
            imgRecentProjectFile.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));

            HyperLinkStyledText txtRecentProjectName = new HyperLinkStyledText(recentProjectDetails, SWT.WRAP) {
                @Override
                protected Color getDefaultForeground() {
                    return ColorUtil.getHighlightForegroundColor();
                }
            };
            txtRecentProjectName.setFont(getNormalFont());
            txtRecentProjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            txtRecentProjectName.setText(project.getName());
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

    private Composite createClickableComponentComposite(Composite parentComposite, String header, String description,
            Image image, MouseListener mouseListener) {
        Composite componentProjectComposite = createComponentLayout(parentComposite);

        Label lblComponent = new Label(componentProjectComposite, SWT.NONE);
        lblComponent.setImage(image);
        lblComponent.setCursor(handCursor);

        HyperLinkStyledText txtComponent = new HyperLinkStyledText(componentProjectComposite, SWT.NONE);
        txtComponent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtComponent.setText(header);
        txtComponent.setFont(getNormalFont());

        if (StringUtils.isNotEmpty(description)) {
            lblComponent.setToolTipText(description);
            txtComponent.setToolTipText(description);
        }
        if (mouseListener != null) {
            lblComponent.addMouseListener(mouseListener);
            txtComponent.addMouseListener(mouseListener);
        }
        return componentProjectComposite;
    }

    private Composite createComponentLayout(Composite parentComposite) {
        Composite componentProjectComposite = new Composite(parentComposite, SWT.NONE);
        GridLayout glComponentProjectComposite = new GridLayout(2, false);
        glComponentProjectComposite.horizontalSpacing = 15;
        glComponentProjectComposite.marginLeft = 50;
        componentProjectComposite.setLayout(glComponentProjectComposite);
        GridData gdComponentProjectComposite = new GridData(SWT.CENTER, SWT.TOP, true, false, 1, 1);
        gdComponentProjectComposite.widthHint = MINIMUM_LEFT_SECTION_SIZE;
        componentProjectComposite.setLayoutData(gdComponentProjectComposite);
        return componentProjectComposite;
    }

    private Display getCurrentDisplay() {
        if (mainComposite == null || mainComposite.isDisposed()) {
            return Display.getCurrent();
        }
        return mainComposite.getDisplay();
    }

    private Font getLargeFont() {
        return getHeaderFont(18, SWT.BOLD);
    }

    private Font getNormalFont() {
        return getHeaderFont(12, SWT.NONE);
    }

    private Font getSmallFont() {
        return getHeaderFont(11, SWT.ITALIC);
    }

    private Font getHeaderFont(int size, int style) {
        return new Font(getCurrentDisplay(), new FontData(getFontName(), size, style));
    }

    private String getFontName() {
        return Platform.OS_WIN32.equals(Platform.getOS()) ? "Segoe UI"
                : JFaceResources.getHeaderFont().getFontData()[0].getName();
    }

    @Focus
    public void setFocus() {
        mainComposite.forceFocus();
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
