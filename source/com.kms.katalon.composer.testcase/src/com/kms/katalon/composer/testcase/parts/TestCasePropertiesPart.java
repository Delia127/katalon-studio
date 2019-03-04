package com.kms.katalon.composer.testcase.parts;

import static org.apache.commons.lang.StringUtils.join;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.katalon.platform.api.Plugin;
import com.katalon.platform.api.service.ApplicationManager;
import com.kms.katalon.composer.components.util.CssUtil;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.dialogs.ManageTestCaseTagDialog;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.util.EntityTagUtil;

public class TestCasePropertiesPart extends CPart {

    @Inject
    private EPartService partService;

    private MPart mPart;

    private TestCaseCompositePart parentPart;

    private Composite parentComposite;

    private Text txtId;

    private Text txtName;

    private Text txtCreatedDate;

    private Text txtModifiedDate;

    private Text txtTag;

    private Text txtDescription;

    private Text txtComment;
    
    private Button btnManageTags;

    private boolean isInputLoaded;

    @PostConstruct
    public void postConstruct(Composite parentComposite, MPart mPart) {
        this.parentComposite = parentComposite;
        this.mPart = mPart;
        MElementContainer<MUIElement> parent = mPart.getParent().getParent();
        if (parent instanceof MGenericTile && ((MGenericTile<?>) parent) instanceof MCompositePart) {
            MCompositePart compositePart = (MCompositePart) (MGenericTile<?>) parent;
            Object compositePartObject = compositePart.getObject();
            if (compositePartObject instanceof TestCaseCompositePart) {
                parentPart = ((TestCaseCompositePart) compositePartObject);
            }
        }
        initialize(mPart, partService);
        createComponents();
        registerListeners();
    }

