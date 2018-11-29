package com.kms.katalon.composer.explorer.custom;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.constants.ImageConstants;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.composer.explorer.parts.ExplorerPart;

public class SearchDropDownBox extends Composite {
	private static final String SEARCH_ALL = StringConstants.CUS_SEARCH_ALL;
	private static final Image IMG_SEARCH_ALL = ImageConstants.IMG_16_SEARCH_ALL;

	private Shell popup;
	private Listener listener;
	private Table table;
	private TableColumn imageColumn;
	private TableColumn labelColumn;
	private CLabel selectedLabel;
	private CLabel arrowLabel;
	private ExplorerPart viewPart;
	private int selectionIndex = -1;
    private boolean selectionEventDisabled = false;
	
	public SearchDropDownBox(Composite parent, int style, ExplorerPart viewPart) {
		super(parent, style);
		this.viewPart = viewPart;
		// create label
		selectedLabel = new CLabel(this, SWT.NONE);
		selectedLabel.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));

		arrowLabel = new CLabel(this, SWT.NONE);
		arrowLabel.setImage(ImageConstants.IMG_16_ARROW_DOWN);
		arrowLabel.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));

		listener = new Listener() {
			public void handleEvent(Event event) {
				if (isDisposed())
					return;
				if (selectedLabel == event.widget || arrowLabel == event.widget) {
					labelEvent(event);
					return;
				}

				if (popup == event.widget) {
					popupEvent(event);
					return;
				}

				if (table == event.widget) {
					tableEvent(event);
				}

				if (SearchDropDownBox.this == event.widget) {
					comboEvent(event);
				}
			}
		};

		int[] comboEvents = { SWT.Resize };
		for (int i = 0; i < comboEvents.length; i++) {
			this.addListener(comboEvents[i], listener);
		}

		int[] labelEvents = { SWT.MouseDown };
		for (int i = 0; i < labelEvents.length; i++) {
			selectedLabel.addListener(labelEvents[i], listener);
			arrowLabel.addListener(labelEvents[i], listener);
		}
		createPopup();
	}

	private void labelEvent(Event event) {
		switch (event.type) {
		case SWT.MouseDown:
			if (popup != null) {
				dropDown(!isDropped());
			}
			break;
		}
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();
		Point labelSize = selectedLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		Point arrowSize = arrowLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
		int borderWidth = getBorderWidth();
		return new Point(labelSize.x + arrowSize.x + 2 * borderWidth + 1, labelSize.y + 2 * borderWidth);
	}

	private void comboEvent(Event event) {
		switch (event.type) {
		case SWT.Resize:
			internalLayout(false);
			break;
		}
	}

	private void popupEvent(Event event) {
		switch (event.type) {
		case SWT.Paint:
			// draw black rectangle around table
			Rectangle listRect = table.getBounds();
			Color black = ColorUtil.getCompositeBackgroundColor();
			event.gc.setForeground(black);
			event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height + 1);
			break;
		case SWT.Close:
			event.doit = false;
			dropDown(false);
			break;
		case SWT.Hide:
		    dropDown(false);
		    break;
		}
	}

	public void createPopup() {
		popup = new Shell(getShell(), SWT.NO_TRIM | SWT.ON_TOP);
		int[] popupEvents = { SWT.Close, SWT.Paint, SWT.Hide };
		for (int i = 0; i < popupEvents.length; i++)
			popup.addListener(popupEvents[i], listener);

		table = new Table(popup, SWT.BORDER | SWT.FULL_SELECTION);
		// Set Search drop-down background
		table.setBackground(ColorUtil.getDefaultBackgroundColor());
		// Split-up image and label into 2 columns
		imageColumn = new TableColumn(table, SWT.CENTER);
		labelColumn = new TableColumn(table, SWT.NONE);
		
		int[] tableEvents = { SWT.Selection, SWT.FocusIn, SWT.FocusOut, SWT.MouseUp };
		for (int i = 0; i < tableEvents.length; i++)
			table.addListener(tableEvents[i], listener);

		clearInput();
	}

	private void updateLabelBackground(boolean isDroped) {
		if (isDroped) {
			selectedLabel.setBackground(ColorUtil.getDefaultBackgroundColor());
			arrowLabel.setBackground(ColorUtil.getDefaultBackgroundColor());
		} else {
			selectedLabel.setBackground(this.getBackground());
			arrowLabel.setBackground(this.getBackground());
		}
	}

	private void tableEvent(Event event) {
		switch (event.type) {
		case SWT.Selection:
			if (!selectionEventDisabled) {
	            refreshLabel();
	            Event e = new Event();
	            e.time = event.time;
	            e.stateMask = event.stateMask;
	            e.doit = event.doit;
	            notifyListeners(SWT.Selection, e);
	            event.doit = e.doit;
                viewPart.filterTreeEntitiesByType();
			}
			break;
		case SWT.FocusIn:
			break;
		case SWT.FocusOut:
			if (!"carbon".equals(SWT.getPlatform())) {
				Point labelPoint = selectedLabel.toControl(getDisplay().getCursorLocation());
				Point labelSize = selectedLabel.getSize();

				Point arrowPoint = arrowLabel.toControl(getDisplay().getCursorLocation());
				Point arrowSize = arrowLabel.getSize();
				Rectangle rect = new Rectangle(0, 0, labelSize.x, labelSize.y);
				Rectangle arrowRect = new Rectangle(0, 0, arrowSize.x, arrowSize.y);

				if (rect.contains(labelPoint) || arrowRect.contains(arrowPoint)) {
					boolean comboShellActivated = getDisplay().getActiveShell() == getShell();
					if (!comboShellActivated)
						dropDown(false);
					break;
				}
			}
			dropDown(false);
			break;
		case SWT.MouseUp:
			dropDown(false);
			break;
		}
	}

	private void internalLayout(boolean changed) {
		if (isDropped())
			dropDown(false);
		Rectangle rect = getClientArea();
		int height = rect.height;
		Point labelSize = selectedLabel.computeSize(SWT.DEFAULT, height, changed);
		Point arrowSize = arrowLabel.computeSize(SWT.DEFAULT, height, changed);
		selectedLabel.setBounds(0, 0, labelSize.x, height);
		arrowLabel.setBounds(labelSize.x, 0, arrowSize.x, arrowSize.y);
	}

	@SuppressWarnings("restriction")
	private void updateTableItems() {
		try {
			table.removeAll();
			// create search all item
			TableItem itemAll = new TableItem(table, SWT.NONE);
			itemAll.setImage(0, IMG_SEARCH_ALL);
			itemAll.setText(1, SEARCH_ALL);
			for (ITreeEntity treeEntity : viewPart.getSearchDropBoxElements()) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				tableItem.setImage(0, treeEntity.getEntryImage());
				tableItem.setText(1, treeEntity.getCopyTag());
			}
			
			table.setSelection(selectionIndex >= 0 ? selectionIndex : 0);
			imageColumn.pack();
			labelColumn.pack();
		} catch (Exception e) {
			LoggerSingleton.getInstance().getLogger().error(e);
		}
	}

	private void dropDown(boolean drop) {
		updateLabelBackground(drop);
		if (!drop) {
			popup.setVisible(false);
		} else {
		    selectionEventDisabled = true;
			updateTableItems();
			Point tableSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			// Remove extra row-column space
            if (!Platform.OS_MACOSX.equals(Platform.getOS())) {
                tableSize.x -= IMG_SEARCH_ALL.getBounds().width;
            }
            tableSize.y -= IMG_SEARCH_ALL.getBounds().height;
			// Set table bounds
			table.setBounds(1, 1, tableSize.x + 8, tableSize.y + 6);

			Point comboSize = getSize();
			Display display = getDisplay();
			Rectangle parentRect = display.map(getParent(), null, getBounds());
			popup.setBounds(parentRect.x - 3, comboSize.y + parentRect.y + 1, tableSize.x + 10, tableSize.y + 8);
			popup.setVisible(true);

			table.setFocus();
			selectionEventDisabled = false;
		}
	}

	@Override
	public void redraw() {
		super.redraw();
		selectedLabel.redraw();
		arrowLabel.redraw();
		if (popup.isVisible())
			table.redraw();
	}

	private boolean isDropped() {
		return !isDisposed() && popup.getVisible();
	}

	@Override
	public boolean isFocusControl() {
		checkWidget();
		if (selectedLabel.isFocusControl() || arrowLabel.isFocusControl() || table.isFocusControl()
				|| popup.isFocusControl()) {
			return true;
		}
		return super.isFocusControl();
	}

	public int getSelectionIndex() {
		return selectionIndex;
	}

	private void refreshLabel() {
		Display.getCurrent().syncExec(new Runnable() {
			@Override
			public void run() {
				if (!table.isDisposed()) {
					selectionIndex = table.getSelectionIndex();
					selectedLabel.setImage(table.getItem(selectionIndex).getImage(0));
					selectedLabel.setToolTipText(table.getItem(selectionIndex).getText(1));
					selectedLabel.pack();
					selectedLabel.getParent().layout();
					selectedLabel.getParent().getParent().layout();
				} else {
					Thread.currentThread().interrupt();
				}
			}
		});
	}

	public void clearInput() {
		table.removeAll();
		// create search all item
		TableItem itemAll = new TableItem(table, SWT.NONE);
		itemAll.setImage(0, IMG_SEARCH_ALL);
		itemAll.setText(1, SEARCH_ALL);
		table.select(0);
		refreshLabel();
	}
}
