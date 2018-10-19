package com.kms.katalon.composer.webservice.view;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.CustomTitleAreaDialog;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class NewHistoryRequestDialog extends CustomTitleAreaDialog {

    private static final int MAX_LINE_NUMBER = 4;

    private Label txtParentFolder;

    private Label txtRequestType;

    private Label txtRequestMethod;

    private Label txtUrl;

    private Text txtName;

    private Text txtDescription;

    private Button btnBrowseFolder;

    private FolderEntity parentFolder;

    private Composite container;
    
    private NewHistoryRequestResult result;
    
    private WebServiceRequestEntity draftRequest;
    
    private List<FileEntity> sibblingEntities;

    public NewHistoryRequestDialog(Shell parentShell, WebServiceRequestEntity draftRequest) {
        super(parentShell);
        this.draftRequest = draftRequest;
    }

    @Override
    protected void registerControlModifyListeners() {
        btnBrowseFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectObjectRepositoryFolder();
            }
        });
        
        txtName.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent event) {
                checkNewName(txtName.getText());
            }
        });

    }

    private void selectObjectRepositoryFolder() {
        try {
            EntityProvider entityProvider = new EntityProvider();
            TreeEntitySelectionDialog dialog = new TreeEntitySelectionDialog(this.getShell(), new EntityLabelProvider(),
                    entityProvider, new ObjectRepositoryFolderViewerFilter(entityProvider));
            dialog.setAllowMultiple(false);
            dialog.setTitle("Select a folder");
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            FolderEntity rootFolder = FolderController.getInstance().getObjectRepositoryRoot(currentProject);
            FolderTreeEntity rootFolderTreeEntity = new FolderTreeEntity(rootFolder, null);

            FolderTreeEntity parentTreeEntity = TreeEntityUtil.createSelectedTreeEntityHierachy(parentFolder,
                    rootFolder);
            dialog.setInitialSelection(parentTreeEntity);

            dialog.setInput(Arrays.asList(rootFolderTreeEntity));
            if (dialog.open() == TreeEntitySelectionDialog.OK) {
                FolderTreeEntity folderTree = (FolderTreeEntity) dialog.getFirstResult();
                parentFolder = folderTree.getObject();
                sibblingEntities = FolderController.getInstance().getChildren(parentFolder);
                txtParentFolder.setText(parentFolder.getIdForDisplay());
                
                checkNewName(txtName.getText());
                container.layout(true);
            }

        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
    }

    @Override
    protected void setInput() {
        try {
            setMessage("Enter a Web Service Request name", IMessageProvider.INFORMATION);

            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            parentFolder = FolderController.getInstance().getObjectRepositoryRoot(currentProject);
            sibblingEntities = FolderController.getInstance().getChildren(parentFolder);
            txtName.setText(getSuggestion("New Request"));
            
            txtParentFolder.setText(parentFolder.getIdForDisplay());

            WebServiceRequestEntity request = draftRequest;
            String serviceType = request.getServiceType();
            // Fix KAT-3704
            txtRequestType.setText(WebServiceRequestEntity.RESTFUL.equals(serviceType)
                   ? WebServiceRequestEntity.REST : serviceType);

            txtRequestMethod.setText(WebServiceRequestEntity.RESTFUL.equals(serviceType)
                    ? request.getRestRequestMethod() : request.getSoapRequestMethod());

            String url = WebServiceRequestEntity.RESTFUL.equals(serviceType)
                    ? request.getRestUrl() : request.getWsdlAddress();
            txtUrl.setText(url.replace("&", "&&"));

            container.layout(true);

            getShell().setSize(getInitialSize());
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
    }

    @Override
    protected Composite createContentArea(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.horizontalSpacing = 15;
        glContainer.verticalSpacing = 10;
        container.setLayout(glContainer);

        Label lblName = new Label(container, SWT.NONE);
        lblName.setText("Name");

        txtName = new Text(container, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblParentFolder = new Label(container, SWT.NONE);
        lblParentFolder.setText("Folder");

        Composite parentFolderComposite = new Composite(container, SWT.NONE);
        parentFolderComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout glParentFolder = new GridLayout(2, false);
        glParentFolder.marginWidth = 0;
        glParentFolder.marginHeight = 0;
        parentFolderComposite.setLayout(glParentFolder);

        txtParentFolder = new Label(parentFolderComposite, SWT.WRAP);
        txtParentFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnBrowseFolder = new Button(parentFolderComposite, SWT.FLAT);
        btnBrowseFolder.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        btnBrowseFolder.setText("Browse...");

        Label lblRequestType = new Label(container, SWT.NONE);
        lblRequestType.setText("Request Type");

        txtRequestType = new Label(container, SWT.NONE);
        txtRequestType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblRequestMethod = new Label(container, SWT.NONE);
        lblRequestMethod.setText("Request Method");

        txtRequestMethod = new Label(container, SWT.NONE);
        txtRequestMethod.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblUrl = new Label(container, SWT.NONE);
        lblUrl.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        lblUrl.setText("URL");

        txtUrl = new Label(container, SWT.WRAP);
        txtUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Label lblDescription = new Label(container, SWT.NONE);
        lblDescription.setText("Description");
        lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        txtDescription = new Text(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false);
        GC graphicContext = new GC(txtDescription);
        FontMetrics fm = graphicContext.getFontMetrics();
        gridData.heightHint = MAX_LINE_NUMBER * fm.getHeight();
        gridData.widthHint = 300;
        txtDescription.setLayoutData(gridData);
        graphicContext.dispose();

        return container;
    }

    private boolean isNameDupplicated(String newName) {
        return this.sibblingEntities.parallelStream().filter(l -> l.getName().equals(newName)).findAny().isPresent();
    }

    private String getSuggestion(String suggestion) {
        String newName = suggestion;
        int index = 0;

        while (isNameDupplicated(newName)) {
            index += 1;
            newName = String.format("%s %d", suggestion, index);
        }
        return newName;
    }

    private void checkNewName(String newName) {
        if (isNameDupplicated(newName)) { 
            setMessage(GlobalMessageConstants.DIA_NAME_EXISTED, IMessageProvider.ERROR);
            getButton(OK).setEnabled(false);
            return;
        }

        try {
            EntityNameController.getInstance().validateName(newName);
            setMessage("Enter a Web Serivce Request name", IMessageProvider.INFORMATION);
            getButton(OK).setEnabled(true);
        } catch (Exception e) {
            setMessage(e.getMessage(), IMessageProvider.ERROR);
            getButton(OK).setEnabled(false);
        }
    }
    
    @Override
    protected void okPressed() {
        result = new NewHistoryRequestResult();
        result.name = txtName.getText();
        result.description = txtDescription.getText();
        result.parentFolder = parentFolder;
        super.okPressed();
    }

    public NewHistoryRequestResult getResult() {
        return result;
    }

    private class ObjectRepositoryFolderViewerFilter extends EntityViewerFilter {

        public ObjectRepositoryFolderViewerFilter(EntityProvider entityProvider) {
            super(entityProvider);
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            return super.select(viewer, parentElement, element) && (element instanceof FolderTreeEntity);
        }
    }
    
    public static class NewHistoryRequestResult {
        private String name;
        
        private String description;

        private FolderEntity parentFolder;

        public String getName() {
            return name;
        }

        public FolderEntity getParentFolder() {
            return parentFolder;
        }

        public String getDescription() {
            return description;
        }
    }
    
    @Override
    public String getDialogTitle() {
        return "Save to Object Repository";
    }
    
    @Override
    protected Point getInitialSize() {
        Point preferredSize = super.getInitialSize();
        return new Point(Math.min(500, preferredSize.x), preferredSize.y);
    }
}
