package com.kms.katalon.composer.checkpoint.dialogs.wizard;

import java.io.IOException;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.controller.DatabaseController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.db.DatabaseConnection;
import com.kms.katalon.core.util.internal.Base64;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.DatabaseCheckpointSourceInfo;

public class NewCheckpointDatabasePage extends AbstractCheckpointWizardPage {

    private static final String WIZ_DATABASE_SOURCE_CONFIGURATION = com.kms.katalon.composer.checkpoint.constants.StringConstants.WIZ_DATABASE_SOURCE_CONFIGURATION;

    private static final String DATABASE = com.kms.katalon.composer.checkpoint.constants.StringConstants.WIZ_TITLE_DATABASE;

    private static final String JDBC_PROTOCOL = "jdbc:";

    protected Button chkGlobalDBSetting;

    protected Button chkSecureUserPassword;

    protected Text txtUser;

    protected Text txtPassword;

    protected Button btnTestConnection;

    protected Label lblStatus;

    protected Text txtConnectionURL;

    protected Text txtQuery;

    private GridData gdLblOptionsDB;

    private GridData gdTxtDriverClassName;

    private Label lblOptionsDB;

    protected Text txtDriverClassName;

    private Composite compDatabase;

    public NewCheckpointDatabasePage() {
        super(NewCheckpointDatabasePage.class.getSimpleName(), DATABASE, WIZ_DATABASE_SOURCE_CONFIGURATION);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout();
        glContainer.marginHeight = 0;
        glContainer.marginWidth = 0;
        container.setLayout(glContainer);

        createDatabasePart(container);
        createQueryPart(container);

        setControlListeners();
        setControl(container);
        setInput();
        setPageComplete(isComplete());
    }

    private void setInput() {
        enableCustomDBConnection(!chkGlobalDBSetting.getSelection());
    }

    protected void showDriverComposite() {
        if (!LicenseUtil.isNotFreeLicense()) {
            gdLblOptionsDB.heightHint = 0;
            gdTxtDriverClassName.heightHint = 0;
            txtDriverClassName.setVisible(false);
            lblOptionsDB.setVisible(false);
            compDatabase.layout(true);
        }
    }
    
