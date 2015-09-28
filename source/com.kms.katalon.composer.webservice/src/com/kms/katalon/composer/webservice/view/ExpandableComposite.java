package com.kms.katalon.composer.webservice.view;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.ImageConstants;

public class ExpandableComposite {
	
	Button btnExpandInformation;
	
	Composite container, compositeInfoDetails, compositeInfo;
	
	private boolean isInfoCompositeExpanded = true;
	
	private String title;
	
	private int detailColumns;
	
	public ExpandableComposite(Composite container, String title, int colums, boolean expandOnShow){
		this.container = container;
		this.title = title;
		this.detailColumns = colums;
	}
	
	public Composite createControl(){
		
		compositeInfo = new Composite(container, SWT.NONE);
		GridLayout gl_compositeInfo = new GridLayout(1, false);
		gl_compositeInfo.verticalSpacing = 0;
		gl_compositeInfo.marginWidth = 0;
		gl_compositeInfo.marginHeight = 0;
		compositeInfo.setLayout(gl_compositeInfo);
		compositeInfo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		compositeInfo.setBackground(ColorUtil.getCompositeBackgroundColor());

		Composite compositeInfoHeader = new Composite(compositeInfo, SWT.NONE);
		compositeInfoHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		compositeInfoHeader.setBounds(0, 0, 64, 64);
		GridLayout gl_compositeInfoHeader = new GridLayout(2, false);
		gl_compositeInfoHeader.marginWidth = 0;
		gl_compositeInfoHeader.marginHeight = 0;
		compositeInfoHeader.setLayout(gl_compositeInfoHeader);

		btnExpandInformation = new Button(compositeInfoHeader, SWT.NONE);
		redrawBtnExpandInfo();
		GridData gd_btnExpandInfo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnExpandInfo.widthHint = 18;
		gd_btnExpandInfo.heightHint = 18;
		btnExpandInformation.setLayoutData(gd_btnExpandInfo);

		Label lblNewLabel = new Label(compositeInfoHeader, SWT.NONE);
		lblNewLabel.setText(title);
		lblNewLabel.setFont(JFaceResources.getFontRegistry().getBold(""));

		compositeInfoDetails = new Composite(compositeInfo, SWT.NONE);
		GridLayout gl_compositeInfoDetails = new GridLayout(detailColumns, true);
		gl_compositeInfoDetails.marginRight = 40;
		gl_compositeInfoDetails.marginLeft = 40;
		gl_compositeInfoDetails.marginBottom = 5;
		gl_compositeInfoDetails.horizontalSpacing = 30;
		gl_compositeInfoDetails.marginHeight = 0;
		gl_compositeInfoDetails.marginWidth = 0;
		compositeInfoDetails.setLayout(gl_compositeInfoDetails);
		compositeInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeInfoDetails.setBounds(0, 0, 64, 64);

		hookControlSelectListerners();
		
		return compositeInfoDetails;
	}
	
	private void redrawBtnExpandInfo() {
		btnExpandInformation.getParent().setRedraw(false);
		if (isInfoCompositeExpanded) {
			btnExpandInformation.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
		} else {
			btnExpandInformation.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
		}
		btnExpandInformation.getParent().setRedraw(true);
	}
    
	private void hookControlSelectListerners() {
		btnExpandInformation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Display.getDefault().timerExec(10, new Runnable() {

					@Override
					public void run() {
						isInfoCompositeExpanded = !isInfoCompositeExpanded;
						compositeInfoDetails.setVisible(isInfoCompositeExpanded);
						if (!isInfoCompositeExpanded) {
							((GridData) compositeInfoDetails.getLayoutData()).exclude = true;
							compositeInfo.setSize(compositeInfo.getSize().x, compositeInfo.getSize().y);
						} else {
							((GridData) compositeInfoDetails.getLayoutData()).exclude = false;
						}
						compositeInfo.layout(true, true);
						compositeInfo.getParent().layout();
						redrawBtnExpandInfo();
					}
				});
			}
		});
	}
}