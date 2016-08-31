package com.kms.katalon.composer.parts;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.ResizableBackgroundImageComposite;
import com.kms.katalon.composer.components.impl.control.ScrollableComposite;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.components.impl.util.DesktopUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.project.constants.CommandId;
import com.kms.katalon.composer.project.menu.RecentProjectParameterizedCommandBuilder;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class WelcomePart {
    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    private MPart welcomePart;

    private ResizableBackgroundImageComposite mainComposite;

    private CommandCaller commandCaller;

    @PostConstruct
    public void initialize(final Composite parentComposite, MPart welcomePart) {
        commandCaller = new CommandCaller();
        this.welcomePart = welcomePart;
        createControls(parentComposite);
        registerEventListeners();
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventHandler() {
            @Override
            public void handleEvent(org.osgi.service.event.Event event) {
                partService.hidePart(welcomePart);
            }
        });
    }

    private void createControls(final Composite parentComposite) {
        parentComposite.setLayout(new FillLayout());

        final ScrollableComposite wrappedComposite = new ScrollableComposite(parentComposite, SWT.H_SCROLL
                | SWT.V_SCROLL);

        mainComposite = new ResizableBackgroundImageComposite(wrappedComposite, SWT.NONE,
                ImageConstants.IMG_BRANDING_BACKGROUND);
        mainComposite.setBackgroundMode(SWT.INHERIT_FORCE);
        mainComposite.setLayout(new GridLayout());

        wrappedComposite.setContent(mainComposite);
        wrappedComposite.setMinSize(new Point(800, 600));
        wrappedComposite.setExpandHorizontal(true);
        wrappedComposite.setExpandVertical(true);

        Composite topComposite = new Composite(mainComposite, SWT.NONE);
        topComposite.setLayout(new GridLayout(1, false));
        final GridData topCompositeLayoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        topComposite.setLayoutData(topCompositeLayoutData);

        Label topCompositeImage = new Label(topComposite, SWT.NONE);
        topCompositeImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
        topCompositeImage.setImage(ImageConstants.IMG_BRANDING);

        Composite bottomComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout bottomCompositeLayout = new GridLayout(3, false);
        bottomCompositeLayout.horizontalSpacing = 20;
        bottomCompositeLayout.marginRight = 70;
        bottomCompositeLayout.marginLeft = 70;
        bottomComposite.setLayout(bottomCompositeLayout);
        bottomComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        final Composite leftSections = createLeftSectionComposite(bottomComposite);

        Composite separatorComposite = new Composite(bottomComposite, SWT.NONE);
        separatorComposite.setLayout(new GridLayout(1, false));
        separatorComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        Label separator = new Label(separatorComposite, SWT.SEPARATOR | SWT.VERTICAL);
        separator.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1));

        final Composite rightSection = createRightSectionComposite(bottomComposite);

        mainComposite.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                adjustTopCompositeSize(topCompositeLayoutData);
                equalizeSectionsSize(leftSections, rightSection);
                mainComposite.layout();
            }

            private void equalizeSectionsSize(final Composite leftSectionComposite,
                    final Composite rightSectionComposite) {
                int totalWidth = leftSectionComposite.getSize().x + rightSectionComposite.getSize().x;
                GridData leftSectionLayoutData = (GridData) leftSectionComposite.getLayoutData();
                GridData rightSectionLayoutData = (GridData) rightSectionComposite.getLayoutData();
                leftSectionLayoutData.widthHint = rightSectionLayoutData.widthHint = totalWidth / 2;
            }

            private void adjustTopCompositeSize(GridData topCompositeLayoutData) {
                topCompositeLayoutData.heightHint = mainComposite.getSize().y / 3;
            }
        });
    }

    private Composite createRightSectionComposite(Composite bottomComposite) {
        final Composite rightSections = new Composite(bottomComposite, SWT.NONE);
        GridLayout rightSectionLayout = new GridLayout(1, false);
        rightSectionLayout.verticalSpacing = 30;
        rightSections.setLayout(rightSectionLayout);
        final GridData rightSectionLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        rightSections.setLayoutData(rightSectionLayoutData);

        createSection(rightSections,
                ImageConstants.IMG_FAQ, 
                StringConstants.PA_LBL_FAQ, 
                StringConstants.PA_TOOLTIP_FAQ,
                StringConstants.PA_LBL_FAQ_DESCRIPTION,
                new MouseAdapter() {
                    public void mouseDown(MouseEvent e) {
                        try {
                            DesktopUtils.openUri(new URL(StringConstants.PA_URL_FAQ).toURI());
                        } catch (IOException | URISyntaxException ex) {
                            LoggerSingleton.logError(ex);
                        }
                    };
                });

        createSection(rightSections,
                ImageConstants.IMG_GETTING_STARTED,
                StringConstants.PA_LBL_GETTING_STARTED,
                StringConstants.PA_TOOLTIP_GETTING_STARTED,
                StringConstants.PA_LBL_GETTING_STARTED_DESCRIPTION,
                new MouseAdapter() {
                    public void mouseDown(MouseEvent e) {
                        try {
                            DesktopUtils.openUri(new URL(StringConstants.PA_URL_GETTING_STARTED).toURI());
                        } catch (IOException | URISyntaxException ex) {
                            LoggerSingleton.logError(ex);
                        }
                    };
                });

        createSection(rightSections,
                ImageConstants.IMG_HOW_TO_ARTICLES,
                StringConstants.PA_LBL_ARTICLES,
                StringConstants.PA_TOOLTIP_ARTICLES,
                StringConstants.PA_LBL_ARTICLES_DESCRIPTION,
                new MouseAdapter() {
                    public void mouseDown(MouseEvent e) {
                        try {
                            DesktopUtils.openUri(new URL(StringConstants.PA_URL_ARTICLES_STARTED).toURI());
                        } catch (IOException | URISyntaxException ex) {
                            LoggerSingleton.logError(ex);
                        }
                    };
                });
        return rightSections;
    }

    private Composite createLeftSectionComposite(Composite bottomComposite) {
        final Composite leftSections = new Composite(bottomComposite, SWT.NONE);
        GridLayout leftSectionLayout = new GridLayout(1, false);
        leftSectionLayout.verticalSpacing = 30;
        leftSections.setLayout(leftSectionLayout);
        final GridData leftSectionLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        leftSections.setLayoutData(leftSectionLayoutData);

        createSection(leftSections,
                ImageConstants.IMG_NEW_PROJECT,
                StringConstants.PA_LBL_NEW_PROJECT,
                StringConstants.PA_TOOLTIP_NEW_PROJECT,
                StringConstants.PA_LBL_NEW_PROJECT_DESCRIPTION,
                new MouseAdapter() {
                    public void mouseDown(MouseEvent e) {
                        try {
                            commandCaller.call(CommandId.PROJECT_ADD);
                        } catch (CommandException ex) {
                            LoggerSingleton.logError(ex);
                        }
                    };
                });

        createSection(leftSections,
                ImageConstants.IMG_OPEN_PROJECT,
                StringConstants.PA_LBL_OPEN_PROJECT,
                StringConstants.PA_TOOLTIP_OPEN_PROJECT,
                StringConstants.PA_LBL_OPEN_PROJECT_DESCRIPTION,
                new MouseAdapter() {
                    public void mouseDown(MouseEvent e) {
                        try {
                            commandCaller.call(CommandId.PROJECT_OPEN);
                        } catch (CommandException ex) {
                            LoggerSingleton.logError(ex);
                        }
                    };
                });

        new RecentProjectSection(leftSections, SWT.NONE);
        return leftSections;
    }

    private SimpleWelcomeSection createSection(Composite parent, final Image image, final String headerText,
            final String headerTooltipText, final String content, final MouseListener listener) {
        return new SimpleWelcomeSection(parent, SWT.NONE) {

            @Override
            public String getHeaderText() {
                return headerText;
            }

            @Override
            public Image getHeaderImage() {
                return image;
            }

            @Override
            protected String getHeaderTooltipText() {
                return headerTooltipText;
            }

            @Override
            public String getDescription() {
                return content;
            }

            @Override
            protected void handleMouseDownOnHeader(MouseEvent e) {
                if (listener != null) {
                    listener.mouseDown(e);
                }
            }
        };
    }

    private Display getCurrentDisplay() {
        if (mainComposite == null || mainComposite.isDisposed()) {
            return Display.getCurrent();
        }
        return mainComposite.getDisplay();
    }

    private Font getLargeFont() {
        return getHeaderFont(14, SWT.BOLD);
    }

    private Font getSmallFont() {
        return getHeaderFont(12, SWT.NONE);
    }

    private Font getHeaderFont(int size, int style) {
        return FontDescriptor.createFrom(JFaceResources.getHeaderFont())
                .setHeight(size)
                .setStyle(style)
                .createFont(getCurrentDisplay());
    }

    @Focus
    public void setFocus() {
        mainComposite.forceFocus();
    }

    private abstract class WelcomeSection extends Composite {

        protected StyledText lblHeader;

        public WelcomeSection(Composite parent, int style) {
            super(parent, style);

            setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
            GridLayout sectionLayout = new GridLayout(2, false);
            sectionLayout.horizontalSpacing = 20;
            setLayout(sectionLayout);

            Label sectionImage = new Label(this, SWT.NONE);
            GridData imageLayoutData = new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1);
            imageLayoutData.widthHint = 45;
            sectionImage.setLayoutData(imageLayoutData);
            sectionImage.setImage(getHeaderImage());

            Composite detailsComposite = new Composite(this, SWT.NONE);
            GridLayout detailsLayout = new GridLayout(1, false);
            detailsLayout.marginHeight = 0;
            detailsComposite.setLayout(detailsLayout);
            detailsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

            lblHeader = createHeaderText(detailsComposite);
            GridData gdLblHeader = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
            gdLblHeader.heightHint = 35;
            lblHeader.setLayoutData(gdLblHeader);
            lblHeader.setText(getHeaderText());
            lblHeader.setFont(getLargeFont());

            createDetailsControl(detailsComposite);

            registerMouseEventListeners();
        }

        protected StyledText createHeaderText(Composite detailsComposite) {
            return new LabelStyledText(detailsComposite, SWT.NONE);
        }

        protected void createDetailsControl(Composite detailsComposite) {
            // Children may override this
        }

        protected void registerMouseEventListeners() {
            // Children may override this
        }

        public abstract Image getHeaderImage();

        public abstract String getHeaderText();
    }

    private abstract class SimpleWelcomeSection extends WelcomeSection {
        public SimpleWelcomeSection(Composite parent, int style) {
            super(parent, style);
        }

        @Override
        protected StyledText createHeaderText(Composite detailsComposite) {
            return new HyperLinkStyledText(detailsComposite, SWT.NONE);
        }

        @Override
        protected void createDetailsControl(Composite detailsComposite) {
            Label lblDescription = new Label(detailsComposite, SWT.WRAP);
            lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            lblDescription.setText(getDescription());
            lblDescription.setFont(getSmallFont());
            lblDescription.setForeground(new Color(getCurrentDisplay(), new RGB(50, 50, 50)));
        }

        protected abstract String getDescription();

        @Override
        protected void registerMouseEventListeners() {
            lblHeader.setToolTipText(getHeaderTooltipText());
            registerMouseClickOnHeader();
        }

        protected abstract String getHeaderTooltipText();

        protected void registerMouseClickOnHeader() {
            lblHeader.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDown(MouseEvent e) {
                    handleMouseDownOnHeader(e);
                }
            });
        }

        protected abstract void handleMouseDownOnHeader(MouseEvent e);
    }

    private class RecentProjectSection extends WelcomeSection {

        public RecentProjectSection(Composite parent, int style) {
            super(parent, style);
        }

        @Override
        protected void createDetailsControl(Composite detailsComposite) {
            final RecentProjectParameterizedCommandBuilder commandBuilder = new RecentProjectParameterizedCommandBuilder();
            for (final ProjectEntity project : getRecentProjects()) {
                HyperLinkStyledText txtRecentProjectName = new HyperLinkStyledText(detailsComposite, SWT.NONE) {
                    @Override
                    protected Color getDefaultForeground() {
                        return ColorUtil.getHighlightForegroundColor();
                    }
                };
                txtRecentProjectName.setFont(getSmallFont());
                txtRecentProjectName.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
                txtRecentProjectName.setText(project.getName());
                txtRecentProjectName.setToolTipText(MessageFormat.format(StringConstants.PA_TOOLTIP_OPEN_RECENT_PROJECT,
                        project.getName()));
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

        private List<ProjectEntity> getRecentProjects() {
            try {
                return ProjectController.getInstance().getRecentProjects();
            } catch (Exception e) {
                LoggerSingleton.logError(e);
                return Collections.emptyList();
            }
        }

        @Override
        public Image getHeaderImage() {
            return ImageConstants.IMG_RECENT_PROJECT;
        }

        @Override
        public String getHeaderText() {
            return StringConstants.PA_LBL_RECENT_PROJECT;
        }
    }

    private class LabelStyledText extends StyledText {

        public LabelStyledText(Composite parent, int style) {
            super(parent, style);
            setCaret(null);
            setCursor(new Cursor(getDisplay(), SWT.CURSOR_ARROW));
        }
    }

    private class HyperLinkStyledText extends StyledText implements MouseTrackListener {

        private StyleRange hyperLinkStyleRange;

        public HyperLinkStyledText(Composite parent, int style) {
            super(parent, style);
            addMouseTrackListener(this);
            setCaret(null);
        }

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
