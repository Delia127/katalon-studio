package com.kms.katalon.activation.dialog;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.RequestException;
import com.kms.katalon.application.utils.ServerAPICommunicationUtil;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.logging.LogUtil;

public class SignupDialog extends AbstractDialog {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public static final int REQUEST_ACTIVATION_CODE = 1000;
    
    public static final int REQUEST_OFFLINE_CODE = 1002;

    private Text txtUsername;

    private Text txtEmail;

    private Text txtPassword;

    private Label lblProgressMessage;

    private AuthenticationInfo authenticationInfo;

    private Link lnkSwitchToActiveDialog;

    private Link lnkConfigProxy;
    
    private Link lnkOfflineActivation;
    
    private Link lnkAgreeTerm;

    public SignupDialog(Shell parentShell) {
        super(parentShell, false);
    }

    public boolean validateInput() {
        return validateUsername() && validateEmail() && validatePassword();
    }

    private boolean validateUsername() {
        return !txtUsername.getText().isEmpty();
    }

    private boolean validateEmail() {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(txtEmail.getText()).find();
    }

    private boolean validatePassword() {
        return txtPassword.getText().length() >= 8;
    }

    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

    @Override
    protected void registerControlModifyListeners() {
        ModifyListener modifyListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getButton(OK).setEnabled(validateInput());
            }
        };

        txtUsername.addModifyListener(modifyListener);
        txtEmail.addModifyListener(modifyListener);
        txtPassword.addModifyListener(modifyListener);

        lnkSwitchToActiveDialog.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(REQUEST_ACTIVATION_CODE);
                close();
            }
        });

        lnkConfigProxy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new ProxyConfigurationDialog(getShell()).open();
            }
        });
        
        lnkOfflineActivation.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setReturnCode(REQUEST_OFFLINE_CODE);
                close();
            }
        });
        
        lnkAgreeTerm.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(StringConstants.AGREE_TERM_URL);
            }
        });
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        Composite contentComposite = new Composite(container, SWT.NONE);
        GridLayout glContent = new GridLayout(2, false);
        glContent.verticalSpacing = 10;
        contentComposite.setLayout(glContent);

        Label lblUsername = new Label(contentComposite, SWT.NONE);
        lblUsername.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblUsername.setText(MessageConstants.SignupDialog_LBL_FULL_NAME);

        txtUsername = new Text(contentComposite, SWT.BORDER);
        GridData gdText = new GridData(SWT.FILL, SWT.CENTER, true, false);
        gdText.heightHint = 22;
        txtUsername.setLayoutData(gdText);

        Label lblEmail = new Label(contentComposite, SWT.NONE);
        GridData gdEmail = new GridData(SWT.LEFT, SWT.TOP, false, false);
        gdEmail.verticalIndent = 5;
        lblEmail.setLayoutData(gdEmail);
        lblEmail.setText(StringConstants.EMAIL);

        Composite txtEmailComposite = new Composite(contentComposite, SWT.NONE);
        txtEmailComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout glTxtComposite = new GridLayout();
        glTxtComposite.marginHeight = 0;
        glTxtComposite.marginWidth = 0;
        txtEmailComposite.setLayout(glTxtComposite);

        txtEmail = new Text(txtEmailComposite, SWT.BORDER);
        txtEmail.setLayoutData(gdText);

        Label lblEmailNotification = new Label(txtEmailComposite, SWT.NONE);
        lblEmailNotification.setText(MessageConstants.SignupDialog_LBL_EMAIL_HINT);
        lblEmailNotification.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        lblEmailNotification.setForeground(ColorUtil.getHintForegroundColor());

        Label lblPassword = new Label(contentComposite, SWT.NONE);
        GridData gdPassword = new GridData(SWT.LEFT, SWT.TOP, false, false);
        gdPassword.verticalIndent = 5;
        lblPassword.setLayoutData(gdPassword);
        lblPassword.setText(StringConstants.PASSSWORD_TITLE);

        Composite txtPasswordComposite = new Composite(contentComposite, SWT.NONE);
        txtPasswordComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
        GridLayout glPasswordComposite = new GridLayout();
        glPasswordComposite.marginHeight = 0;
        glPasswordComposite.marginWidth = 0;
        txtPasswordComposite.setLayout(glPasswordComposite);

        txtPassword = new Text(txtPasswordComposite, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setLayoutData(gdText);

        Label lblPasswordNotification = new Label(txtPasswordComposite, SWT.NONE);
        lblPasswordNotification.setText(MessageConstants.SignupDialog_LBL_PASSWORD_HINT);
        lblPasswordNotification.setForeground(ColorUtil.getHintForegroundColor());
        lblPasswordNotification.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        lblProgressMessage = new Label(contentComposite, SWT.NONE);
        lblProgressMessage.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 2, 1));

        return container;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite bottomBar = new Composite(parent, SWT.NONE);
        bottomBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdBottomBar = new GridLayout(1, false);
        gdBottomBar.marginWidth = 0;
        bottomBar.setLayout(gdBottomBar);

        Composite bottomTerm = new Composite(bottomBar, SWT.NONE);
        bottomTerm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gdBottomBarTerm = new GridLayout(2, false);
        bottomTerm.setLayout(gdBottomBarTerm);
        
        lnkAgreeTerm = new Link(bottomTerm, SWT.WRAP);
        lnkAgreeTerm.setText(MessageConstants.ActivationDialogV2_LBL_AGREE_TERM_SIGNING_UP);
        
        Composite bottomLeftComposite = new Composite(bottomBar, SWT.NONE);
        bottomLeftComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bottomLeftComposite.setLayout(new GridLayout(6, false));

        Label lblAskForAccount = new Label(bottomLeftComposite, SWT.NONE);
        lblAskForAccount.setText(MessageConstants.SignupDialog_LBL_ASK_FOR_ACCOUNT);

        lnkSwitchToActiveDialog = new Link(bottomLeftComposite, SWT.NONE);
        lnkSwitchToActiveDialog.setText(String.format("<a>%s</a>", MessageConstants.SignupDialog_LNK_SIGN_IN));

        Label lblSeparator = new Label(bottomLeftComposite, SWT.SEPARATOR);
        GridData gdSeparator = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gdSeparator.heightHint = 22;
        lblSeparator.setLayoutData(gdSeparator);

        lnkConfigProxy = new Link(bottomLeftComposite, SWT.NONE);
        lnkConfigProxy.setText(MessageConstants.CONFIG_PROXY);
        lnkConfigProxy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        
        Label label = new Label(bottomLeftComposite, SWT.SEPARATOR);
        label.setLayoutData(gdSeparator);
        
        lnkOfflineActivation = new Link(bottomLeftComposite, SWT.NONE);
        lnkOfflineActivation
                .setText(String.format("<a>%s</a>", MessageConstants.SignupDialog_LNK_OFFLINE_ACTIVATION));
        lnkOfflineActivation.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

        Composite bottomRightComposite = new Composite(bottomBar, SWT.NONE);
        bottomRightComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = 0;
        bottomRightComposite.setLayout(gridLayout);

        createButtonsForButtonBar(bottomRightComposite);
        return bottomBar;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, MessageConstants.SignupDialog_BTN_SIGN_UP, true);
    }

    @Override
    protected void setInput() {
        getButton(OK).setEnabled(validateInput());
    }

    @Override
    protected Point getInitialSize() {
        Point initialSize = super.getInitialSize();
        return new Point(Math.max(500, initialSize.x), initialSize.y);
    }

    @Override
    public String getDialogTitle() {
        return MessageConstants.SignupDialog_DIA_TITLE;
    }

    @Override
    protected void okPressed() {
        getButton(OK).setEnabled(false);
        authenticationInfo = new AuthenticationInfo(txtUsername.getText(), txtEmail.getText(), txtPassword.getText());
        Thread thread = new Thread(() -> {
            try {
                createAccount(authenticationInfo);

                UISynchronizeService.syncExec(() -> SignupDialog.super.okPressed());

            } catch (UnknownHostException e) {
                UISynchronizeService.syncExec(() -> {
                    setProgressMessage(MessageConstants.SignupDialog_MSG_NETWORK_ERROR, true);
                    getButton(OK).setEnabled(true);
                });
            } catch (IOException | GeneralSecurityException | ActivationErrorException e) {
                UISynchronizeService.syncExec(() -> {
                    setProgressMessage(e.getMessage(), true);
                    getButton(OK).setEnabled(true);
                });    
            } catch (RequestException e) {
                UISynchronizeService.syncExec(() -> {
                    setProgressMessage(MessageConstants.SignupDialog_MSG_SIGNUP_REQUEST_FAILED, true);
                    getButton(OK).setEnabled(true);
                });
                
                try {
                    Program.launch(ServerAPICommunicationUtil.getSignupUrlWithActivationRedirectLink());
                } catch (UnsupportedEncodingException uee) {
                    LogUtil.logError(uee);
                }
            }
        });
        thread.start();
    }

    private void setProgressMessage(String message, boolean isError) {
        lblProgressMessage.setText(message);
        if (isError) {
            lblProgressMessage.setForeground(ColorUtil.getTextErrorColor());
        } else {
            lblProgressMessage.setForeground(ColorUtil.getDefaultTextColor());
        }
        lblProgressMessage.getParent().layout();
    }

    private void setSyncMessage(String message, boolean isError) {
        UISynchronizeService.syncExec(() -> setProgressMessage(message, isError));
    }

    private void createAccount(AuthenticationInfo authenticationInfo)
            throws IOException, GeneralSecurityException, ActivationErrorException, RequestException {
        setSyncMessage(MessageConstants.SignupDialog_MSG_CREATING_NEW_ACCOUNT, false);

        String token = ServerAPICommunicationUtil.invokeFormEncoded(ServerAPICommunicationUtil.getSignupAPIUrl(),
                "GET", getUrlEncodedRequestTokenBody());

        String signupBody = getUrlEncodedSignupBody(token, authenticationInfo);
        String signupResponse = ServerAPICommunicationUtil.invokeFormEncoded(
                ServerAPICommunicationUtil.getSignupAPIUrl(), "POST", signupBody);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SignupResponseBody.class, new SignupResponseBodyJsonDeserializer()).create();
        SignupResponseBody signupResponseBody = gson.fromJson(signupResponse, SignupResponseBody.class);
        if (signupResponseBody.getError()) {
            throw new ActivationErrorException(signupResponseBody.getMessage());
        }

        setSyncMessage(MessageConstants.SignupDialog_MSG_ACTIVATING_NEW_ACCOUNT, false);
        StringBuilder errorMessageBuilder = new StringBuilder();
        ActivationInfoCollector.activate(authenticationInfo.getEmail(), authenticationInfo.getPassword(),
                errorMessageBuilder);
        if (errorMessageBuilder.length() > 0) {
            throw new ActivationErrorException(errorMessageBuilder.toString());
        }

        // TODO: KAT-3523 Should enable if needed for further tracking
        // Program.launch(signupResponseBody.getDataOptions().getLink());
    }

    private static class ActivationErrorException extends Exception {
        private static final long serialVersionUID = -3281802999345073175L;

        public ActivationErrorException(String message) {
            super(message);
        }
    }

    public AuthenticationInfo getAuthenticationInfo() {
        return authenticationInfo;
    }

    public static class AuthenticationInfo {
        private final String fullName;

        private final String email;

        private final String password;

        public AuthenticationInfo(String fullName, String email, String password) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }

    public String getUrlEncodedRequestTokenBody() throws UnsupportedEncodingException {
        StringBuilder urlEncodedBuilder = new StringBuilder();
        urlEncodedBuilder.append("action=" + URLEncoder.encode("katalon_token_register", "utf-8"));
        return urlEncodedBuilder.toString();
    }

    public String getUrlEncodedSignupBody(String token, AuthenticationInfo authenticationInfo)
            throws UnsupportedEncodingException {
        StringBuilder urlEncodedBuilder = new StringBuilder();
        urlEncodedBuilder.append("user_email=" + URLEncoder.encode(authenticationInfo.getEmail(), "utf-8"));
        urlEncodedBuilder.append("&user_pass=" + URLEncoder.encode(authenticationInfo.getPassword(), "utf-8"));
        urlEncodedBuilder.append("&user_login=" + URLEncoder.encode(authenticationInfo.getFullName(), "utf-8"));
        urlEncodedBuilder.append("&action=" + URLEncoder.encode("katalon_register", "utf-8"));
        urlEncodedBuilder.append("&register-security=" + URLEncoder.encode(token, "utf-8"));
        urlEncodedBuilder.append("&_wp_http_referrer=" + URLEncoder.encode("/sign-up/", "utf-8"));
        urlEncodedBuilder.append("&user_agreement=" + URLEncoder.encode("on", "utf-8"));
        return urlEncodedBuilder.toString();
    }

    public static class SignupResponseBody {
        private boolean error;

        private String message;

        @SerializedName("data_options")
        private DataOptions dataOptions;

        public void setError(boolean error) {
            this.error = error;
        }

        public boolean getError() {
            return error;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public DataOptions getDataOptions() {
            return dataOptions;
        }

        public void setDataOptions(DataOptions dataOptions) {
            this.dataOptions = dataOptions;
        }
    }

    public static class DataOptions {
        private String link;

        public String getLink() {
            return link;
        }
    }

    public static class SignupResponseBodyJsonDeserializer implements JsonDeserializer<SignupResponseBody> {

        @Override
        public SignupResponseBody deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            if (!(jsonElement instanceof JsonObject)) {
                return null;
            }
            SignupResponseBody signupResponse = new SignupResponseBody();
            JsonObject jsObject = (JsonObject) jsonElement;
            if (jsObject.has("error")) {
                signupResponse.setError(jsObject.get("error").getAsBoolean());
            }
            if (jsObject.has("message")) {
                signupResponse.setMessage(jsObject.get("message").getAsString());
            }
            if (jsObject.has("data_options")) {
                JsonElement jsDataOptions = jsObject.get("data_options");
                if (jsDataOptions.isJsonObject()) {
                    signupResponse.setDataOptions(JsonUtil.fromJson(jsDataOptions.toString(), DataOptions.class));
                }
            }
            return signupResponse;
        }
    }
}
