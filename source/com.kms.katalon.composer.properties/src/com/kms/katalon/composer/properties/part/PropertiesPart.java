package com.kms.katalon.composer.properties.part;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.CssUtil;
import com.kms.katalon.composer.properties.constants.ImageConstants;
import com.kms.katalon.composer.properties.constants.PropertiesIdConstants;
import com.kms.katalon.composer.properties.constants.PropertiesMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.PropertiesController;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class PropertiesPart {

    private static final String CANNOT_SAVE_THE_PROPERTIES = PropertiesMessageConstants.PART_EXCEPTION_CANNOT_SAVE_THE_PROPERTIES;

    private static final String DO_YOU_WANT_TO_SAVE_THE_CHANGES_IN_PROPERTIES = PropertiesMessageConstants.PART_MSG_DO_YOU_WANT_TO_SAVE_THE_CHANGES_IN_PROPERTIES;

    private static final String DISCARD_CHANGES = PropertiesMessageConstants.PART_TOOLITEM_DISCARD_CHANGES;

    private static final String SAVE_CHANGES = PropertiesMessageConstants.PART_TOOLITEM_SAVE_CHANGES;

    private static final String ID = StringConstants.ID;

    private static final String NAME = StringConstants.NAME;

    private static final String CREATED_DATE = StringConstants.CREATED_DATE;

    private static final String MODIFIED_DATE = StringConstants.MODIFIED_DATE;

    private static final String TAG = StringConstants.TAG;

    private static final String DESCRIPTION = StringConstants.DESCRIPTION;

    private static final String TAKEN_DATE = PropertiesMessageConstants.PART_LBL_TAKEN_DATE;

    private static final String DATA_TYPE = PropertiesMessageConstants.PART_LBL_DATA_TYPE;

    private ScrolledComposite container;

    private Text txtId;

    private Text txtName;

    private Text txtCreatedDate;

    private Text txtModifiedDate;

    private Text txtTag;

    private Text txtDescription;

    private Text txtCustom;

    private Label lblCustom;

    private ToolItem tiSave;

    private ToolItem tiDiscard;

    private FileEntity entity;

    private boolean isDiscarding;

    @Inject
    private MDirtyable dirty;

    @PostConstruct
    public void postConstruct(Composite parent, MPart part) {
        createToolBar(part);
        createPartControls(parent);
        registerControlListeners();
        setInput(null);
    }

    private void createToolBar(MPart part) {
        CTabFolder ctabfolder = (CTabFolder) part.getParent().getWidget();
        ToolBar toolbar = new ToolBar(ctabfolder, SWT.FLAT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        MToolBar mToolbar = MMenuFactory.INSTANCE.createToolBar();
        mToolbar.setElementId(PropertiesIdConstants.PROPERTIES_TOOLBAR_ID);
        mToolbar.setWidget(toolbar);
        part.setToolbar(mToolbar);

        List<MToolBarElement> toolItems = mToolbar.getChildren();

        MDirectToolItem mtiSave = MMenuFactory.INSTANCE.createDirectToolItem();
        mtiSave.setElementId(PropertiesIdConstants.SAVE_TOOLITEM_ID);
        toolItems.add(mtiSave);

        MDirectToolItem mtiDiscard = MMenuFactory.INSTANCE.createDirectToolItem();
        mtiDiscard.setElementId(PropertiesIdConstants.DISCARD_TOOLITEM_ID);
        toolItems.add(mtiDiscard);

        tiSave = new ToolItem(toolbar, SWT.PUSH);
        tiSave.setImage(ImageConstants.IMG_SAVE_16);
        tiSave.setToolTipText(SAVE_CHANGES);

        tiDiscard = new ToolItem(toolbar, SWT.PUSH);
        tiDiscard.setImage(ImageConstants.IMG_REFRESH_16);
        tiDiscard.setToolTipText(DISCARD_CHANGES);
    }

    private void resizeContainer(final boolean isCustomFieldVisible) {
        UISynchronizeService.syncExec(new Runnable() {

            @Override
            public void run() {
                lblCustom.setVisible(isCustomFieldVisible);
                txtCustom.setVisible(isCustomFieldVisible);
                ((GridData) lblCustom.getLayoutData()).exclude = !isCustomFieldVisible;
                ((GridData) txtCustom.getLayoutData()).exclude = !isCustomFieldVisible;
                Composite mainComposite = txtCustom.getParent();
                mainComposite.layout(true, true);
                mainComposite.getParent().layout();
            }
        });
    }

    private void createPartControls(Composite parent) {
        container = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        container.setMinSize(300, 200);
        container.setExpandHorizontal(true);
        container.setExpandVertical(true);
        CssUtil.applyCssClassName(container, Composite.class.getSimpleName());
        container.setBackgroundMode(SWT.INHERIT_DEFAULT);

        Composite mainComposite = new Composite(container, SWT.NONE);
        mainComposite.setLayout(new GridLayout(2, false));
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        container.setContent(mainComposite);

        int txtStyle = SWT.READ_ONLY | SWT.BORDER;

        Label lblId = new Label(mainComposite, SWT.NONE);
        lblId.setText(ID);

        txtId = new Text(mainComposite, txtStyle);
        txtId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblName = new Label(mainComposite, SWT.NONE);
        lblName.setText(NAME);

        txtName = new Text(mainComposite, txtStyle);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblTag = new Label(mainComposite, SWT.NONE);
        lblTag.setText(TAG);

        txtTag = new Text(mainComposite, SWT.BORDER);
        txtTag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblDescription = new Label(mainComposite, SWT.NONE);
        lblDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lblDescription.setText(DESCRIPTION);

        txtDescription = new Text(mainComposite, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        GridData descLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        descLayoutData.heightHint = 80;
        txtDescription.setLayoutData(descLayoutData);

        Label lblCreatedDate = new Label(mainComposite, SWT.NONE);
        lblCreatedDate.setText(CREATED_DATE);

        txtCreatedDate = new Text(mainComposite, txtStyle);
        txtCreatedDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblModifiedDate = new Label(mainComposite, SWT.NONE);
        lblModifiedDate.setText(MODIFIED_DATE);

        txtModifiedDate = new Text(mainComposite, txtStyle);
        txtModifiedDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        // Custom field: Data Type (Test Data), Taken Date (Checkpoint)
        lblCustom = new Label(mainComposite, SWT.NONE);
        lblCustom.setText(StringConstants.EMPTY);
        lblCustom.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtCustom = new Text(mainComposite, txtStyle);
        txtCustom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }

    private void registerControlListeners() {
        ModifyListener modifyListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setModified(true);
            }
        };

        txtTag.addModifyListener(modifyListener);
        txtDescription.addModifyListener(modifyListener);

        container.addControlListener(new ControlListener() {

            @Override
            public void controlResized(ControlEvent e) {
                resizeContainer(lblCustom.isVisible());
            }

            @Override
            public void controlMoved(ControlEvent e) {
                resizeContainer(lblCustom.isVisible());
            }
        });

        tiSave.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                onSave();
            }
        });

        tiDiscard.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                isDiscarding = true;
                setModified(false);
                setInput(entity);
            }
        });
    }

    private FileEntity saveProperties() {
        if (entity == null) {
            return null;
        }

        String currentTag = entity.getTag();
        String currentDesc = entity.getDescription();
        try {
            // Only 2 fields need to update currently
            entity.setTag(txtTag.getText());
            entity.setDescription(txtDescription.getText());
            FileEntity savedEntity = PropertiesController.getInstance().updateProperties(entity);
            boolean isNull = savedEntity == null;
            setEnabledToolItems(isNull);
            if (isNull) {
                // This could not happen
                throw new DALException(CANNOT_SAVE_THE_PROPERTIES);
            }
            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.PROPERTIES_ENTITY_UPDATED,
                    savedEntity);
            return savedEntity;
        } catch (DALException e) {
            // Roll-back the modification
            entity.setTag(currentTag);
            entity.setDescription(currentDesc);
            setEnabledToolItems(true);
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR, e.getMessage());
            return entity;
        }
    }

    @Persist
    public void onSave() {
        if (entity == null) {
            return;
        }

        FileEntity savedEntity = saveProperties();
        if (savedEntity != null && StringUtils.equals(entity.getTag(), savedEntity.getTag())
                && StringUtils.equals(entity.getDescription(), savedEntity.getDescription())) {
            setModified(false);
        }
    }

    @Inject
    @Optional
    public void setInput(@UIEventTopic(EventConstants.PROPERTIES_ENTITY) FileEntity entity) {
        if (isModified()) {
            boolean isYes = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
                    StringConstants.PROPERTIES, DO_YOU_WANT_TO_SAVE_THE_CHANGES_IN_PROPERTIES);
            if (isYes) {
                saveProperties();
            }
            isDiscarding = !isYes;
            setModified(false);
        }

        if (!isDiscarding && this.entity != null && entity != null
                && new EqualsBuilder().append(this.entity.getIdForDisplay(), entity.getIdForDisplay())
                        .append(this.entity.getDateCreated(), entity.getDateCreated())
                        .append(this.entity.getDateModified(), entity.getDateModified())
                        .append(this.entity.getTag(), entity.getTag())
                        .append(this.entity.getDescription(), entity.getDescription())
                        .isEquals()) {
            return;
        }

        isDiscarding = false;
        clearInputFields();

        this.entity = entity;
        boolean isInvalidEntity = entity == null;
        setEnabledEditableFields(!isInvalidEntity);
        resizeContainer(false);
        if (isInvalidEntity) {
            return;
        }

        populateTxtFieldValue(txtId, entity.getIdForDisplay());
        populateTxtFieldValue(txtName, entity.getName());
        populateTxtFieldValue(txtCreatedDate, entity.getDateCreated());
        populateTxtFieldValue(txtModifiedDate, entity.getDateModified());
        populateTxtFieldValue(txtTag, entity.getTag());
        populateTxtFieldValue(txtDescription, entity.getDescription());
        setModified(false);

        if (entity instanceof DataFileEntity) {
            setCustomField(DATA_TYPE, ((DataFileEntity) entity).getDriver());
            return;
        }

        if (entity instanceof CheckpointEntity) {
            setCustomField(TAKEN_DATE, ((CheckpointEntity) entity).getTakenDate());
        }
    }

    private void setCustomField(final String label, final Object value) {
        UISynchronizeService.syncExec(new Runnable() {

            @Override
            public void run() {
                lblCustom.setText(label);
                populateTxtFieldValue(txtCustom, value);
            }
        });

        resizeContainer(true);
    }

    private void populateTxtFieldValue(Text txtField, Object value) {
        txtField.setRedraw(false);
        txtField.setText(ObjectUtils.toString(value));
        txtField.setRedraw(true);
    }

    private void clearInputFields() {
        txtId.setText(StringConstants.EMPTY);
        txtName.setText(StringConstants.EMPTY);
        txtCreatedDate.setText(StringConstants.EMPTY);
        txtModifiedDate.setText(StringConstants.EMPTY);
        txtTag.setText(StringConstants.EMPTY);
        txtDescription.setText(StringConstants.EMPTY);
        lblCustom.setText(StringConstants.EMPTY);
        txtCustom.setText(StringConstants.EMPTY);
        setModified(false);
    }

    private void setEnabledToolItems(boolean enabled) {
        tiSave.setEnabled(enabled);
        tiDiscard.setEnabled(enabled);
    }

    private void setEnabledEditableFields(boolean enabled) {
        txtTag.setEnabled(enabled);
        txtDescription.setEnabled(enabled);
    }

    public boolean isModified() {
        return dirty.isDirty();
    }

    public void setModified(boolean isModified) {
        dirty.setDirty(isModified);
        setEnabledToolItems(isModified);
    }

}
