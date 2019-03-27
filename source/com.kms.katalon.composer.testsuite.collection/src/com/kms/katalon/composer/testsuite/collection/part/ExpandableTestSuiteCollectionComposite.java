package com.kms.katalon.composer.testsuite.collection.part;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.kms.katalon.composer.components.impl.control.ImageButton;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;

public class ExpandableTestSuiteCollectionComposite {
		
		private String name;
		private AbstractTestSuiteCollectionUIDescriptionView descView;
		private Composite view;
		private ImageButton btnExpandExecutionComposite;
		private Label lblExecutionInformation;
		private boolean isExecutionCompositeExpanded;
		private Composite container;
		private Composite parent;
		
		private Listener layoutExecutionCompositeListener = new Listener() {

			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				isExecutionCompositeExpanded = !isExecutionCompositeExpanded;
				layoutExecutionInfo();
			}
		};
		
		public ExpandableTestSuiteCollectionComposite(String name, AbstractTestSuiteCollectionUIDescriptionView descView) {
			this.name = name;
			this.descView = descView;
			this.isExecutionCompositeExpanded = false;
		}
		
		public Composite createComposite(Composite parent) {
			this.parent = parent;
			
			Composite compositeExecutionCompositeHeader = new Composite(parent, SWT.NONE);
	        compositeExecutionCompositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
	        GridLayout glCompositeExecutionCompositeHeader = new GridLayout(2, false);
	        glCompositeExecutionCompositeHeader.marginHeight = 0;
	        glCompositeExecutionCompositeHeader.marginWidth = 0;
	        compositeExecutionCompositeHeader.setLayout(glCompositeExecutionCompositeHeader);
	        compositeExecutionCompositeHeader.setCursor(compositeExecutionCompositeHeader.getDisplay().getSystemCursor(
	                SWT.CURSOR_HAND));
	        compositeExecutionCompositeHeader.setCursor(compositeExecutionCompositeHeader.getDisplay().getSystemCursor(
	                SWT.CURSOR_HAND));

			btnExpandExecutionComposite = new ImageButton(compositeExecutionCompositeHeader, SWT.NONE);
	        redrawBtnExpandExecutionInfo();

	        lblExecutionInformation = new Label(compositeExecutionCompositeHeader, SWT.NONE);
	        lblExecutionInformation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
	        lblExecutionInformation.setFont(JFaceResources.getFontRegistry().getBold(""));
	        lblExecutionInformation.setText(name);

	        view = descView.createContainer(parent);
	        descView.postContainerCreated();
	        registerControlListener();
	        
	        layoutExecutionInfo();
	        
			return container;
		}
		
	    private void registerControlListener() {
	        lblExecutionInformation.addListener(SWT.MouseDown, layoutExecutionCompositeListener);
	        btnExpandExecutionComposite.addListener(SWT.MouseDown, layoutExecutionCompositeListener);
		}

		private void layoutExecutionInfo() {
	        Display.getDefault().timerExec(10, new Runnable() {
	            @Override
	            public void run() {
	            	view.setVisible(isExecutionCompositeExpanded);
	                if (!isExecutionCompositeExpanded) {
	                    ((GridData) view.getLayoutData()).exclude = true;
	                    parent.setSize(parent.getSize().x, 0);
	                } else {
	                    ((GridData) view.getLayoutData()).exclude = false;
	                }
	                parent.layout(true, true);
	                parent.getParent().layout();
	                redrawBtnExpandExecutionInfo();
	            }
	        });
	    }
	    
	    private void redrawBtnExpandExecutionInfo() {
	        btnExpandExecutionComposite.getParent().setRedraw(false);
	        if (isExecutionCompositeExpanded) {
	            btnExpandExecutionComposite.setImage(ImageConstants.IMG_16_ARROW_DOWN);
	        } else {
	            btnExpandExecutionComposite.setImage(ImageConstants.IMG_16_ARROW);
	        }
	        btnExpandExecutionComposite.getParent().setRedraw(true);
	    }
}
