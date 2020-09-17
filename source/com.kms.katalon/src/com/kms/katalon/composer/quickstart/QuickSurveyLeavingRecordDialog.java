package com.kms.katalon.composer.quickstart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.ComponentUtil.EventHandler;
import com.kms.katalon.tracking.service.Trackings;

public class QuickSurveyLeavingRecordDialog extends BaseQuickStartDialog {

    private final int CONTENT_WIDTH = 500;

    private final int OTHER_ANSWER_INPUT_HEIGHT = 50;

    private boolean isActionButtonClicked = false;

    private String answer, otherAnswer;

    private Text txtOtherAnswer;

    public QuickSurveyLeavingRecordDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public String getDialogTitle() {
        return "Could you leave us a comment?";
    }

    @Override
    protected void createContent(Composite container) {
        Composite body = ComponentBuilder.gridContainer(container).gridMargin(30).gridVerticalSpacing(10).build();

        createSurvey(body);
    }

    private void createSurvey(Composite parent) {
        Composite surveyComposite = ComponentBuilder.gridContainer(parent, 1).build();

        String confirmQuestion = "We would appreciate if you could tell us the reason why you stop the recording:";
        ComponentBuilder.label(surveyComposite).text(confirmQuestion).width(CONTENT_WIDTH).build();

        EventHandler handleOptionChange = event -> {
            answer = ((Button) event.widget).getText();
            txtOtherAnswer.setEnabled(answer == "Other");
        };
        createOption(surveyComposite, "I don't know what to do, it looks confusing", handleOptionChange);
        createOption(surveyComposite, "I know how to record tests, but want to do something else", handleOptionChange);
        createOption(surveyComposite, "I have done my recording, but don't want to save it", handleOptionChange);
        createOption(surveyComposite, "Other", handleOptionChange);
        txtOtherAnswer = ComponentBuilder.text(surveyComposite, SWT.MULTI | SWT.BORDER).fill().onChange(event -> {
            otherAnswer = ((Text) event.widget).getText();
        }).border().height(OTHER_ANSWER_INPUT_HEIGHT).build();
    }

    private void createOption(Composite parent, String text, EventHandler onClick) {
        ComponentBuilder.radio(parent).left().text(text).onClick(onClick).build();
    }

    @Override
    protected void createButtons(Composite parent) {
        Composite buttonsCompositeWrapper = ComponentBuilder.gridContainer(parent).fill().build();

        Composite buttonsComposite = ComponentBuilder.gridContainer(buttonsCompositeWrapper, 2).right().build();

        ComponentBuilder.button(buttonsComposite).text("Close").grayButton().onClick(event -> {
            isActionButtonClicked = true;
            Trackings.trackQuickRecordSurveyClose();
            cancelPressed();
        }).build();

        ComponentBuilder.button(buttonsComposite).text("Send").primaryButton().onClick(event -> {
            isActionButtonClicked = true;
            Trackings.trackQuickRecordSurveySend(answer, otherAnswer);
            okPressed();
        }).build();
    }

    @Override
    public boolean close() {
        if (!isActionButtonClicked) { // Click close [X] button
            Trackings.trackQuickRecordSurveyClose();
            setReturnCode(OK);
        }
        return super.close();
    }
    
    @Override
    protected boolean canClose() {
        return true;
    }
}
