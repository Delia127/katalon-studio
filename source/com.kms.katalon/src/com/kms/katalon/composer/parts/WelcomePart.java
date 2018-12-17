package com.kms.katalon.composer.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.ScrollableComposite;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.PreferenceConstants;

public class WelcomePart {
    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    private MPart part;

    private Composite mainComposite;

    private WelcomeRightPart startPageContent;

    @PostConstruct
    public void initialize(final Composite parent, MPart part) {
        this.part = part;
        createControls(parent);
        registerEventListeners();
    }

    private void showThisPart() {
        IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
        if (!prefStore.contains(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP)) {
            prefStore.setDefault(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP, true);
        }
        if (!prefStore.getBoolean(PreferenceConstants.GENERAL_SHOW_HELP_AT_START_UP)) {
            partService.hidePart(part);
        }
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventHandler() {
            @Override
            public void handleEvent(org.osgi.service.event.Event event) {
                showThisPart();
                if (startPageContent != null || !startPageContent.isDisposed()) {
                    startPageContent.reloadRecentProjects();
                }
            }
        });
    }

    private void createControls(final Composite parent) {
        FillLayout parentLayout = new FillLayout();
        parentLayout.marginHeight = 0;
        parentLayout.marginWidth = 0;
        parent.setLayout(parentLayout);

        final ScrollableComposite container = new ScrollableComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

        mainComposite = new Composite(container, SWT.NONE);
        mainComposite.setBackground(ColorUtil.getCompositeBackgroundColor());
        mainComposite.setBackgroundMode(SWT.INHERIT_FORCE);
        GridLayout mainGridLayout = new GridLayout(2, false);
        mainGridLayout.verticalSpacing = 0;
        mainGridLayout.horizontalSpacing = 0;
        mainGridLayout.marginWidth = 0;
        mainGridLayout.marginHeight = 0;
        mainComposite.setLayout(mainGridLayout);


        mainComposite.addListener(SWT.Paint, new Listener() {
            
            @Override
            public void handleEvent(Event event) {
               Point size = mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
               if (container.getMinHeight() < size.y) {
                   container.setMinHeight(size.y);
               }
            }
        });

        container.setContent(mainComposite);
        container.setMinSize(new Point(900, 1300));
        container.setExpandHorizontal(true);
        container.setExpandVertical(true);

        WelcomeLeftPart leftComposite = new WelcomeLeftPart(mainComposite, SWT.NONE);
        leftComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));

        Composite rightComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout glRightComposite = new GridLayout(3, false);
        glRightComposite.marginHeight = 0;
        glRightComposite.marginWidth = 0;
        glRightComposite.marginTop = 40;
        glRightComposite.horizontalSpacing = 0;
        glRightComposite.verticalSpacing = 0;
        rightComposite.setLayout(glRightComposite);
        rightComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        addSpacer(rightComposite);

        startPageContent = new WelcomeRightPart(rightComposite, SWT.NONE);
        GridData gdStartPageContent = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdStartPageContent.minimumWidth = 600;
        startPageContent.setLayoutData(gdStartPageContent);
        
        addSpacer(rightComposite);
    }

    private void addSpacer(Composite rightComposite) {
        Label spacer = new Label(rightComposite, SWT.NONE);
        spacer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    @Focus
    public void setFocus() {
        mainComposite.forceFocus();
    }
    
    @PreDestroy
    public void onPartClosed() {
        if (startPageContent != null) {
            startPageContent.onPartClosed();
        }
    }

}
