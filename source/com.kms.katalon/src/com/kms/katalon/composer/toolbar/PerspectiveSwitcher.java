package com.kms.katalon.composer.toolbar;

import java.net.MalformedURLException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ImageUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

public class PerspectiveSwitcher {

    private static final String ECLIPSE_MAIN_TOOLBAR_ID = "org.eclipse.ui.main.toolbar";

    @Inject
    IEventBroker eventBroker;

    @Inject
    EModelService modelService;

    @Inject
    private MWindow window;

    @Inject
    private EPartService partService;

    @Inject
    private MApplication application;

    private MToolControl perspectiveToolControl;

    private ToolBar toolbar;

    private void registerEvent() {
        eventBroker.subscribe(UIEvents.ElementContainer.TOPIC_SELECTEDELEMENT, selectionHandler);
    }

    private EventHandler selectionHandler = new EventHandler() {
        @Override
        public void handleEvent(Event event) {
            if (toolbar.isDisposed()) {
                return;
            }

            MUIElement changedElement = (MUIElement) event.getProperty(UIEvents.EventTags.ELEMENT);
            if (perspectiveToolControl == null || !(changedElement instanceof MPerspectiveStack)) {
                return;
            }

            MWindow perspWin = modelService.getTopLevelWindowFor(changedElement);
            MWindow switcherWin = modelService.getTopLevelWindowFor(perspectiveToolControl);
            if (perspWin != switcherWin) {
                return;
            }

            MPerspectiveStack perspStack = (MPerspectiveStack) changedElement;
            if (!perspStack.isToBeRendered()) {
                return;
            }

            MPerspective selElement = perspStack.getSelectedElement();
            for (ToolItem ti : toolbar.getItems()) {
                ti.setSelection(ti.getData() == selElement);
            }
        }
    };

    private void addPerspectiveItem(MPerspective perspective) {
        ToolItem tltmNewItem = new ToolItem(toolbar, SWT.CHECK);
        tltmNewItem.setToolTipText(perspective.getTooltip());
        tltmNewItem.setText(perspective.getLabel());
        try {
            tltmNewItem.setImage(ImageUtil.loadImage(perspective.getIconURI()));
        } catch (MalformedURLException e) {
            LoggerSingleton.logError(e);
        }
        tltmNewItem.setData(perspective);
        tltmNewItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ToolItem selectedItem = (ToolItem) e.getSource();
                if (!selectedItem.getSelection()) {
                    selectedItem.setSelection(true);
                    return;
                }
                activePerspective(selectedItem);
            }
        });
    }

    /**
     * Active a perspective that is data of the given toolItem
     * 
     * @param toolItem
     */
    private void activePerspective(ToolItem toolItem) {
        for (ToolItem childItem : toolbar.getItems()) {
            childItem.setSelection(childItem.equals(toolItem));
        }

        MPerspective perspective = (MPerspective) toolItem.getData();

        MPerspectiveStack perspectiveStack = find(IdConstants.MAIN_PERSPECTIVE_STACK_ID, window);

        if (perspectiveStack == null) {
            return;
        }
        perspectiveStack.setSelectedElement(perspective);

        // remove redundancy tool items
        MTrimBar toolControl = find(ECLIPSE_MAIN_TOOLBAR_ID, application);
        if (toolControl != null) {
            toolControl.getChildren().removeAll(toolControl.getPendingCleanup());
        }

        if (IdConstants.KEYWORD_PERSPECTIVE_ID.equals(perspective.getElementId())) {
            eventBroker.post(EventConstants.EXPLORER_RELOAD_DATA, false);
            return;
        }

        if (IdConstants.DEBUG_PERSPECTIVE_ID.equals(perspective.getElementId())) {
            reopenPartsIfClosed(perspective);
        }
    }

    public void reopenPartsIfClosed(MPerspective perspective) {
        showPart(IdConstants.ECLIPSE_DEBUG_PART_ID, perspective);
        showPart(IdConstants.ECLIPSE_VARIABLE_PART_ID, perspective);
        showPart(IdConstants.ECLIPSE_BREAKPOINT_PART_ID, perspective);
        showPart(IdConstants.ECLIPSE_EXPRESSION_PART_ID, perspective);

        MPlaceholder consolePlaceholder = find(IdConstants.ECLIPSE_CONSOLE_PART_ID, perspective);
        if (consolePlaceholder != null && !consolePlaceholder.isToBeRendered()) {
            partService.showPart(consolePlaceholder.getElementId(), PartState.ACTIVATE);
        }
    }

    private void showPart(String partId, MPerspective perspective) {
        MPart part = find(partId, perspective);
        if (part != null && part.getCurSharedRef() != null && !part.getCurSharedRef().isToBeRendered()) {
            partService.showPart(part.getElementId(), PartState.ACTIVATE);
        }
    }

    @PostConstruct
    void createWidget(Composite parent, MToolControl toolControl) {
        PartServiceSingleton.getInstance().setPartService(partService);
        application.getContext().set(EPartService.class, partService);

        perspectiveToolControl = toolControl;
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));

        Label label = new Label(container, SWT.SEPARATOR | SWT.VERTICAL);
        GridData gdLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLabel.heightHint = 24;
        label.setLayoutData(gdLabel);
        toolbar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());

        MPerspectiveStack perspectiveStack = find(IdConstants.MAIN_PERSPECTIVE_STACK_ID, window);
        if (perspectiveStack == null) {
            return;
        }

        // Create an item for each perspective that should show up
        for (MPerspective perspective : perspectiveStack.getChildren()) {
            if (perspective.isToBeRendered()) {
                addPerspectiveItem(perspective);
            }
        }
        initialPerspectiveSelection();
        registerEvent();
    }

    private void initialPerspectiveSelection() {
        MPerspective activePerspective = modelService.getActivePerspective(window);
        if (activePerspective == null) {
            toolbar.getItems()[0].setSelection(true);
            return;
        }

        for (ToolItem item : toolbar.getItems()) {
            item.setSelection(item.getText().equals(activePerspective.getLabel()));
        }
    }

    public ToolBar getToolbar() {
        return toolbar;
    }

    @SuppressWarnings("unchecked")
    public <T> T find(String elementId, MUIElement where) {
        return (T) modelService.find(elementId, where);
    }

}