    private void createComponents() {
        GridLayout parentLayout = new GridLayout();
        parentLayout.marginWidth = 0;
        parentLayout.marginHeight = 0;
        parentComposite.setLayout(parentLayout);
        parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        ScrolledComposite container = new ScrolledComposite(parentComposite, SWT.H_SCROLL | SWT.V_SCROLL);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setMinSize(600, 400);
        container.setExpandHorizontal(true);
        container.setExpandVertical(true);
        CssUtil.applyCssClassName(container, Composite.class.getSimpleName());
        container.setBackgroundMode(SWT.INHERIT_DEFAULT);

        Composite mainComposite = new Composite(container, SWT.NONE);
        GridLayout mainCompositeLayout = new GridLayout(2, true);
        mainCompositeLayout.marginWidth = 0;
        mainCompositeLayout.marginHeight = 0;
        mainComposite.setLayout(mainCompositeLayout);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        container.setContent(mainComposite);

        Composite left = new Composite(mainComposite, SWT.NONE);
        left.setLayout(new GridLayout(2, false));
        left.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createLabel(StringConstants.ID, left, SWT.CENTER);
        txtId = new Text(left, SWT.BORDER | SWT.READ_ONLY);
        txtId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createLabel(StringConstants.CREATED_DATE, left, SWT.CENTER);
        txtCreatedDate = new Text(left, SWT.BORDER | SWT.READ_ONLY);
        txtCreatedDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createLabel(StringConstants.TAG, left, SWT.CENTER);
        txtTag = new Text(left, SWT.BORDER);
        txtTag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        if (isAdvancedTagPluginInstalled()) {
            Composite tagComposite = new Composite(left, SWT.NONE);
            tagComposite.setLayout(new GridLayout());
            tagComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 2, 1));
            btnManageTags = new Button(tagComposite, SWT.NONE);
            btnManageTags.setText(ComposerTestcaseMessageConstants.TestCasePropertiesPart_BTN_MANAGE_TAGS);
        }
        
        createLabel(StringConstants.DESCRIPTION, left, SWT.TOP);
        txtDescription = new Text(left, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData gdDesc = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdDesc.minimumHeight = 100;
        txtDescription.setLayoutData(gdDesc);

        Composite right = new Composite(mainComposite, SWT.NONE);
        right.setLayout(new GridLayout(2, false));
        right.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createLabel(StringConstants.NAME, right, SWT.CENTER);
        txtName = new Text(right, SWT.BORDER | SWT.READ_ONLY);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createLabel(StringConstants.MODIFIED_DATE, right, SWT.CENTER);
        txtModifiedDate = new Text(right, SWT.BORDER | SWT.READ_ONLY);
        txtModifiedDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblEmpty = new Label(right, SWT.NONE);
        GridData gdEmptyLabel = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
        gdEmptyLabel.heightHint = 21;
        lblEmpty.setLayoutData(gdEmptyLabel);

        createLabel(StringConstants.COMMENT, right, SWT.TOP);
        txtComment = new Text(right, SWT.BORDER | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
        GridData gdComment = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdComment.minimumHeight = 100;
        txtComment.setLayoutData(gdComment);
    }

    private Label createLabel(String labelText, Composite parent, int verticalAlign) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);
        GridData gdLabel = new GridData(SWT.LEFT, verticalAlign, false, false);
        gdLabel.widthHint = 80;
        label.setLayoutData(gdLabel);
        return label;
    }

    private void registerListeners() {
        txtTag.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = txtTag.getText();
                parentPart.getTestCase().setTag(text);
                setDirty(true);
            }
        });
        
        txtDescription.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = txtDescription.getText();
                parentPart.getTestCase().setDescription(text);
                setDirty(true);
            }
        });
        
        if (isAdvancedTagPluginInstalled()) {
            btnManageTags.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    Set<String> testCaseTags = EntityTagUtil.parse(txtTag.getText());
                    ManageTestCaseTagDialog dialog = new ManageTestCaseTagDialog(Display.getCurrent().getActiveShell(),
                            testCaseTags);
                    if (dialog.open() == ManageTestCaseTagDialog.CM_APPEND_TAGS) {
                        Set<String> newTags = dialog.getAppendedTags();
                        String newTagValues = EntityTagUtil.joinTags(newTags);
                        if (!StringUtils.isBlank(newTagValues)) {
                            String updatedTagValues = EntityTagUtil.appendTags(txtTag.getText(), newTags);
                            txtTag.setText(updatedTagValues);
                        }
                    }
                }
            });
        }
    }
    
    private boolean isAdvancedTagPluginInstalled() {
        Plugin plugin = ApplicationManager.getInstance().getPluginManager().getPlugin(IdConstants.PLUGIN_ADVANCED_TAGS);
        return plugin != null;
    }

    public void loadInput() {
        isInputLoaded = false;
        TestCaseEntity originalTestCase = parentPart.getOriginalTestCase();
        TestCaseEntity testCase = parentPart.getTestCase();
        populateTxtFieldValue(txtId, originalTestCase.getIdForDisplay());
        populateTxtFieldValue(txtName, originalTestCase.getName());
        populateTxtFieldValue(txtCreatedDate, originalTestCase.getDateCreated());
        populateTxtFieldValue(txtModifiedDate, originalTestCase.getDateModified());
        populateTxtFieldValue(txtTag, testCase.getTag());
        populateTxtFieldValue(txtDescription, testCase.getDescription());
        populateTxtFieldValue(txtComment, getComments());
        isInputLoaded = true;
    }

    private void populateTxtFieldValue(Text txtField, Object value) {
        if (txtField == null) {
            return;
        }
        txtField.setText(ObjectUtils.toString(value));
    }

    private String getComments() {
        return join(parentPart.getChildTestCasePart().getCommentSteps(), "\n");
    }

    public MPart getMPart() {
        return mPart;
    }

    public boolean isDirty() {
        return mPart.isDirty();
    }

    public void setDirty(boolean isDirty) {
        if (!isInputLoaded) {
            return;
        }
        mPart.setDirty(isDirty);
        parentPart.updateDirty();
    }

    public void preSave() {
        // save the comment for advanced searching purpose
        parentPart.getTestCase().setComment(txtComment.getText());
    }

    @PreDestroy
    public void preDestroy() {
        super.dispose();
    }
}
