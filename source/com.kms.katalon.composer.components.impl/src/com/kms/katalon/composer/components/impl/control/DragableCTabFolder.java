package com.kms.katalon.composer.components.impl.control;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DragableCTabFolder extends CTabFolder {

    private static final String DRAGED_ITEM_INDEX = "draggedItemIndex";

    private static final String SOURCE_FOLDER = "sourceFolder";

    private static final String IS_SHOWING = "isShowing";

    private static final String ITEM_CAPTION = "itemCaption";

    public int marginWidth = 10;

    public int marginHeight = 10;

    public DragableCTabFolder(Composite parent, int style) {
        super(parent, style | SWT.BORDER);
        this.setBorderVisible(true);
        enableDrag();
//        enablePopout();
    }
    
    private void enableDrag() {
        addDragListener(this);
        addDropListener(this);
    }
    
    private void enablePopout() {
         addPopoutListener(this);
    }

    private void addDragListener(final CTabFolder tabFolder) {
        final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();

        final DragSourceAdapter dragAdapter = new DragSourceAdapter() {
            private CTabItem item;

            @Override
            public void dragStart(DragSourceEvent event) {
                event.doit = tabFolder.getItemCount() != 0;
                Point cursorLocation = tabFolder.getDisplay().getCursorLocation();
                item = tabFolder.getItem(tabFolder.toControl(cursorLocation));
                event.doit = item != null;
            }

            @Override
            public void dragSetData(final DragSourceEvent event) {
                Control control = item.getControl();
                control.setData(ITEM_CAPTION, item.getText());
                control.setData(IS_SHOWING, tabFolder.getSelectionIndex() == tabFolder.indexOf(item));
                control.setData(SOURCE_FOLDER, tabFolder);
                control.setData(DRAGED_ITEM_INDEX, tabFolder.indexOf(item));

                transfer.setSelection(new StructuredSelection(control));

                item.setControl(null);
                item.dispose();
            }
        };

        final DragSource dragSource = new DragSource(tabFolder, DND.DROP_MOVE);
        dragSource.setTransfer(new Transfer[] { transfer });
        dragSource.addDragListener(dragAdapter);
    }

    private void addDropListener(final CTabFolder tabFolder) {
        final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();

        final DropTargetAdapter dragAdapter = new DropTargetAdapter() {
            private int indexToInsert;

            @Override
            public void dragOver(DropTargetEvent event) {
                if (tabFolder.getDisplay().getCursorControl() instanceof CTabFolder) {
                    event.detail = DND.DROP_MOVE;
                    CTabItem itemUnderCursor = tabFolder
                            .getItem(tabFolder.toControl(tabFolder.getDisplay().getCursorLocation()));
                    indexToInsert = tabFolder.indexOf(itemUnderCursor);
                    if (itemUnderCursor == null) {
                        // insert at the end
                        indexToInsert = tabFolder.getItemCount() - 1;
                        tabFolder.setInsertMark(indexToInsert, true);
                    } else {
                        Rectangle bounds = itemUnderCursor.getBounds();

                        int x = tabFolder.toControl(event.x, event.y).x;
                        int middle = bounds.x + (bounds.width / 2);

                        if (x <= middle) {
                            // insert before this tab
                            indexToInsert--;
                            tabFolder.setInsertMark(indexToInsert, false);
                        } else {
                            tabFolder.setInsertMark(indexToInsert, true);
                        }
                    }
                } else {
                    event.detail = DND.DROP_NONE;
                    tabFolder.setInsertMark(-1, true);
                }

                // Workaround for bug #32846
                if (indexToInsert == -1) {
                    tabFolder.redraw();
                }
            }

            @Override
            public void drop(final DropTargetEvent event) {
                Control droppedObj = (Control) ((StructuredSelection) transfer.getSelection()).getFirstElement();
                Object source = droppedObj.getData(SOURCE_FOLDER);
                final String itemCaption = (String) droppedObj.getData(ITEM_CAPTION);
                int dragedItemIndex = (int) droppedObj.getData(DRAGED_ITEM_INDEX);

                if (source == tabFolder) {
                    // dnd between same folder, old item is already disposed so item under cursorlocation is wrong
                    if (indexToInsert < dragedItemIndex) {
                        // move from right to left
                        indexToInsert++;
                    }
                } else {
                    // dnd between different folders
                    indexToInsert++;
                }
                createNewItem(tabFolder, indexToInsert, droppedObj, itemCaption,
                        (boolean) droppedObj.getData(IS_SHOWING));
            }

            private CTabItem createNewItem(final CTabFolder folder, int index, final Control droppedObj,
                    final String itemCaption, boolean select) {
                CTabItem item = new CTabItem(folder, SWT.NULL, index);
                item.setText(itemCaption);
                droppedObj.setParent(folder);
                item.setControl(droppedObj);
                if (select) {
                    folder.setSelection(item);
                }
                return item;
            }
        };

        final DropTarget dropTarget = new DropTarget(tabFolder, DND.DROP_MOVE);
        dropTarget.setTransfer(new Transfer[] { transfer });
        dropTarget.addDropListener(dragAdapter);
    }

    private void addPopoutListener(CTabFolder tabFolder) {
        Listener dragListener = new Listener() {
            private CTabItem dragItem;

            public void handleEvent(Event event) {
                Point mouseLocation = new Point(event.x, event.y);
                switch (event.type) {
                    case SWT.DragDetect: {
                        CTabItem item = tabFolder.getItem(mouseLocation);
                        if (dragItem == null && item != null) {
                            dragItem = item;
                            tabFolder.setCapture(true);
                        }
                        break;
                    }
                    case SWT.MouseUp: {
                        if (dragItem != null && !tabFolder.getBounds().contains(mouseLocation)) {
                            popOut(dragItem, tabFolder.toDisplay(mouseLocation));
                            dragItem.dispose();
                            dragItem = null;
                        }
                        break;
                    }
                }
            }
        };
        tabFolder.addListener(SWT.DragDetect, dragListener);
        tabFolder.addListener(SWT.MouseUp, dragListener);
    }

    private static void popOut(CTabItem tabItem, Point location) {
        Control control = tabItem.getControl();
        tabItem.setControl(null);
        Shell itemShell = new Shell(tabItem.getParent().getShell(), SWT.DIALOG_TRIM | SWT.RESIZE);
        itemShell.setLayout(new FillLayout());
        control.setParent(itemShell);
        control.setVisible(true); // control is hidden by tabItem.setControl( null ), make visible again
        itemShell.pack();
        itemShell.setLocation(location);
        itemShell.open();
    }
}
