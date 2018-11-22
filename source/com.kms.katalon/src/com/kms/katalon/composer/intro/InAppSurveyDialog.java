package com.kms.katalon.composer.intro;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.project.constants.ImageConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class InAppSurveyDialog extends TitleAreaDialog {
	protected boolean shouldShowDialogAgain;
    private String title = "In-app Survey";
    private String dialogMessage = "Help us improve Katalon Studio";
    
    private Label lblStarRating;
    private Label lblUserIdea;
    private Text txtUserIdea;
    private StarRating star;
    
	public InAppSurveyDialog(Shell parentShell) {
		super(parentShell);
        setShellStyle(SWT.APPLICATION_MODAL | SWT.NO_TRIM | SWT.ON_TOP);
        shouldShowDialogAgain = getPreferenceStore()
                .getBoolean(PreferenceConstants.GENERAL_SHOW_IN_APP_SURVEY_DIALOG_ON_APP_FIRST_CLOSE);
	}

    
    private StarRating createStarRatingComposite(Composite parent){
    	StarRating star = new StarRating(parent, SWT.NONE);
    	star.setImage(ImageConstants.API_ICON);
    	star.setVertical(false);
    	star.setNrOfStars(5);
    	star.setSelection(0);
    	star.addListener(SWT.MouseUp, new Listener()
        {
            @Override
            public void handleEvent(Event arg0)
            {
                int numberOfStarsSelected = star.getSelection();
                if(numberOfStarsSelected >= 0 && numberOfStarsSelected <= 2){
                	lblUserIdea.setText("What do you dislike the most in Katalon Studio");
                }else if(numberOfStarsSelected == 3){
                	lblUserIdea.setText("What feature should we improve in Katalon Studio");
                }else {
                	lblUserIdea.setText("What do you like the most in Katalon Studio");
                }
            }
        });
    	
    	return star;
    }
    
    @Override
    public void create() {
        super.create();
        setTitle(title);
        setMessage(dialogMessage, IMessageProvider.INFORMATION);
    }
	
    private ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(IdConstants.KATALON_GENERAL_BUNDLE_ID);
    }


    @Override
    protected Control createDialogArea(Composite parent) {
    	Composite container = new Composite(parent, SWT.NONE);
    	container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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
        lblUserIdea.setText("We are happy to receive your idea");
        lblUserIdea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        txtUserIdea = new Text(userIdeaComposite, SWT.NONE);
        txtUserIdea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        return container;
	}
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, "No, thanks", false);
        createButton(parent, IDialogConstants.OK_ID, "Send", true);
        createButton(parent, IDialogConstants.NEXT_ID, "Later", false);
    }
    
    public String getUserIdea(){
    	return txtUserIdea.getText();
    }
}