    private void createDatabasePart(Composite parent) {
        compDatabase = new Composite(parent, SWT.NONE);
        compDatabase.setLayout(new GridLayout());
        compDatabase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Group grpDatabase = new Group(compDatabase, SWT.NONE);
        grpDatabase.setLayout(new GridLayout(2, false));
        grpDatabase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpDatabase.setText(StringConstants.DIA_GRP_DATABASE_CONNECTION);

        chkGlobalDBSetting = new Button(grpDatabase, SWT.CHECK);
        chkGlobalDBSetting.setText(StringConstants.DIA_CHK_USE_GLOBAL_DB_SETTINGS);
        chkGlobalDBSetting.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        new Label(grpDatabase, SWT.NONE);
        chkSecureUserPassword = new Button(grpDatabase, SWT.CHECK);
        chkSecureUserPassword.setText(StringConstants.DIA_CHK_SECURE_USER_PASSWORD);
        chkSecureUserPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblUser = new Label(grpDatabase, SWT.NONE);
        lblUser.setText(StringConstants.DIA_LBL_USER);
        lblUser.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));

        txtUser = new Text(grpDatabase, SWT.BORDER);
        txtUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPassword = new Label(grpDatabase, SWT.NONE);
        lblPassword.setText(StringConstants.DIA_LBL_PASSWORD);
        lblPassword.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));

        txtPassword = new Text(grpDatabase, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        lblOptionsDB = new Label(grpDatabase, SWT.NONE);
        lblOptionsDB.setText("JDBC driver");
        gdLblOptionsDB = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblOptionsDB.setLayoutData(gdLblOptionsDB);

        txtDriverClassName = new Text(grpDatabase, SWT.BORDER);
        gdTxtDriverClassName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        txtDriverClassName.setLayoutData(gdTxtDriverClassName);

        Label lblConnectionURL = new Label(grpDatabase, SWT.NONE);
        lblConnectionURL.setLayoutData(new GridData(SWT.LEAD, SWT.TOP, false, false, 1, 1));
        lblConnectionURL.setText(StringConstants.DIA_LBL_CONNECTION_URL);

        txtConnectionURL = new Text(grpDatabase, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        layoutData.heightHint = 80;
        txtConnectionURL.setLayoutData(layoutData);

        new Label(grpDatabase, SWT.NONE);
        Composite compTestConn = new Composite(grpDatabase, SWT.NONE);
        GridLayout glTestConn = new GridLayout(2, false);
        glTestConn.marginHeight = 0;
        glTestConn.marginWidth = 0;
        compTestConn.setLayout(glTestConn);
        compTestConn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnTestConnection = new Button(compTestConn, SWT.NONE);
        btnTestConnection.setText(StringConstants.DIA_BTN_TEST_CONNECTION);

        lblStatus = new Label(compTestConn, SWT.NONE);
        lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        createConnectionUrlSample(grpDatabase);
    }

    private void createConnectionUrlSample(Composite parent) {
        Composite compSample = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        compSample.setLayout(layout);
        compSample.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Label lblSampleURL = new Label(compSample, SWT.NONE);
        lblSampleURL.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));
        lblSampleURL.setText(StringConstants.DIA_LBL_CONNECTION_URL_SAMPLE);

        Label separator = new Label(compSample, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        addLink(parent, StringConstants.DIA_LNK_MYSQL, StringConstants.DIA_LINK_MYSQL_DOC);
        Text txtMySQLURL = new Text(parent, SWT.READ_ONLY);
        txtMySQLURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtMySQLURL.setText(StringConstants.DIA_TXT_MYSQL_SAMPLE_LINK);

        addLink(parent, StringConstants.DIA_LNK_SQL_SERVER, StringConstants.DIA_LINK_SQL_SERVER_DOC);
        Text txtSQLServerURL = new Text(parent, SWT.READ_ONLY);
        txtSQLServerURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtSQLServerURL.setText(StringConstants.DIA_TXT_SQL_SERVER_SAMPLE_LINK);

        addLink(parent, StringConstants.DIA_LNK_ORACLE_SQL, StringConstants.DIA_LINK_ORACLE_SQL_DOC);
        Text txtOracleSQLURL1 = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
        txtOracleSQLURL1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtOracleSQLURL1.setText(StringConstants.DIA_TXT_ORACLE_SQL_SAMPLE_THIN_LINK);

        new Label(parent, SWT.NONE);
        Text txtOracleSQLURL2 = new Text(parent, SWT.READ_ONLY);
        txtOracleSQLURL2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtOracleSQLURL2.setText(StringConstants.DIA_TXT_ORACLE_SQL_SAMPLE_OCI_LINK);

        addLink(parent, StringConstants.DIA_LNK_POSTGRESQL, StringConstants.DIA_LINK_POSTGRESQL_DOC);
        Text txtPostgreSQLURL = new Text(parent, SWT.READ_ONLY);
        txtPostgreSQLURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtPostgreSQLURL.setText(StringConstants.DIA_TXT_POSTGRESQL_SAMPLE_LINK);
    }

    private void createQueryPart(Composite parent) {
        Composite compQuery = new Composite(parent, SWT.NONE);
        compQuery.setLayout(new GridLayout());
        compQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Group grpQuery = new Group(compQuery, SWT.NONE);
        grpQuery.setLayout(new GridLayout());
        grpQuery.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        grpQuery.setText(StringConstants.DIA_LBL_SQL_QUERY);

        txtQuery = new Text(grpQuery, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        layoutData.minimumHeight = 80;
        txtQuery.setLayoutData(layoutData);
    }

    private void setControlListeners() {
        chkGlobalDBSetting.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                enableCustomDBConnection(!chkGlobalDBSetting.getSelection());
                setPageComplete(isComplete());
            }
        });

        chkSecureUserPassword.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                enableUserPassword(chkSecureUserPassword.getSelection());
            }
        });

        btnTestConnection.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ClassLoader oldClassLoader = null;
                DatabaseConnection dbConn = getDatabaseConnection();

                try {
                    oldClassLoader = Thread.currentThread().getContextClassLoader();
                    // fetch data and load into table
                    URLClassLoader projectClassLoader = ProjectController.getInstance()
                            .getProjectClassLoader(ProjectController.getInstance().getCurrentProject());
                    Thread.currentThread().setContextClassLoader(projectClassLoader);
                    if (chkGlobalDBSetting.getSelection()) {
                        dbConn = DatabaseController.getInstance().getGlobalDatabaseConnection();
                    }
                    if (dbConn == null) {
                        setStatusLabel(MessageFormat.format(StringConstants.DIA_LBL_TEST_STATUS_FAIL,
                                StringConstants.DIA_MSG_CONNECTION_EMPTY), ColorUtil.getTextErrorColor());
                        return;
                    }
                    dbConn.getConnection();
                    if (!dbConn.isAlive()) {
                        setStatusLabel(MessageFormat.format(StringConstants.DIA_LBL_TEST_STATUS_FAIL,
                                StringConstants.DIA_LBL_CONNECTION_CLOSED), ColorUtil.getTextErrorColor());
                        return;
                    }
                    setStatusLabel(StringConstants.DIA_LBL_TEST_STATUS_SUCCESS, ColorUtil.getTextSuccessfulColor());
                } catch (SQLException | IOException | CoreException ex) {
                    setStatusLabel(MessageFormat.format(StringConstants.DIA_LBL_TEST_STATUS_FAIL, ex.getMessage()),
                            ColorUtil.getTextErrorColor());
                } finally {
                    if (dbConn != null) {
                        dbConn.close();
                    }
                    if (oldClassLoader != null) {
                        Thread.currentThread().setContextClassLoader(oldClassLoader);
                    }
                }
            }
        });

        lblStatus.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                if (lblStatus.getText().isEmpty()) {
                    return;
                }
                MessageDialog.openInformation(getShell(), StringConstants.DIA_TITLE_STATUS_DETAILS,
                        lblStatus.getToolTipText());
            }
        });

        txtUser.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setPageComplete(isComplete());
            }
        });

        txtConnectionURL.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setPageComplete(isComplete());
            }
        });

        txtQuery.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setPageComplete(isComplete());
            }
        });
    }

    private DatabaseConnection getDatabaseConnection() {
        String user = null;
        String password = null;
        if (chkSecureUserPassword.getSelection()) {
            user = txtUser.getText();
            password = txtPassword.getText();
        }
        if (!StringUtils.startsWithIgnoreCase(txtConnectionURL.getText(), JDBC_PROTOCOL)) {
            return null;
        }
        return new DatabaseConnection(txtConnectionURL.getText(), user, password, txtDriverClassName.getText());
    }

    protected void enableCustomDBConnection(boolean enabled) {
        chkSecureUserPassword.setEnabled(enabled);
        enableUserPassword(enabled && chkSecureUserPassword.getSelection());
        txtConnectionURL.setEnabled(enabled);
        txtDriverClassName.setEnabled(enabled);
    }

    private void enableUserPassword(boolean enabled) {
        txtUser.setEnabled(enabled);
        txtPassword.setEnabled(enabled);
    }

    private void setStatusLabel(String msg, Color msgColor) {
        lblStatus.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
        lblStatus.setForeground(msgColor);
        lblStatus.setText(StringUtils.abbreviate(msg, 80));
        lblStatus.setToolTipText(msg);
    }

    private Link addLink(Composite parent, String label, final String hyperlink) {
        Link link = new Link(parent, SWT.NONE);
        link.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));
        link.setText(label + " (<a href=\"" + hyperlink + "\">Docs</a>)");
        link.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(hyperlink);
            }
        });
        return link;
    }

    @Override
    protected boolean isComplete() {
        boolean isQueryNotBlank = StringUtils.isNotBlank(txtQuery.getText());
        if (chkGlobalDBSetting.getSelection()) {
            return isQueryNotBlank;
        }

        if (chkSecureUserPassword.getSelection()) {
            isQueryNotBlank &= StringUtils.isNotBlank(txtUser.getText());
            // note that password can be empty or null
        }

        return isQueryNotBlank && StringUtils.startsWith(txtConnectionURL.getText(), JDBC_PROTOCOL);
    }

    @Override
    public Point getPageSize() {
        return getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
    }

    public CheckpointSourceInfo getSourceInfo() {
        if (this.equals(getContainer().getCurrentPage())) {
            return new DatabaseCheckpointSourceInfo(txtConnectionURL.getText(), chkGlobalDBSetting.getSelection(),
                    chkSecureUserPassword.getSelection(), txtUser.getText(), Base64.encode(txtPassword.getText()),
                    txtQuery.getText(), txtDriverClassName.getText());
        }
        return new DatabaseCheckpointSourceInfo();
    }

}
