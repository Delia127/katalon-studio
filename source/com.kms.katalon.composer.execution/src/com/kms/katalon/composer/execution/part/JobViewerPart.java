package com.kms.katalon.composer.execution.part;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.FocusableComposite;
import com.kms.katalon.composer.components.impl.control.GifCLabel;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.execution.constants.ImageConstants;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.launcher.IDEObservableLauncher;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.launcher.result.ILauncherResult;
import com.kms.katalon.execution.launcher.result.LauncherStatus;

public class JobViewerPart implements EventHandler {
    private static final Image IMG_DONE = ImageConstants.IMG_16_DONE;
    private static final Image IMG_WATCH = ImageConstants.IMG_16_WATCH;
    private static final Image IMG_STOP = ImageConstants.IMG_16_STOP;
    private static final Image IMG_WAIT = ImageConstants.IMG_16_WAIT;
    private static final Image IMG_TERMINATE = ImageConstants.IMG_16_TERMINATE;
    private static final Image IMG_PAUSE = ImageConstants.IMG_16_PAUSE;
    private static final Image IMG_PLAY = ImageConstants.IMG_16_PLAY;

    private Composite listCompositeLauncher;
    private Composite parentComposite;
    private ScrolledComposite scrolledComposite;
    private MPart mpart;

    private static final String CONTROL_ID = "launcherId";
    private static final String LAUNCHER_PROGRESS_BAR = "launcherProgressBar";
    private static final String LAUNCHER_PROGRESS_LABEL = "launcherProgressLabel";

    @Inject
    private IEventBroker eventBroker;

    @Inject
    EPartService partService;

    @Inject
    EModelService modelService;

    @Inject
    MApplication application;

    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        parentComposite = parent;
        this.mpart = mpart;
        registerListeners();
        updateToolItemStatus();

        parentComposite.setLayout(new FillLayout());
        //parentComposite.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

        scrolledComposite = new ScrolledComposite(parentComposite, SWT.V_SCROLL);
        scrolledComposite.setBackground(ColorUtil.getCompositeBackgroundColor());
        scrolledComposite.setBackgroundMode(SWT.INHERIT_FORCE);

        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        listCompositeLauncher = new Composite(scrolledComposite, SWT.NONE);
        scrolledComposite.setContent(listCompositeLauncher);

        //listCompositeLauncher.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout gl_composite_listLauncher = new GridLayout(1, false);
        gl_composite_listLauncher.marginHeight = 0;
        gl_composite_listLauncher.marginWidth = 0;
        listCompositeLauncher.setLayout(gl_composite_listLauncher);

        scrolledComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(listCompositeLauncher.computeSize(r.width, SWT.DEFAULT));
            }
        });
        // draw();

    }

    private void updateToolItemStatus() {
        try {
            ProjectEntity project = ProjectController.getInstance().getCurrentProject();
            for (MToolBarElement toolbarElement : mpart.getToolbar().getChildren()) {
                MDirectToolItem toolItem = (MDirectToolItem) toolbarElement;
                switch (toolItem.getElementId()) {
                    case "com.kms.katalon.composer.execution.directtoolitem.removeAllTerminated":
                        toolItem.setEnabled((project != null)
                                && (LauncherManager.getInstance().isAnyLauncherTerminated()));
                        break;
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

    }

    private void registerListeners() {
        eventBroker.subscribe(EventConstants.JOB_REFRESH, this);
        eventBroker.subscribe(EventConstants.JOB_UPDATE_PROGRESS, this);
    }
    
    private void createLauncherIdComposite(Composite parent, final IDEObservableLauncher launcher) {
        Composite launcherIdComposite = new FocusableComposite(parent, SWT.NONE);
        launcherIdComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        boolean isDebugMode = launcher.getMode() == LaunchMode.DEBUG;
        GridLayout glLauncherId = new GridLayout(isDebugMode ? 2 : 1, false);
        glLauncherId.marginWidth = 0;
        glLauncherId.marginHeight = 0;
        launcherIdComposite.setLayout(glLauncherId);
        if (isDebugMode) {
            Label lblDebugImage = new Label(launcherIdComposite, SWT.NONE);
            lblDebugImage.setImage(ImageConstants.IMG_16_DEBUG);
            lblDebugImage.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        }
        Label lblId = new Label(launcherIdComposite, SWT.WRAP);
        lblId.setText(launcher.getName());
        lblId.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
    }

    private void createJobComposite(Composite composite, final IDEObservableLauncher launcher) throws Exception {
        final Composite compositeLauncher = new FocusableComposite(composite, SWT.BORDER);
        compositeLauncher.setData(CONTROL_ID, launcher.getId());
        compositeLauncher.setBackground(ColorUtil.getCompositeBackgroundColor());
        compositeLauncher.setBackgroundMode(SWT.INHERIT_FORCE);

        compositeLauncher.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        //compositeLauncher.setBackground(ColorUtil.getWhiteBackgroundColor());
        
        GridLayout glCompositeLauncher = new GridLayout(3, false);
        glCompositeLauncher.marginHeight = 0;
        compositeLauncher.setLayout(glCompositeLauncher);

        Label lblWatched = new Label(compositeLauncher, SWT.NONE);
        GridData gd_lblWatched = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblWatched.widthHint = 30;
        lblWatched.setLayoutData(gd_lblWatched);
        lblWatched.setImage(IMG_WATCH);
        if (!launcher.isObserved()) {
            lblWatched.setVisible(false);
        }

        createLauncherIdComposite(compositeLauncher, launcher);
        
        Label lblProgressStatus = new Label(compositeLauncher, SWT.WRAP);
        lblProgressStatus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblProgressStatus.setText(launcher.getResult().getExecutedTestCases() + "/" + launcher.getResult().getTotalTestCases());
        
        lblProgressStatus.setData(CONTROL_ID, LAUNCHER_PROGRESS_LABEL);

        if (launcher.getStatus() == LauncherStatus.RUNNING || launcher.getStatus() == LauncherStatus.SENDING_REPORT 
                || launcher.getStatus() == LauncherStatus.PREPARE_REPORT || launcher.getStatus() == LauncherStatus.UPLOAD_REPORT) {
            final GifCLabel lblLauncherStatus = new GifCLabel(compositeLauncher, SWT.NONE);
            lblLauncherStatus.setGifImage(ImageConstants.URL_16_LOADING.openStream());

            GridData gd_lblLauncherStatus = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_lblLauncherStatus.widthHint = 30;
            lblLauncherStatus.setLayoutData(gd_lblLauncherStatus);
        } else {
            Label lblLauncherStatus = new Label(compositeLauncher, SWT.NONE);
            GridData gd_lblLauncherStatus = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
            gd_lblLauncherStatus.widthHint = 30;
            lblLauncherStatus.setLayoutData(gd_lblLauncherStatus);

            switch (launcher.getStatus()) {
                case DONE:
                    lblLauncherStatus.setImage(IMG_DONE);
                    break;
                case SUSPENDED:
                    break;
                case TERMINATED:
                    lblLauncherStatus.setImage(IMG_TERMINATE);
                    break;
                case WAITING:
                    lblLauncherStatus.setImage(IMG_WAIT);
                    break;
                default:
                    break;
            }
        }

        Label lblStatus = new Label(compositeLauncher, SWT.NONE);
        GridData gdLblStatus = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdLblStatus.heightHint = 9;

        lblStatus.setText(launcher.getDisplayMessage());
        new Label(compositeLauncher, SWT.NONE);

        if (launcher.getStatus() == LauncherStatus.RUNNING || launcher.getStatus() == LauncherStatus.WAITING
                || launcher.getStatus() == LauncherStatus.SUSPENDED) {
            new Label(compositeLauncher, SWT.NONE);
            ProgressBar progressBar = new ProgressBar(compositeLauncher, SWT.BORDER | SWT.SMOOTH);
            GridData gd_progressBar = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
            gd_progressBar.heightHint = 12;
            progressBar.setLayoutData(gd_progressBar);
            progressBar.setMinimum(0);
            progressBar.setMaximum(launcher.getResult().getTotalTestCases());
            progressBar.setSelection(launcher.getResult().getExecutedTestCases());
            progressBar.setData(CONTROL_ID, LAUNCHER_PROGRESS_BAR);
            if (launcher.getStatus() == LauncherStatus.SUSPENDED) {
                progressBar.setState(SWT.PAUSED);
            }

            Composite compositeLauncherToolbar = new Composite(compositeLauncher, SWT.NONE);
            GridLayout glCompositeLauncherToolbar = new GridLayout(1, false);
            glCompositeLauncherToolbar.marginWidth = 0;
            glCompositeLauncherToolbar.marginHeight = 0;
            compositeLauncherToolbar.setLayout(glCompositeLauncherToolbar);
            compositeLauncherToolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

            ToolBar toolBar = new ToolBar(compositeLauncherToolbar, SWT.FLAT | SWT.RIGHT);
            toolBar.setForeground(ColorUtil.getToolBarForegroundColor());
            toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

            ToolItem tltmStop = new ToolItem(toolBar, SWT.NONE);
            tltmStop.setImage(IMG_STOP);
            if (launcher.getStatus() == LauncherStatus.TERMINATED || launcher.getStatus() == LauncherStatus.DONE) {
                tltmStop.setEnabled(false);
            }

            tltmStop.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    launcher.stop();
                    compositeLauncher.setFocus();
                }
            });

            ToolItem tltmPause = new ToolItem(toolBar, SWT.NONE);

            if (launcher.getStatus() == LauncherStatus.SUSPENDED) {
                tltmPause.setImage(IMG_PLAY);
                tltmPause.setToolTipText(StringConstants.PA_LOG_RESUME);
            } else if (launcher.getStatus() == LauncherStatus.RUNNING) {
                tltmPause.setImage(IMG_PAUSE);
                tltmPause.setToolTipText(StringConstants.PA_LOG_PAUSE);
            }

            if (launcher.getMode() == LaunchMode.RUN) {
                tltmPause.setEnabled(false);
            }

            tltmPause.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (launcher.getStatus() == LauncherStatus.RUNNING) {
                        launcher.suspend();
                    } else {
                        launcher.resume();
                    }

                    compositeLauncher.setFocus();
                }
            });
        } else {
            glCompositeLauncher.marginBottom = 5;
        }

        compositeLauncher.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));

        compositeLauncher.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {

            }

            @SuppressWarnings("restriction")
            @Override
            public void focusGained(FocusEvent e) {
                try {
                    if (!launcher.isObserved()) {
                        eventBroker.send(EventConstants.CONSOLE_LOG_RESET, launcher.getId());
                        eventBroker.send(EventConstants.JOB_REFRESH, null);
                    }
                } catch (Exception ex) {
                    LoggerSingleton.getInstance().getLogger().error(ex);
                }
            }
        });
        
        ControlUtils.recursivelyAddMouseListener(compositeLauncher, new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent event) {
                if (compositeLauncher != null && !compositeLauncher.isDisposed()) {
                    compositeLauncher.setFocus();
                }
            }
        });
    }

    private void draw() {
        try {
            for (ILauncher launcher : LauncherManager.getInstance().getAllLaunchers()) {
                if (launcher instanceof IDEObservableLauncher) {
                    createJobComposite(listCompositeLauncher, (IDEObservableLauncher) launcher);
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        listCompositeLauncher.setSize(listCompositeLauncher.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        Rectangle r = scrolledComposite.getClientArea();
        scrolledComposite.setMinSize(listCompositeLauncher.computeSize(r.width, SWT.DEFAULT));
    }

    @Override
    public void handleEvent(Event event) {
        try {
            if (event.getTopic().equals(EventConstants.JOB_REFRESH)) {
                while (listCompositeLauncher.getChildren().length > 0) {
                    listCompositeLauncher.getChildren()[0].dispose();
                }
                draw();
                updateToolItemStatus();
            } else if (event.getTopic().equals(EventConstants.JOB_UPDATE_PROGRESS)) {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object == null || !(object instanceof String)) { return; }

                for (Control control : listCompositeLauncher.getChildren()) {
                    if (control.getData(CONTROL_ID) == null) { continue; }

                    String launcherCompositeId = (String) control.getData(CONTROL_ID);

                    if (!launcherCompositeId.equals(object)) { continue; }

                    ILauncher launcher = LauncherManager.getInstance().getLauncherInRunningList(
                            launcherCompositeId);
                    if (launcher == null) { continue; }

                    Composite laucherComposite = (Composite) control;
                    for (Control launcherControl : laucherComposite.getChildren()) {
                        if (launcherControl.getData(CONTROL_ID) == null) continue;

                        String dataId = (String) launcherControl.getData(CONTROL_ID);

                        ILauncherResult result = launcher.getResult();
                        if (dataId.equals(LAUNCHER_PROGRESS_LABEL)) {
                            
                            Label progressLabel = (Label) launcherControl;
                            progressLabel.setText(result.getExecutedTestCases() + "/"
                                    + result.getTotalTestCases());
                            progressLabel.pack();

                        } else if (dataId.equals(LAUNCHER_PROGRESS_BAR)) {
                            ProgressBar progressBar = (ProgressBar) launcherControl;
                            progressBar.setSelection(result.getTotalTestCases());
                        }
                    }
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
