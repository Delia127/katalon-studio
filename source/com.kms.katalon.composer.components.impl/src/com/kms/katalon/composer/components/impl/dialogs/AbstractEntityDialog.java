package com.kms.katalon.composer.components.impl.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.dal.exception.InvalidNameException;
import com.kms.katalon.entity.folder.FolderEntity;

public class AbstractEntityDialog extends TitleAreaDialog {

    private static final int BASE_NUMBER_COLUMN = 2;

    private static final int DIFF_SIZE = 1;

    private static final int H_GAP_IMAGE = 5;

    private String name = "";

    private String windowTitle = StringConstants.DIA_WINDOW_TITLE_NEW;

    private String lblName = StringConstants.DIA_LBL_NAME;

    private String dialogTitle = "";

    private String dialogMsg = StringConstants.DIA_LBL_CREATE_NEW;

    /** The valid message types are one of NONE, INFORMATION,WARNING, or ERROR */
    private int msgType = IMessageProvider.INFORMATION;

    protected Text txtName;

    protected Composite container;

    protected FolderEntity parentFolder;

    private boolean isFileEntity = true;

    private Composite wholeArea;

    private Label messageImageLabel;

    private Text messageLabel;

    private Point imageLocation;

    private Point textLocation;

    public AbstractEntityDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell);
        this.parentFolder = parentFolder;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getWindowTitle());
    }

    @Override
    protected Control createContents(Composite parent) {
        wholeArea = (Composite) super.createContents(parent);
        Control[] controls = wholeArea.getChildren();
        for (int i = 0; i < controls.length; ++i) {
            if (controls[i] instanceof Text && i != 0 && (controls[i - 1] instanceof Label)) {
                messageLabel = (Text) controls[i];
                messageImageLabel = (Label) controls[i - 1];
                break;
            }
        }
        return wholeArea;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // Title and message area
        Composite area = (Composite) super.createDialogArea(parent);

        // body area
        createDialogBodyArea(area);

        // Build the separator line
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return area;
    }

    public Control createDialogBodyArea(Composite parent) {
        if (container == null) {
            container = new Composite(parent, SWT.NONE);
        }
        GridLayout gLayout = (GridLayout) container.getLayout();
        int numColumns = (gLayout != null && gLayout.numColumns > BASE_NUMBER_COLUMN) ? gLayout.numColumns
                : BASE_NUMBER_COLUMN;
        int span = numColumns - BASE_NUMBER_COLUMN;
        createEntityNameControl(container, numColumns, span);
        createEntityCustomControl(container, numColumns, span);
        return container;
    }

    private Control createEntityNameControl(Composite parent, int column, int span) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(column, false));
        Label labelName = new Label(parent, SWT.NONE);
        labelName.setText(getLblName());

        txtName = new Text(parent, SWT.BORDER);
        txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtName.setText(getName());
        txtName.selectAll();
        txtName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setName(((Text) e.getSource()).getText());
                updateStatus();
            }
        });
        if (span > 0) {
            createEmptySpace(parent, span);
        }
        return parent;
    }

    protected Control createEntityCustomControl(Composite parent, int column, int span) {
        return parent;
    }

    public void updateStatus() {
        super.getButton(OK).setEnabled(isValidEntityName());
    }

    private boolean isValidEntityName() {
        String entityName = getName();
        try {
            EntityNameController.getInstance().validateName(entityName);

            validateEntityName(entityName);

            setErrorMessage(null);
            return true;
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
            return false;
        }
    }

    /**
     * Extra validation for entity name
     * 
     * @param entityName
     * @throws InvalidNameException
     * @throws Exception
     */
    public void validateEntityName(String entityName) throws Exception {
        if (!StringUtils.equalsIgnoreCase(
                EntityNameController.getInstance().getAvailableName(entityName, parentFolder, !isFileEntity()),
                entityName)) {
            throw new InvalidNameException(StringConstants.DIA_NAME_EXISTED);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        updateStatus();
        setErrorMessage(null); // Should not show any error message on dialog
                               // initialization
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, super.getInitialSize().y);
    }

    @Override
    public void create() {
        super.create();
        setTitle(getDialogTitle());
        setMessage(getDialogMsg(), getMsgType());
    }

    @Override
    protected boolean isResizable() {
        return false;
    }

    /**
     * Creates a spacer control with the given span. The composite is assumed to
     * have <code>GridLayout</code> as layout.
     * 
     * @param parent
     * The parent composite
     * @param span
     * the given span
     * @return the spacer control
     */
    public static Control createEmptySpace(Composite parent, int span) {
        Label label = new Label(parent, SWT.LEFT);
        GridData gd = new GridData();
        gd.horizontalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = false;
        gd.horizontalSpan = span;
        gd.horizontalIndent = 0;
        gd.widthHint = 0;
        gd.heightHint = 0;
        label.setLayoutData(gd);
        return label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            // trim and replace multiple space by single one
            name = name.trim().replaceAll("\\s+", " ");
        }
        this.name = name;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public String getLblName() {
        return lblName;
    }

    public void setLblName(String lblName) {
        this.lblName = lblName;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public String getDialogMsg() {
        return dialogMsg;
    }

    public void setDialogMsg(String dialogMsg) {
        this.dialogMsg = dialogMsg;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public boolean isFileEntity() {
        return isFileEntity;
    }

    public void setFileEntity(boolean isFileEntity) {
        this.isFileEntity = isFileEntity;
    }

    @Override
    public void setErrorMessage(String newErrorMessage) {
        super.setErrorMessage(newErrorMessage);
        if (wholeArea == null) {
            return;
        }
        customizeMessageImage();
    }

    @Override
    public void setMessage(String newMessage, int newType) {
        super.setMessage(newMessage, newType);
        if (wholeArea == null) {
            return;
        }
        customizeMessageImage();
    }

    private void customizeMessageImage() {
        Image newImage = getImage(messageImageLabel);
        messageImageLabel.setImage(newImage);
        Rectangle imageBound = newImage.getBounds();
        messageImageLabel.setSize(imageBound.width, imageBound.height);
        if (!(messageImageLabel.getVisible())) {
            messageImageLabel.setVisible(true);
        }
        setTextAndImageAlign();
    }

    private Image getImage(Label messageLabel) {
        Image oldImage = messageLabel.getImage();
        if (oldImage == null) {
            return null;
        }
        if (oldImage.equals(JFaceResources.getImage(DLG_IMG_MESSAGE_INFO))) {
            return ImageConstants.IMG_20_INFO_MSG;
        }
        if (oldImage.equals(JFaceResources.getImage(DLG_IMG_MESSAGE_ERROR))) {
            return ImageConstants.IMG_20_ERROR_MSG;
        }
        if (oldImage.equals(JFaceResources.getImage(DLG_IMG_MESSAGE_WARNING))) {
            return ImageConstants.IMG_20_WARNING_MSG;
        }
        return oldImage;
    }

    private void setTextAndImageAlign() {
        if (imageLocation == null) {
            imageLocation = messageImageLabel.getLocation();
        }
        if (textLocation == null) {
            textLocation = messageLabel.getLocation();
        }
        int messageImagePosX = imageLocation.x + DIFF_SIZE;
        int messagePosX = messageImagePosX + messageImageLabel.getBounds().width + H_GAP_IMAGE;
        messageLabel.setLocation(messagePosX, textLocation.y);
        messageImageLabel.setLocation(messageImagePosX, imageLocation.y - DIFF_SIZE);
    }

}
