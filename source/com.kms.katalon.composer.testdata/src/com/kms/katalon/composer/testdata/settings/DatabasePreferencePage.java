package com.kms.katalon.composer.testdata.settings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.application.utils.LicenseUtil;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.constants.ComposerComponentsImplMessageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.db.DatabaseConnection;
import com.kms.katalon.core.db.DatabaseSettings;
import com.kms.katalon.core.setting.PropertySettingStoreUtil;

public class DatabasePreferencePage extends PreferencePageWithHelp {

    private static final String PROJECT_DIR = ProjectController.getInstance().getCurrentProject().getFolderLocation();

    private static final String SETTING_NAME = DatabaseSettings.class.getName();

    private Button chkSecureUserPassword;

    private Text txtUser;

    private Text txtPassword;

    private Text txtConnectionURL;
    
    private Text txtDriverClassName;

    private Button btnTestConnection;

    private Label lblStatus;

    private boolean isLoaded;

    private DatabaseSettings dbSettings;

    private Composite compContainer;

    private GridData gdLblOptionsDB;

    private GridData gdTxtDriverClassName;

    private Label lblOptionsDB;

    @Override
    protected Control createContents(Composite parent) {
        compContainer = new Composite(parent, SWT.NONE);
        compContainer.setLayout(new GridLayout());
        compContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite compDatabase = new Composite(compContainer, SWT.NONE);
        compDatabase.setLayout(new GridLayout(2, false));
        compDatabase.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        new Label(compDatabase, SWT.NONE);
        chkSecureUserPassword = new Button(compDatabase, SWT.CHECK);
        chkSecureUserPassword.setText(StringConstants.DIA_CHK_SECURE_USER_PASSWORD);
        chkSecureUserPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblUser = new Label(compDatabase, SWT.NONE);
        lblUser.setText(StringConstants.DIA_LBL_USER);
        lblUser.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));

        txtUser = new Text(compDatabase, SWT.BORDER);
        txtUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblPassword = new Label(compDatabase, SWT.NONE);
        lblPassword.setText(StringConstants.DIA_LBL_PASSWORD);
        lblPassword.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));

        txtPassword = new Text(compDatabase, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        lblOptionsDB = new Label(compDatabase, SWT.NONE);
        lblOptionsDB.setText("JDBC driver");
        gdLblOptionsDB = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        lblOptionsDB.setLayoutData(gdLblOptionsDB);

        txtDriverClassName = new Text(compDatabase, SWT.BORDER);
        gdTxtDriverClassName = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        txtDriverClassName.setLayoutData(gdTxtDriverClassName);

        Label lblConnectionURL = new Label(compDatabase, SWT.NONE);
        lblConnectionURL.setLayoutData(new GridData(SWT.LEAD, SWT.TOP, false, false, 1, 1));
        lblConnectionURL.setText(StringConstants.DIA_LBL_CONNECTION_URL);

        txtConnectionURL = new Text(compDatabase, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        layoutData.heightHint = 80;
        txtConnectionURL.setLayoutData(layoutData);

        new Label(compDatabase, SWT.NONE);
        Composite compTestConn = new Composite(compDatabase, SWT.NONE);
        GridLayout glTestConn = new GridLayout(2, false);
        glTestConn.marginHeight = 0;
        glTestConn.marginWidth = 0;
        compTestConn.setLayout(glTestConn);
        compTestConn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        btnTestConnection = new Button(compTestConn, SWT.NONE);
        btnTestConnection.setText(StringConstants.DIA_BTN_TEST_CONNECTION);

        lblStatus = new Label(compTestConn, SWT.NONE);
        lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        createConnectionUrlSample(compContainer);

        registerControlModifyListeners();
        loadSettings();
        isLoaded = true;
        return parent;
    }

    private void createConnectionUrlSample(Composite parent) {
        Composite compSample = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        compSample.setLayout(layout);
        compSample.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Label lblSampleURL = new Label(compSample, SWT.NONE);
        lblSampleURL.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));
        lblSampleURL.setText(StringConstants.DIA_LBL_CONNECTION_URL_SAMPLE);
        ControlUtils.setFontToBeBold(lblSampleURL);

        Label separator = new Label(compSample, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        addLink(compSample, StringConstants.DIA_LNK_MYSQL, StringConstants.DIA_LINK_MYSQL_DOC);
        Text txtMySQLURL = new Text(compSample, SWT.READ_ONLY);
        txtMySQLURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtMySQLURL.setText(StringConstants.DIA_TXT_MYSQL_SAMPLE_LINK);

        addLink(compSample, StringConstants.DIA_LNK_SQL_SERVER, StringConstants.DIA_LINK_SQL_SERVER_DOC);
        Text txtSQLServerURL = new Text(compSample, SWT.READ_ONLY);
        txtSQLServerURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtSQLServerURL.setText(StringConstants.DIA_TXT_SQL_SERVER_SAMPLE_LINK);

        addLink(compSample, StringConstants.DIA_LNK_ORACLE_SQL, StringConstants.DIA_LINK_ORACLE_SQL_DOC);
        Text txtOracleSQLURL1 = new Text(compSample, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
        txtOracleSQLURL1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtOracleSQLURL1.setText(StringConstants.DIA_TXT_ORACLE_SQL_SAMPLE_THIN_LINK);

        new Label(compSample, SWT.NONE);
        Text txtOracleSQLURL2 = new Text(compSample, SWT.READ_ONLY);
        txtOracleSQLURL2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtOracleSQLURL2.setText(StringConstants.DIA_TXT_ORACLE_SQL_SAMPLE_OCI_LINK);

        addLink(compSample, StringConstants.DIA_LNK_POSTGRESQL, StringConstants.DIA_LINK_POSTGRESQL_DOC);
        Text txtPostgreSQLURL = new Text(compSample, SWT.READ_ONLY);
        txtPostgreSQLURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtPostgreSQLURL.setText(StringConstants.DIA_TXT_POSTGRESQL_SAMPLE_LINK);
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

	protected void registerControlModifyListeners() {
		chkSecureUserPassword.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				enableUserPassword(((Button) e.getSource()).getSelection());
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
				} catch (SQLException | MalformedURLException | CoreException ex) {
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
        if (!StringUtils.startsWithIgnoreCase(txtConnectionURL.getText(), "jdbc")) {
            return null;
        }
        return new DatabaseConnection(txtConnectionURL.getText(), user, password, driverClassName);
    }

    private void setStatusLabel(String msg, Color msgColor) {
        lblStatus.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
        lblStatus.setForeground(msgColor);
        lblStatus.setText(StringUtils.abbreviate(msg, 80));
        lblStatus.setToolTipText(msg);
    }

    private void loadSettings() {
        try {
            dbSettings = new DatabaseSettings(PROJECT_DIR);
            chkSecureUserPassword.setSelection(dbSettings.isSecureUserAccount());
            txtUser.setText(StringUtils.defaultString(dbSettings.getUser()));
            txtPassword.setText(StringUtils.defaultString(dbSettings.getPassword()));
            txtConnectionURL.setText(StringUtils.defaultString(dbSettings.getUrl()));
            txtDriverClassName.setText(StringUtils.defaultString(dbSettings.getDriverClassName()));
            enableUserPassword(chkSecureUserPassword.getSelection());
            
            // Hide this feature for normal users
            if (!isEnterpriseAccount()) {
                gdLblOptionsDB.heightHint = 0;
                gdTxtDriverClassName.heightHint = 0;
                lblOptionsDB.setVisible(false);
                txtDriverClassName.setVisible(false);
                compContainer.layout(true, true);
            }
        } catch (IOException e) {
            setStatusLabel(e.getMessage(), ColorUtil.getTextErrorColor());
        }
    }

    private void enableUserPassword(boolean enabled) {
        txtUser.setEnabled(enabled);
        txtPassword.setEnabled(enabled);
    }

    @Override
    protected void performDefaults() {
        // restore default settings
        chkSecureUserPassword.setSelection(false);
        txtUser.setText(StringUtils.EMPTY);
        txtPassword.setText(StringUtils.EMPTY);
        txtConnectionURL.setText(StringUtils.EMPTY);
    }

    @Override
    public boolean performOk() {
        if (!isLoaded) {
            return true;
        }

        // save preferences
        dbSettings.setSecureUserAccount(chkSecureUserPassword.getSelection());
        dbSettings.setUser(txtUser.getText());
        dbSettings.setPassword(txtPassword.getText());
        String connectionUrl = txtConnectionURL.getText();
        dbSettings.setUrl(connectionUrl);
        dbSettings.setDriverClassName(txtDriverClassName.getText());
        if (!isEnterpriseAccount()) {
            if (isOracleSql(connectionUrl)) {
                MessageDialog.openWarning(getShell(), GlobalStringConstants.INFO,
                        ComposerComponentsImplMessageConstants.PREF_WARN_KSE_ORACLE_SQL);
                return false;
            }

            if (isMicrosoftSqlServer(connectionUrl)) {
                MessageDialog.openWarning(getShell(), GlobalStringConstants.INFO,
                        ComposerComponentsImplMessageConstants.PREF_WARN_KSE_SQL_SERVER);
                return false;
            }
        }

        try {
            PropertySettingStoreUtil.saveExternalSettings(PROJECT_DIR, SETTING_NAME, dbSettings.getSettings(),
                    com.kms.katalon.composer.testdata.constants.StringConstants.DIA_DB_SETTING_COMMENT);
            return true;
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.ERROR,
                    com.kms.katalon.composer.testdata.constants.StringConstants.DIA_MSG_UNABLE_TO_SAVE_DB_SETTING_PAGE);
            return false;
        }
    }
    
    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTINGS_DATABASE;
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

    private boolean isEnterpriseAccount() {
        return LicenseUtil.isNotFreeLicense();
    }
}
