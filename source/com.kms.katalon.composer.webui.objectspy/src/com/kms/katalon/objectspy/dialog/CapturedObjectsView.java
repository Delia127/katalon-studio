package com.kms.katalon.objectspy.dialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolTip;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebElement.WebElementType;
import com.kms.katalon.objectspy.element.WebFrame;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.element.tree.WebElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.WebElementTreeContentProvider;
import com.kms.katalon.util.listener.EventListener;
import com.kms.katalon.util.listener.EventManager;

public class CapturedObjectsView extends Composite implements EventHandler, EventManager<ObjectSpyEvent> {

    private TreeViewer treeViewer;

    private WebElement selectedObject;

    private Label lblInfo;

    private ToolTip infoTooltip;

    private IEventBroker eventBroker;

    private Map<ObjectSpyEvent, Set<EventListener<ObjectSpyEvent>>> eventListeners = new HashMap<>();

    public CapturedObjectsView(Composite parent, int style, IEventBroker eventBroker) {
        super(parent, style);

        this.eventBroker = eventBroker;

        setLayoutAndLayoutData();

        createControls();

        addControlListeners();

        subscribeEvents();
    }

    private void setLayoutAndLayoutData() {
        GridLayout mainLayout = new GridLayout();
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        setLayout(mainLayout);
        GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
        layoutData.heightHint = 150;
        setLayoutData(layoutData);
    }

    private void createControls() {
        Composite capturedObjectsComposite = new Composite(this, SWT.NONE);
        capturedObjectsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        GridLayout glCapturedObjectsComposite = new GridLayout(2, false);
        glCapturedObjectsComposite.marginWidth = 0;
        glCapturedObjectsComposite.marginHeight = 0;
        capturedObjectsComposite.setLayout(glCapturedObjectsComposite);

        Label lblCapturedObjects = new Label(capturedObjectsComposite, SWT.NONE);
        lblCapturedObjects.setFont(ControlUtils.getFontBold(lblCapturedObjects));
        lblCapturedObjects.setText(StringConstants.DIA_LBL_CAPTURED_OBJECTS);

        lblInfo = new Label(capturedObjectsComposite, SWT.NONE);

        GridData gdLblInfo = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gdLblInfo.heightHint = 16;
        lblInfo.setLayoutData(gdLblInfo);
        lblInfo.setImage(ImageConstants.IMG_16_HELP);

        infoTooltip = new ToolTip(this.getShell(), SWT.BALLOON);
        infoTooltip.setMessage(StringConstants.TOOLTIP_CAPTURED_OBJECTS_HELP);

        treeViewer = new TreeViewer(this, SWT.BORDER | SWT.MULTI);
        treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        treeViewer.setContentProvider(new WebElementTreeContentProvider());
        treeViewer.setLabelProvider(new WebElementLabelProvider());
    }

    private void addControlListeners() {
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!(event.getSelection() instanceof TreeSelection)) {
                    return;
                }

                TreeSelection treeSelection = (TreeSelection) event.getSelection();
                Object selection = treeSelection.getFirstElement();
                if (!(selection instanceof WebElement)) {
                    return;
                }

