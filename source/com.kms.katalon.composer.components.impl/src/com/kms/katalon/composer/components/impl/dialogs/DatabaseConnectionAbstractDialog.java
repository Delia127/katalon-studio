package com.kms.katalon.composer.components.impl.dialogs;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.impl.constants.ComposerComponentsImplMessageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.DatabaseController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.db.DatabaseConnection;
import com.kms.katalon.license.models.LicenseType;

public abstract class DatabaseConnectionAbstractDialog extends AbstractDialog {

    private static final String JDBC_PROTOCOL = "jdbc:";

    protected Button chkGlobalDBSetting;

    protected Button chkSecureUserPassword;

    protected Text txtUser;

    protected Text txtPassword;

    protected Button btnTestConnection;

    protected Label lblStatus;

    protected Text txtConnectionURL;

    protected Text txtQuery;
    
    protected Text txtDriverClassName;

    protected boolean isChanged;

    private GridData gdCompositeDriver;

    private Composite compDatabase;

    public DatabaseConnectionAbstractDialog(Shell parentShell) {
        super(parentShell);
        setDialogTitle(StringConstants.DIA_TITLE_DB_CONNECTION_QUERY_SETTINGS);
    }

    public boolean isChanged() {
        return isChanged;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout glMain = new GridLayout();
        glMain.marginHeight = 0;
        glMain.marginWidth = 0;
        main.setLayout(glMain);

        createDatabasePart(main);
        createQueryPart(main);
        return parent;
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
        lblUser.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtUser = new Text(grpDatabase, SWT.BORDER);
        txtUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPassword = new Label(grpDatabase, SWT.NONE);
        lblPassword.setText(StringConstants.DIA_LBL_PASSWORD);
        lblPassword.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtPassword = new Text(grpDatabase, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite compositeDriver = new Composite(compDatabase, SWT.NONE);
        gdCompositeDriver = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        compositeDriver.setLayoutData(gdCompositeDriver);
        GridLayout glCompositeDriver = new GridLayout(2, false);
        glCompositeDriver.marginWidth = 0;
        glCompositeDriver.marginHeight = 0;
        compositeDriver.setLayout(glCompositeDriver);

        Label lblOptionsDB = new Label(compositeDriver, SWT.NONE);
        lblOptionsDB.setText("JDBC driver");
        lblOptionsDB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtDriverClassName = new Text(compositeDriver, SWT.BORDER);
        txtDriverClassName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblConnectionURL = new Label(grpDatabase, SWT.NONE);
        lblConnectionURL.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
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
        lblSampleURL.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
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

    @Override
    protected void registerControlModifyListeners() {
        chkGlobalDBSetting.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setChanged();
                enableCustomDBConnection(!chkGlobalDBSetting.getSelection());
            }
        });

        chkSecureUserPassword.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setChanged();
                enableUserPassword(chkSecureUserPassword.getSelection());
            }
        });

        final ModifyListener textModifyListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setChanged();
            }
        };

        txtUser.addModifyListener(textModifyListener);

        txtPassword.addModifyListener(textModifyListener);

        txtConnectionURL.addModifyListener(textModifyListener);

        txtQuery.addModifyListener(textModifyListener);
        
        txtDriverClassName.addModifyListener(textModifyListener);

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
    }

    private DatabaseConnection getDatabaseConnection() {
        String user = null;
        String password = null;
        String driverClassName = null;
        if (chkSecureUserPassword.getSelection()) {
            user = txtUser.getText();
            password = txtPassword.getText();
            driverClassName = txtDriverClassName.getText();
        }
        if (!StringUtils.startsWithIgnoreCase(txtConnectionURL.getText(), JDBC_PROTOCOL)) {
            return null;
        }
        return new DatabaseConnection(txtConnectionURL.getText(), user, password,driverClassName);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(800, super.getInitialSize().y);
    }

    @Override
    protected int getShellStyle() {
        return SWT.PRIMARY_MODAL | SWT.TITLE | SWT.MAX | SWT.CLOSE;
    }

    @Override
    protected void okPressed() {
        String connectionUrl = txtConnectionURL.getText();
        if (!isEnterpriseAccount()) {
            if (isOracleSql(connectionUrl)) {
                MessageDialog.openWarning(getShell(), GlobalStringConstants.INFO,
                        ComposerComponentsImplMessageConstants.PREF_WARN_KSE_ORACLE_SQL);
                return;
            }

            if (isMicrosoftSqlServer(connectionUrl)) {
                MessageDialog.openWarning(getShell(), GlobalStringConstants.INFO,
                        ComposerComponentsImplMessageConstants.PREF_WARN_KSE_SQL_SERVER);
                return;
            }
        }

        updateChanges();
        super.okPressed();
    }

    protected abstract void updateChanges();

    private void setChanged() {
        if (isChanged) {
            return;
        }

        this.isChanged = true;
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
        link.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        link.setText(label + " (<a href=\"" + hyperlink + "\">Docs</a>)");
        link.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(hyperlink);
            }
        });
        return link;
    }

    protected boolean isEnterpriseAccount() {
        return LicenseType.valueOf(
                ApplicationInfo.getAppProperty(ApplicationStringConstants.LICENSE_TYPE)) != LicenseType.FREE;
    }

    private boolean isOracleSql(String connectionUrl) {
        if (StringUtils.isEmpty(connectionUrl)) {
            return false;
        }
        return connectionUrl.startsWith("jdbc:oracle");
    }

    private boolean isMicrosoftSqlServer(String connectionUrl) {
        if (StringUtils.isEmpty(connectionUrl)) {
            return false;
        }
        return connectionUrl.startsWith("jdbc:sqlserver");
    }

    protected void showDriverComposite() {
        if (!isEnterpriseAccount()) {
            gdCompositeDriver.heightHint = 0;
            compDatabase.layout(true);
        }
    }
}
