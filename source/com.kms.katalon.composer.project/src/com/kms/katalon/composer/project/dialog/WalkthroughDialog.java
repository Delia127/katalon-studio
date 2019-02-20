package com.kms.katalon.composer.project.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.project.dialog.WalkthroughItem.SecondaryLinkItem;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.tracking.service.Trackings;

public class WalkthroughDialog extends Dialog {

	private List<WalkthroughItem> walkthroughItems;
	private String learnMoreLink = "";
	private int uncheckedItem;
	private String ignoredCriteria = "";
	private boolean isIgnored = false;
	private String trackingId = "";
	private String dialogTitle = "";

	/**
	 * 
	 * Create a new dialog with a collection of a check box followed by some
	 * links. This represents a typical walk through flow where each check box
	 * will be checked if the user accomplishes the corresponding instruction
	 * 
	 * @param trackingId
	 * 			  An internal ID that identifies a particular walk-through dialog (used in Trackings {@link com.kms.katalon.tracking.service.Trackings})
	 * 
	 * @param dialogTitle
	 * 			  Title of this walk-through dialog
	 * 
	 * @param parentShell
	 *            should be null to create a top-level shell
	 * @param items
	 *            A list of WalkthroughItem to be populated
	 * 
	 * @param learnMoreLink
	 *            Link to "Learn More" when all instructions have been cleared
	 * @param ignoreCriteria
	 *            This text will appear on a button that allows users to ignore
	 *            this dialog forever
	 * 
	 */
	public WalkthroughDialog(String trackingId, String dialogTitle, Shell parentShell, List<WalkthroughItem> items, String learnMoreLink,
			String ignoreCriteria) {
		super(parentShell);
		this.walkthroughItems = items;
		this.learnMoreLink = learnMoreLink;
		this.uncheckedItem = walkthroughItems.size();
		this.ignoredCriteria = ignoreCriteria;
		this.trackingId = trackingId;
		this.dialogTitle = dialogTitle;
	}

	public List<WalkthroughItem> getWalkthroughItems() {
		return this.walkthroughItems;
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		// This code makes the dialog not block the application
		super.setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE | SWT.ON_TOP);
		setBlockOnOpen(false);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(dialogTitle);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		GridData gdContainer = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdContainer.heightHint = 400;
		gdContainer.widthHint = 400;
		container.setLayoutData(gdContainer);
		GridLayout glContainer = new GridLayout(1, false);
		container.setLayout(glContainer);

		Label lblInfo = new Label(container, SWT.NONE);
		lblInfo.setText("Let's go through these following Katalon Studio functions:");

		Link lblXLeftToGo = new Link(container, SWT.NONE);
		FontDescriptor descriptor = FontDescriptor.createFrom(lblXLeftToGo.getFont());
		// setStyle method returns a new font descriptor for the given style
		descriptor = descriptor.setStyle(SWT.BOLD);
		lblXLeftToGo.setFont(descriptor.createFont(lblXLeftToGo.getDisplay()));
		lblXLeftToGo.setText(uncheckedItem + " more to go");

		if (walkthroughItems != null && !walkthroughItems.isEmpty()) {
			for (WalkthroughItem item : walkthroughItems) {
				Composite walkthroughItemContainer = new Composite(container, SWT.NONE);
				GridData gdWalkthroughItemContainer = new GridData(SWT.FILL, SWT.FILL, false, false);
				GridLayout glWalkthroughItemContainer = new GridLayout(1, false);
				walkthroughItemContainer.setLayout(glWalkthroughItemContainer);
				walkthroughItemContainer.setLayoutData(gdWalkthroughItemContainer);

				CLabel checkBox = new CLabel(walkthroughItemContainer, SWT.NONE);
				checkBox.setText(item.getPrimaryInstruction());
				checkBox.setImage(ImageManager.getImage(IImageKeys.ERROR_16));

				EventBrokerSingleton.getInstance().getEventBroker().subscribe(item.getRegisteredEvent(),
						new EventHandler() {
							@Override
							public void handleEvent(Event event) {
								decreaseUncheckedItemCount();
								UISynchronizeService.asyncExec(() -> {
									checkBox.setFocus();
									checkBox.setImage(ImageManager.getImage(IImageKeys.OK_16));
									if (getUncheckedItemCount() == 0) {
										lblInfo.setText(
												"Congratulations! You have gone through all the basics.");
										lblXLeftToGo.setText("<a> Learn more </a>");
										lblXLeftToGo.addSelectionListener(new SelectionAdapter() {
											@Override
											public void widgetSelected(SelectionEvent e) {
												Program.launch(learnMoreLink);
											}
										});
									} else {
										int currentCount = getUncheckedItemCount();
										lblXLeftToGo.setText(currentCount + " more to go");
									}
								});
							}
						});

				Composite linkContainer = new Composite(walkthroughItemContainer, SWT.NONE);
				GridData gdLinkContainer = new GridData(SWT.FILL, SWT.FILL, true, true);
				linkContainer.setLayoutData(gdLinkContainer);
				GridLayout glLinkContainer = new GridLayout(2, false);
				linkContainer.setLayout(glLinkContainer);

				for (SecondaryLinkItem linkItem : item.getSecondaryLinkItems()) {
					// This won't show
					new Label(linkContainer, SWT.NONE).setText("\t");
					// This will
					Link thisLink = new Link(linkContainer, SWT.NONE);
					thisLink.setText("<a>" + linkItem.getText() + "</a>");
					thisLink.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							Program.launch(linkItem.getLink());
							Trackings.trackClickWalkthroughDialogLink(trackingId, linkItem.getText(), linkItem.getLink());
						}
					});
				}
			}
		}
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, this.ignoredCriteria, false);
		getButton(IDialogConstants.OK_ID).addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				isIgnored = true;
				Trackings.trackClickWalkthroughIgnoreButton(trackingId);
			}
		});
	}
	
	public boolean isIgnore(){
		return this.isIgnored;
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		return new Point(Display.getCurrent().getClientArea().width, 0);
	}

	private void decreaseUncheckedItemCount() {
		uncheckedItem = uncheckedItem - 1;
	}

	private int getUncheckedItemCount() {
		return uncheckedItem;
	}
}