                invoke(ObjectSpyEvent.SELECTED_ELEMENT_CHANGED, selection);
            }
        });
        treeViewer.addDragSupport(DND.DROP_MOVE, new Transfer[] { LocalSelectionTransfer.getTransfer() },
                new DragSourceAdapter() {

                    @SuppressWarnings("unchecked")
                    private boolean isDragable(IStructuredSelection selection) {
                        if (selection == null) {
                            return false;
                        }

                        List<WebElement> webElements = (List<WebElement>) selection.toList();
                        Optional<WebElement> optionalWebPage = webElements.stream()
                                .filter(element -> element instanceof WebPage)
                                .findFirst();
                        return !optionalWebPage.isPresent();
                    }

                    @Override
                    public void dragSetData(DragSourceEvent event) {
                        IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

                        if (!isDragable(selection)) {
                            event.detail = DND.DROP_NONE;
                            event.doit = false;
                            return;
                        }

                        LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
                        if (transfer.isSupportedType(event.dataType)) {
                            transfer.setSelection(selection);
                            transfer.setSelectionSetTime(event.time & 0xFFFF);
                        }
                    }
                });
        treeViewer.addDropSupport(DND.DROP_MOVE, new Transfer[] { LocalSelectionTransfer.getTransfer() },
                new ViewerDropAdapter(treeViewer) {

                    @Override
                    public boolean validateDrop(Object target, int operation, TransferData transferType) {
                        if (target == null) {
                            return false;
                        }
                        LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
                        return getCurrentLocation() == LOCATION_ON && target instanceof WebElement
                                && transfer.isSupportedType(transferType);
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public boolean performDrop(Object data) {
                        IStructuredSelection selection = (IStructuredSelection) LocalSelectionTransfer.getTransfer()
                                .getSelection();
                        if (selection == null) {
                            return false;
                        }
                        List<WebElement> webElements = (List<WebElement>) selection.toList();
                        WebElement target = (WebElement) getCurrentTarget();
                        WebFrame parent = null;
                        if (target.getType() == WebElementType.ELEMENT) {
                            parent = target.getParent();
                        } else {
                            parent = (WebFrame) target;
                        }
                        for (WebElement element : webElements) {
                            WebElement newWebElement = element.softClone();
                            newWebElement.setParent(parent);
                            // update captured objects in RecorderDialog Action table
                            eventBroker.send(EventConstants.RECORDER_ACTION_OBJECT_REORDERED,
                                    new WebElement[] { element, newWebElement });
                        }

                        try {
                            // Ensure things happened correctly
                            TimeUnit.MILLISECONDS.sleep(500);
                        } catch (InterruptedException e) {
                            LoggerSingleton.logError(e);
                        }

                        // remove the previous order
                        for (WebElement element : webElements) {
                            element.getParent().getChildren().remove(element);
                        }
                        treeViewer.refresh();
                        LocalSelectionTransfer.getTransfer().setSelection(null);
                        return true;
                    }
                });

        lblInfo.addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseEnter(MouseEvent e) {
                Point location = e.display.getCursorLocation();
                infoTooltip.setVisible(true);
                infoTooltip.setLocation(location.x, location.y - e.y + lblInfo.getSize().y);
            }

            @Override
            public void mouseExit(MouseEvent e) {
                infoTooltip.setVisible(false);
            }
        });
    }

    private void setTreeDataInput(Object input) {
        if (treeViewer != null && ControlUtils.isReady(treeViewer.getControl())) {
            treeViewer.setInput(input);
        }
    }

    public void setInput(WebPage[] input) {
        setTreeDataInput(input);
    }

    public void setInput(List<WebPage> input) {
        setTreeDataInput(input);
    }

    public WebElement getSelectedObject() {
        return selectedObject;
    }

    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    public IStructuredSelection getSelection() {
        return treeViewer.getStructuredSelection();
    }

    public void refreshTree(Object object) {
        treeViewer.getControl().setRedraw(false);
        Object[] expandedElements = treeViewer.getExpandedElements();
        treeViewer.refresh(object);
        for (Object element : expandedElements) {
            treeViewer.setExpandedState(element, true);
        }
        treeViewer.getControl().setRedraw(true);
    }

    private void subscribeEvents() {
        eventBroker.subscribe(EventConstants.RECORDER_ACTION_SELECTED, this);
    }

    private void unsubscribeEvents() {
        eventBroker.unsubscribe(this);

    }

    @Override
    public void dispose() {
        unsubscribeEvents();
        super.dispose();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    @Override
    public Iterable<EventListener<ObjectSpyEvent>> getListeners(ObjectSpyEvent event) {
        return eventListeners.get(event);
    }

    @Override
    public void addListener(EventListener<ObjectSpyEvent> listener, Iterable<ObjectSpyEvent> events) {
        events.forEach(e -> {
            Set<EventListener<ObjectSpyEvent>> listenerOnEvent = eventListeners.get(e);
            if (listenerOnEvent == null) {
                listenerOnEvent = new HashSet<>();
            }
            listenerOnEvent.add(listener);
            eventListeners.put(e, listenerOnEvent);
        });
    }

    @Override
    public void handleEvent(Event event) {
        String eventType = event.getTopic();
        if (eventType == EventConstants.RECORDER_ACTION_SELECTED) {
            WebElement selectedElement = (WebElement) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            treeViewer.setSelection(new StructuredSelection(selectedElement), true);
        }
    }

}
