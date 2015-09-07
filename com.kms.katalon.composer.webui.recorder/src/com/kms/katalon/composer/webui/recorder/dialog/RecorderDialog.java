package com.kms.katalon.composer.webui.recorder.dialog;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.webui.recorder.action.HTMLAction;
import com.kms.katalon.composer.webui.recorder.constants.ImageConstants;
import com.kms.katalon.composer.webui.recorder.constants.StringConstants;
import com.kms.katalon.composer.webui.recorder.core.HTMLElementRecorderServer;
import com.kms.katalon.composer.webui.recorder.core.RecordSession;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;

@SuppressWarnings("restriction")
public class RecorderDialog extends Dialog implements EventHandler {

	private static final String TABLE_COLUMN_ELEMENT_TITLE = StringConstants.DIA_COL_ELEMENT;
	private static final String TABLE_COLUMN_ACTION_DATA_TITLE = StringConstants.DIA_COL_ACTION_DATA;
	private static final String TABLE_COLUMN_ACTION_TITLE = StringConstants.DIA_COL_ACTION;
	private static final String TABLE_COLUMN_NO_TITLE = StringConstants.DIA_COL_NO;
	private static final String RESUME_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_RESUME;
	private static final String STOP_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_STOP;
	private static final String PAUSE_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_PAUSE;
	private static final String START_TOOL_ITEM_LABEL = StringConstants.DIA_TOOLITEM_START;

