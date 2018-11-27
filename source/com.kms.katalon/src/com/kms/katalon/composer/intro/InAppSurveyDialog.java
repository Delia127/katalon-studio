package com.kms.katalon.composer.intro;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class InAppSurveyDialog extends Dialog {
	protected boolean shouldShowDialogAgain;
    
    private Label lblStarRating;
    private Label lblUserIdea;
    private Text txtUserIdea;
    private StarRating star;
    
    private int status = 0;
    private int numberOfStars = 0;
    private String userIdea = StringUtils.EMPTY;
    
	public InAppSurveyDialog(Shell parentShell) {
		super(parentShell);
        setShellStyle(SWT.APPLICATION_MODAL | SWT.NO_TRIM | SWT.ON_TOP);
        shouldShowDialogAgain = getPreferenceStore()
                .getBoolean(PreferenceConstants.GENERAL_SHOW_IN_APP_SURVEY_DIALOG_ON_APP_FIRST_CLOSE);
	}

    
    private StarRating createStarRatingComposite(Composite parent){
    	StarRating star = new StarRating(parent, SWT.NONE);
    	star.setImage(ImageManager.getImage(IImageKeys.STAR));
    	star.setVertical(false);
    	star.setNrOfStars(5);
    	star.setSelection(0);
    	star.addListener(SWT.MouseUp, new Listener()
        {
            @Override
            public void handleEvent(Event arg0)
            {
                int numberOfStarsSelected = star.getSelection();
                numberOfStars = numberOfStarsSelected;
                if(numberOfStarsSelected != 0){
                	getButton(Dialog.OK).setEnabled(true);
                }
                if(numberOfStarsSelected >= 0 && numberOfStarsSelected <= 2){
                	lblUserIdea.setText("We're sorry :(. What didn't you like about Katalon Studio?");  
                }else if(numberOfStarsSelected <=4 ){
                	lblUserIdea.setText("Thanks! How can we improve?");
                }else {
                	lblUserIdea.setText("Glad you like us :). What do you enjoy most about Katalon Studio?");
                }
            }
        });
    	
    	return star;
    }
    
   
    private ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
    }


    @Override
    protected Control createDialogArea(Composite parent) {
    	Composite container = new Composite(parent, SWT.NONE);
    	GridData gdContainer = new GridData(SWT.FILL, SWT.FILL, true, true);
    	gdContainer.heightHint = 300;
    	gdContainer.widthHint = 400;
    	container.setLayoutData(gdContainer);
    	GridLayout glContainer = new GridLayout(1, false);
    	container.setLayout(glContainer);
    	
    	Composite starRatingContainer = new Composite(container, SWT.NONE);
    	starRatingContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout glStarRatingContainer = new GridLayout(2, false);
        glStarRatingContainer.horizontalSpacing = 15;
        starRatingContainer.setLayout(glStarRatingContainer);
        
        
        lblStarRating = new Label(starRatingContainer, SWT.NONE);
        lblStarRating.setText("Please rate us");
        lblStarRating.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        star = createStarRatingComposite(starRatingContainer);
        star.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
       
        
        Composite userIdeaComposite = new Composite(container, SWT.NONE);
        userIdeaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout glUserIdea = new GridLayout(1, false);
        userIdeaComposite.setLayout(glUserIdea);
        
        lblUserIdea = new Label(userIdeaComposite, SWT.NONE);
        lblUserIdea.setText("we are happy to receive your ideas");
        lblUserIdea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        txtUserIdea = new Text(userIdeaComposite, SWT.BORDER);
        txtUserIdea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return container;
	}
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, "Send", true); // status = 0
        createButton(parent, IDialogConstants.FINISH_ID, "Later", false); // status = 1
        createButton(parent, IDialogConstants.CANCEL_ID, "No, thanks", false); // status = 2
        
        Button sendButton = getButton(IDialogConstants.OK_ID);
        sendButton.setEnabled(false);
		sendButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					status = 0;
					break;
				}
			}
		});
		
		Button laterButton = getButton(IDialogConstants.FINISH_ID);
		laterButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					status = 1;
					break;
				}
			}
		});
		
		Button noThanksButton = getButton(IDialogConstants.CANCEL_ID);
		noThanksButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Selection:
					status = 2;
					break;
				}
			}
		});
    }

    @Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == IDialogConstants.FINISH_ID){
			setReturnCode(IDialogConstants.FINISH_ID);
			close();
		} else {
			super.buttonPressed(buttonId);
		}
	}
    
    public int getStatus(){
    	return status;
    }
    
    public int getNumberOfStars(){
    	return numberOfStars;
    }
    
    public String getUserIdea(){
    	return userIdea;
    }
}