	private HTMLElementRecorderServer server;
	private Logger logger;
	private IEventBroker eventBroker;
	private List<HTMLPageElement> elements;
	private List<HTMLAction> actions;
	private boolean isPausing;
	private TableViewer actionTableViewer;
	private ToolBar toolBar;
	private ToolItem toolItemBrowserDropdown, tltmPause, tltmStop;
	private RecordSession session;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public RecorderDialog(Shell parentShell, Logger logger, IEventBroker eventBroker) {
		super(parentShell);
		setShellStyle(SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
		this.logger = logger;
		this.eventBroker = eventBroker;
		eventBroker.subscribe(EventConstants.RECORDER_ELEMENT_ADDED, this);
		elements = new ArrayList<HTMLPageElement>();
		actions = new ArrayList<HTMLAction>();
		isPausing = false;
	}

	private void startBrowser(WebUIDriverType webUiDriverType) {
		try {
			if (server != null && server.isRunning()) {
				server.stop();
			}
			server = new HTMLElementRecorderServer(logger, eventBroker);
			server.start();

			if (session != null) {
				session.stop();
			}
			session = new RecordSession(server.getServerUrl(), webUiDriverType, ProjectController.getInstance()
					.getCurrentProject(), logger);
			new Thread(session).start();

			tltmPause.setEnabled(true);
			tltmStop.setEnabled(true);
			resume();

			elements.clear();
			actions.clear();
			actionTableViewer.refresh();
		} catch (Exception e) {
			logger.error(e);
			MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
		}
	}

	class DropdownSelectionListener extends SelectionAdapter {
		private Menu menu;

		public DropdownSelectionListener(Menu menu) {
			this.menu = menu;
		}

		public void widgetSelected(SelectionEvent event) {
			if (event.detail == SWT.ARROW) {
				ToolItem item = (ToolItem) event.widget;
				Rectangle rect = item.getBounds();
				Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
				menu.setLocation(pt.x, pt.y + rect.height);
				menu.setVisible(true);
			} else if (event.widget instanceof ToolItem) {
				ToolItem item = (ToolItem) event.widget;
				if (item.getText().equals(START_TOOL_ITEM_LABEL)) {
					toolItemBrowserDropdown.setText(WebUIDriverType.FIREFOX_DRIVER.toString());
					startBrowser(WebUIDriverType.FIREFOX_DRIVER);
				} else if (!item.getText().isEmpty()) {
					startBrowser(WebUIDriverType.fromStringValue(item.getText()));
				}
			}
		}
	}
	
	private void pause() {
		isPausing = true;
		tltmPause.setText(RESUME_TOOL_ITEM_LABEL);
		tltmPause.setImage(ImageConstants.IMG_16_PLAY);
		toolBar.pack();
	}

	private void resume() {
		isPausing = false;
		tltmPause.setText(PAUSE_TOOL_ITEM_LABEL);
		tltmPause.setImage(ImageConstants.IMG_16_PAUSE);
		toolBar.pack();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gl_container = new GridLayout();
		container.setLayout(gl_container);

		toolBar = new ToolBar(container, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

		toolItemBrowserDropdown = new ToolItem(toolBar, SWT.DROP_DOWN);
		toolItemBrowserDropdown.setText(START_TOOL_ITEM_LABEL);
		toolItemBrowserDropdown.setImage(ImageConstants.IMG_28_RECORD);

		Menu browserMenu = new Menu(toolBar.getShell());

		addBrowserMenuItem(browserMenu, WebUIDriverType.FIREFOX_DRIVER);
		addBrowserMenuItem(browserMenu, WebUIDriverType.CHROME_DRIVER);
		addBrowserMenuItem(browserMenu, WebUIDriverType.IE_DRIVER);

		toolItemBrowserDropdown.addSelectionListener(new DropdownSelectionListener(browserMenu));

		tltmPause = new ToolItem(toolBar, SWT.PUSH);
		tltmPause.setText(PAUSE_TOOL_ITEM_LABEL);
		tltmPause.setImage(ImageConstants.IMG_16_PAUSE);
		tltmPause.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isPausing) {
					resume();
				} else {
					pause();
				}
			}

		});
		tltmPause.setEnabled(false);

		tltmStop = new ToolItem(toolBar, SWT.PUSH);
		tltmStop.setText(STOP_TOOL_ITEM_LABEL);
		tltmStop.setImage(ImageConstants.IMG_16_STOP);
		tltmStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					server.stop();
					session.stop();
				} catch (Exception e) {
					logger.error(e);
					MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
				}
				resume();

				tltmPause.setEnabled(false);
				tltmStop.setEnabled(false);
			}
		});
		tltmStop.setEnabled(false);

		Composite tableComposite = new Composite(container, SWT.None);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		actionTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		actionTableViewer.getTable().setHeaderVisible(true);
		actionTableViewer.getTable().setLinesVisible(true);

		TableViewerColumn tableViewerColumnNo = new TableViewerColumn(actionTableViewer, SWT.NONE);
		TableColumn tableViewerNo = tableViewerColumnNo.getColumn();
		tableViewerNo.setText(TABLE_COLUMN_NO_TITLE);

		TableViewerColumn tableViewerColumnAction = new TableViewerColumn(actionTableViewer, SWT.NONE);
		TableColumn tableColumnAction = tableViewerColumnAction.getColumn();
		tableColumnAction.setText(TABLE_COLUMN_ACTION_TITLE);

		TableViewerColumn tableViewerColumnActionData = new TableViewerColumn(actionTableViewer, SWT.NONE);
		TableColumn tableColumnActionData = tableViewerColumnActionData.getColumn();
		tableColumnActionData.setText(TABLE_COLUMN_ACTION_DATA_TITLE);

		TableViewerColumn tableViewerColumnElement = new TableViewerColumn(actionTableViewer, SWT.NONE);
		TableColumn tableColumnElement = tableViewerColumnElement.getColumn();
		tableColumnElement.setText(TABLE_COLUMN_ELEMENT_TITLE);

		TableColumnLayout tableLayout = new TableColumnLayout();
		tableLayout.setColumnData(tableViewerNo, new ColumnWeightData(0, 40));
		tableLayout.setColumnData(tableColumnAction, new ColumnWeightData(20, 100));
		tableLayout.setColumnData(tableColumnActionData, new ColumnWeightData(20, 100));
		tableLayout.setColumnData(tableColumnElement, new ColumnWeightData(60, 100));

		tableComposite.setLayout(tableLayout);

		actionTableViewer.setContentProvider(ArrayContentProvider.getInstance());

		tableViewerColumnNo.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof HTMLAction) {
					return String.valueOf(actions.indexOf((HTMLAction) element) + 1);
				}
				return StringUtils.EMPTY;
			}
		});

		tableViewerColumnAction.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof HTMLAction) {
					return ((HTMLAction) element).getActionName();
				}
				return StringUtils.EMPTY;
			}
		});

		tableViewerColumnActionData.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof HTMLAction) {
					return ((HTMLAction) element).getActionData();
				}
				return StringUtils.EMPTY;
			}
		});

		tableViewerColumnElement.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof HTMLAction) {
					HTMLElement targetElement = ((HTMLAction) element).getTargetElement();
					if (targetElement != null) {
						return targetElement.getName() + " (" + targetElement.getXpath() + ")";
					}
				}
				return StringUtils.EMPTY;
			}
		});

		actionTableViewer.setInput(actions);
		return container;
	}

	private void addBrowserMenuItem(Menu browserMenu, final WebUIDriverType webUIDriverType) {
		MenuItem menuItem = new MenuItem(browserMenu, SWT.NONE);
		menuItem.setText(webUIDriverType.toString());
		menuItem.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				toolItemBrowserDropdown.setText(webUIDriverType.toString());
				startBrowser(webUIDriverType);
			}
		});
	}

	public void dispose() {
		if (server != null && server.isRunning()) {
			try {
				server.stop();
			} catch (Exception e) {
				logger.error(e);
			}
		}
		if (session != null && session.isRunning()) {
			session.stop();
		}
		eventBroker.unsubscribe(this);
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
	}

	@Override
	protected void okPressed() {
		super.okPressed();
		dispose();
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
		dispose();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(650, 400);
	}

	@Override
	protected void handleShellCloseEvent() {
		super.handleShellCloseEvent();
		dispose();
	}

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(EventConstants.RECORDER_ELEMENT_ADDED)
				&& event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME) instanceof HTMLAction && !isPausing) {
			HTMLAction newAction = (HTMLAction) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
			if (HTMLActionUtil.verifyAction(newAction, actions)) {
				if (newAction.getTargetElement() != null) {
					addNewElement(newAction.getTargetElement(), newAction);
				}
				actions.add(newAction);
				actionTableViewer.refresh();
				actionTableViewer.reveal(newAction);
			}
		}
	}

	private void addNewElement(HTMLElement newElement, HTMLAction newAction) {
		HTMLPageElement parentPageElement = newElement.getParentPageElement();
		if (parentPageElement != null) {
			if (elements.contains(parentPageElement)) {
				addNewElement(elements.get(elements.indexOf(parentPageElement)), parentPageElement.getChildElements()
						.get(0), parentPageElement, newAction);
			} else {
				elements.add(parentPageElement);
			}
		}
	}

	private void addNewElement(HTMLFrameElement parentElement, HTMLElement newElement, HTMLPageElement pageElement,
			HTMLAction newAction) {
		if (parentElement.getChildElements().contains(newElement)) {
			if (newElement instanceof HTMLFrameElement) {
				HTMLFrameElement frameElement = (HTMLFrameElement) newElement;
				HTMLFrameElement existingFrameElement = (HTMLFrameElement) (parentElement.getChildElements()
						.get(parentElement.getChildElements().indexOf(newElement)));
				addNewElement(existingFrameElement, frameElement.getChildElements().get(0), pageElement, newAction);
			} else {
				for (HTMLElement element : parentElement.getChildElements()) {
					if (element.equals(newElement)) {
						newAction.setTargetElement(element);
						break;
					}
				}
			}
		} else {
			parentElement.getChildElements().add(newElement);
			newElement.setParentElement(parentElement);
			return;
		}
	}

	public List<HTMLAction> getActions() {
		return actions;
	}

	public List<HTMLPageElement> getElements() {
		return elements;
	}

}
