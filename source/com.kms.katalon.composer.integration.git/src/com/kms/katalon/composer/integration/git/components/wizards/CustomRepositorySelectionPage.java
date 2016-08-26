package com.kms.katalon.composer.integration.git.components.wizards;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.TreeMap;

import org.eclipse.egit.core.securestorage.UserPasswordCredentials;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.UIUtils;
import org.eclipse.egit.ui.internal.KnownHosts;
import org.eclipse.egit.ui.internal.SecureStoreUtils;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.components.RepositorySelection;
import org.eclipse.egit.ui.internal.provisional.wizards.GitRepositoryInfo;
import org.eclipse.egit.ui.internal.provisional.wizards.IRepositorySearchResult;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.TransportProtocol;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.FS;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.integration.git.constants.GitStringConstants;

/**
 * Wizard page that allows the user entering the location of a remote repository
 * by specifying URL manually or selecting a preconfigured remote repository.
 */
@SuppressWarnings("restriction")
public class CustomRepositorySelectionPage extends WizardPage implements IRepositorySearchResult {

    private static final String GIT_CLONE_COMMAND_PREFIX = "git clone "; //$NON-NLS-1$

    private static final String EMPTY_STRING = "";  //$NON-NLS-1$

    private final String presetUri;

    private Text uriText;

    private Text userText;

    private Text passText;

    private Button storeCheckbox;

    private int eventDepth;

    private URIish uri;

    private RepositorySelection selection;

    private Composite uriPanel;

    private String user = EMPTY_STRING;

    private String password = EMPTY_STRING;

    private boolean storeInSecureStore;

    private static final RepositorySelection INVALID_SELECTION = new RepositorySelection(null, null);

    /**
     * Transport protocol abstraction
     *
     * TODO rework this to become part of JGit API
     */
    public static class Protocol {
        /** Ordered list of all protocols **/
        private static final TreeMap<String, Protocol> protocols = new TreeMap<>();

        /** Git native transfer */
        public static final Protocol GIT = new Protocol("git", //$NON-NLS-1$
                UIText.RepositorySelectionPage_tip_git, true, true, false);

        /** Git over SSH */
        public static final Protocol SSH = new Protocol("ssh", //$NON-NLS-1$
                UIText.RepositorySelectionPage_tip_ssh, true, true, true) {
            @Override
            public boolean handles(URIish uri) {
                if (!uri.isRemote()) {
                    return false;
                }
                final String scheme = uri.getScheme();
                if (getDefaultScheme().equals(scheme)) {
                    return true;
                }
                if ("ssh+git".equals(scheme)) { //$NON-NLS-1$ 
                    return true;
                }
                if ("git+ssh".equals(scheme)) {//$NON-NLS-1$
                    return true;
                }
                if (scheme == null && uri.getHost() != null && uri.getPath() != null) {
                    return true;
                }
                return false;
            }
        };

        /** Secure FTP */
        public static final Protocol SFTP = new Protocol("sftp", //$NON-NLS-1$
                UIText.RepositorySelectionPage_tip_sftp, true, true, true);

        /** HTTP */
        public static final Protocol HTTP = new Protocol("http", //$NON-NLS-1$
                UIText.RepositorySelectionPage_tip_http, true, true, true);

        /** Secure HTTP */
        public static final Protocol HTTPS = new Protocol("https", //$NON-NLS-1$
                UIText.RepositorySelectionPage_tip_https, true, true, true);

        /** FTP */
        public static final Protocol FTP = new Protocol("ftp", //$NON-NLS-1$
                UIText.RepositorySelectionPage_tip_ftp, true, true, true);

        /** Local repository */
        public static final Protocol FILE = new Protocol("file", //$NON-NLS-1$
                UIText.RepositorySelectionPage_tip_file, false, false, false) {
            @Override
            public boolean handles(URIish uri) {
                if (getDefaultScheme().equals(uri.getScheme())) {
                    return true;
                }
                if (uri.getHost() != null || uri.getPort() > 0 || uri.getUser() != null || uri.getPass() != null
                        || uri.getPath() == null) {
                    return false;
                }
                if (uri.getScheme() == null) {
                    return FS.DETECTED.resolve(new File("."), uri.getPath()).isDirectory(); //$NON-NLS-1$
                }
                return false;
            }
        };

        private final String defaultScheme;

        private final String tooltip;

        private final boolean hasHost;

        private final boolean hasPort;

        private final boolean canAuthenticate;

        private Protocol(String defaultScheme, String tooltip, boolean hasHost, boolean hasPort, boolean canAuthenticate) {
            this.defaultScheme = defaultScheme;
            this.tooltip = tooltip;
            this.hasHost = hasHost;
            this.hasPort = hasPort;
            this.canAuthenticate = canAuthenticate;
            protocols.put(defaultScheme, this);
        }

        /**
         * @param uri
         * URI to match against this protocol
         * @return {@code true} if the uri is handled by this protocol
         */
        public boolean handles(URIish uri) {
            return getDefaultScheme().equals(uri.getScheme());
        }

        /**
         * @return the default protocol scheme
         */
        public String getDefaultScheme() {
            return defaultScheme;
        }

        /**
         * @return the tooltip text describing the protocol
         */
        public String getTooltip() {
            return tooltip;
        }

        /**
         * @return true if protocol has host segment
         */
        public boolean hasHost() {
            return hasHost;
        }

        /**
         * @return true if protocol has port
         */
        public boolean hasPort() {
            return hasPort;
        }

        /**
         * @return true if protocol can authenticate
         */
        public boolean canAuthenticate() {
            return canAuthenticate;
        }

        /**
         * @return all protocols
         */
        public static Protocol[] values() {
            return protocols.values().toArray(new Protocol[protocols.size()]);
        }

        /**
         * Lookup protocol supporting given default URL scheme
         *
         * @param scheme
         * default scheme to lookup protocol for
         * @return protocol matching scheme or null
         */
        public static Protocol fromDefaultScheme(String scheme) {
            return protocols.get(scheme);
        }

        /**
         * Lookup protocol handling given URI
         *
         * @param uri URI to lookup protocol for
         * @return protocol handling this URI
         */
        public static Protocol fromUri(URIish uri) {
            for (Protocol p : protocols.values()) {
                if (p.handles(uri)) {
                    return p;
                }
            }
            return null;
        }
    }

    /**
     * Create repository selection page, allowing user specifying URI or
     * (optionally) choosing from preconfigured remotes list.
     * <p>
     * Wizard page is created without image, just with text description.
     *
     * @param sourceSelection
     * true if dialog is used for source selection; false otherwise
     * (destination selection). This indicates appropriate text
     * messages.
     * @param configuredRemotes
     * list of configured remotes that user may select as an
     * alternative to manual URI specification. Remotes appear in
     * given order in GUI, with {@value Constants#DEFAULT_REMOTE_NAME} as the default choice.
     * List may be null or empty - no remotes configurations appear
     * in this case. Note that the provided list may be changed by
     * this constructor.
     * @param presetUri
     * the pre-set URI, may be null
     */
    public CustomRepositorySelectionPage(final boolean sourceSelection, final List<RemoteConfig> configuredRemotes,
            String presetUri) {

        super(CustomRepositorySelectionPage.class.getName());

        this.uri = new URIish();
        if (presetUri == null) {
            presetUri = getPresetUriFromClipboard();
        }
        this.presetUri = presetUri;

        selection = INVALID_SELECTION;

        if (sourceSelection) {
            setTitle(UIText.RepositorySelectionPage_sourceSelectionTitle);
            setDescription(UIText.RepositorySelectionPage_sourceSelectionDescription);
        } else {
            setTitle(UIText.RepositorySelectionPage_destinationSelectionTitle);
            setDescription(UIText.RepositorySelectionPage_destinationSelectionDescription);
        }

        storeInSecureStore = getPreferenceStore().getBoolean(UIPreferences.CLONE_WIZARD_STORE_SECURESTORE);
    }

    protected String getPresetUriFromClipboard() {
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        String text = (String) clipboard.getContents(TextTransfer.getInstance());
        try {
            if (text == null) {
                return null;
            }
            text = stripGitCloneCommand(text);
            // Split on any whitespace character
            text = text.split("[ \\f\\n\\r\\x0B\\t\\xA0\\u1680\\u180e\\u2000-\\u200a\\u202f\\u205f\\u3000]", //$NON-NLS-1$
                    2)[0];
            URIish u = new URIish(text);
            if ((canHandleProtocol(u))
                    && (Protocol.GIT.handles(u) || Protocol.SSH.handles(u)
                            || (Protocol.HTTP.handles(u) || Protocol.HTTPS.handles(u))
                            && KnownHosts.isKnownHost(u.getHost()) || text.endsWith(Constants.DOT_GIT_EXT))) {
                return text;
            }
        } catch (URISyntaxException e) {
            // ignore, preset is null
        } finally {
            clipboard.dispose();
        }
        return null;
    }

    /**
     * Create repository selection page, allowing user specifying URI, with no
     * preconfigured remotes selection.
     *
     * @param sourceSelection
     * true if dialog is used for source selection; false otherwise
     * (destination selection). This indicates appropriate text
     * messages.
     * @param presetUri
     * the pre-set URI, may be null
     */
    public CustomRepositorySelectionPage(final boolean sourceSelection, String presetUri) {
        this(sourceSelection, null, presetUri);
    }

    /**
     * No args constructor; needed because the page is provided by the extension
     * point {@code org.eclipse.egit.ui.cloneSourceProvider}
     */
    public CustomRepositorySelectionPage() {
        this(true, null);
    }

    /**
     * @return repository selection representing current page state.
     */
    public RepositorySelection getSelection() {
        return selection;
    }

    /**
     * Compare current repository selection set by user to provided one.
     *
     * @param s
     * repository selection to compare.
     * @return true if provided selection is equal to current page selection,
     * false otherwise.
     */
    public boolean selectionEquals(final RepositorySelection s) {
        return selection.equals(s);
    }

    @Override
    public void createControl(final Composite parent) {
        final Composite panel = new Composite(parent, SWT.NULL);
        panel.setLayout(new GridLayout());

        createUriPanel(panel);

        if (presetUri != null) {
            updateFields(presetUri);
        }

        updateRemoteAndURIPanels();
        Dialog.applyDialogFont(panel);
        setControl(panel);

        checkPage();
    }

    private boolean canHandleProtocol(URIish u) {
        for (TransportProtocol proto : Transport.getTransportProtocols()) {
            if (proto.canHandle(u)) {
                return true;
            }
        }
        return false;
    }

    private void createUriPanel(final Composite parent) {
        uriPanel = new Composite(parent, SWT.NULL);
        uriPanel.setLayout(new GridLayout());
        final GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        uriPanel.setLayoutData(gd);

        createLocationGroup(uriPanel);
        createAuthenticationGroup(uriPanel);
    }

    private void createLocationGroup(final Composite parent) {
        final Group locationGroup = createGroup(parent, UIText.RepositorySelectionPage_groupLocation);

        locationGroup.setLayout(new GridLayout(2, false));
        newLabel(locationGroup, GitStringConstants.LBL_REPOSITORY_URL + ":"); //$NON-NLS-1$
        uriText = new Text(locationGroup, SWT.BORDER);

        if (presetUri != null) {
            uriText.setText(presetUri);
            uriText.selectAll();
        }

        uriText.setLayoutData(createFieldGridData());
        uriText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                updateFields(uriText.getText());
            }
        });
    }

    private Group createAuthenticationGroup(final Composite parent) {
        final Group authenticateGroup = createGroup(parent, UIText.RepositorySelectionPage_groupAuthentication);

        newLabel(authenticateGroup, UIText.RepositorySelectionPage_promptUser + ":"); //$NON-NLS-1$
        userText = new Text(authenticateGroup, SWT.BORDER);
        userText.setLayoutData(createFieldGridData());
        userText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                user = userText.getText();
            }
        });

        newLabel(authenticateGroup, UIText.RepositorySelectionPage_promptPassword + ":"); //$NON-NLS-1$
        passText = new Text(authenticateGroup, SWT.BORDER | SWT.PASSWORD);
        passText.setLayoutData(createFieldGridData());
        passText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(final ModifyEvent e) {
                password = passText.getText();
            }
        });

        storeCheckbox = new Button(authenticateGroup, SWT.CHECK);
        storeCheckbox.setText(GitStringConstants.CHCK_SAVE_AUTHENTICATION);
        storeCheckbox.setSelection(storeInSecureStore);
        storeCheckbox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                storeInSecureStore = storeCheckbox.getSelection();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                storeInSecureStore = storeCheckbox.getSelection();
            }
        });
        GridDataFactory.fillDefaults().span(2, 1).applyTo(storeCheckbox);
        return authenticateGroup;
    }

    private Group createGroup(final Composite parent, final String text) {
        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setText(text);
        group.setLayoutData(createFieldGridData());
        return group;
    }

    private void newLabel(final Group g, final String text) {
        new Label(g, SWT.NULL).setText(text);
    }

    private GridData createFieldGridData() {
        return new GridData(SWT.FILL, SWT.DEFAULT, true, false);
    }

    private void safeSet(final Text text, final String value) {
        text.setText(value != null ? value : EMPTY_STRING);
    }

    /**
     * Check the user input and set messages in case of invalid input.
     */
    protected void checkPage() {
        if (uriText.getText().length() == 0) {
            selectionIncomplete(null);
            return;
        } else if (uriText.getText().endsWith(" ")) { //$NON-NLS-1$
            selectionIncomplete(UIText.RepositorySelectionPage_UriMustNotHaveTrailingSpacesMessage);
            return;
        }
        
        if (uri == null) {
            return;
        }
        try {
            final URIish finalURI = new URIish(stripGitCloneCommand(uriText.getText()));
            String proto = finalURI.getScheme();

            if (uri.getPath() == null) {
                selectionIncomplete(NLS.bind(UIText.RepositorySelectionPage_fieldRequired,
                        unamp(UIText.RepositorySelectionPage_promptPath), proto));
                return;
            }

            if (Protocol.FILE.handles(finalURI)) {
                String badField = null;
                if (uri.getHost() != null) {
                    badField = UIText.RepositorySelectionPage_promptHost;
                }
                else if (uri.getUser() != null) {
                    badField = UIText.RepositorySelectionPage_promptUser;
                }
                else if (uri.getPass() != null) {
                    badField = UIText.RepositorySelectionPage_promptPassword;
                }
                if (badField != null) {
                    selectionIncomplete(NLS.bind(UIText.RepositorySelectionPage_fieldNotSupported, unamp(badField),
                            proto));
                    return;
                }

                final File file = FS.DETECTED.resolve(new File("."), uri.getPath()); //$NON-NLS-1$
                if (!file.exists()) {
                    selectionIncomplete(NLS.bind(UIText.RepositorySelectionPage_fileNotFound, file.getAbsolutePath()));
                    return;
                }

                selectionComplete(finalURI, null);
                return;
            }

            if (uri.getHost() == null) {
                selectionIncomplete(NLS.bind(UIText.RepositorySelectionPage_fieldRequired,
                        unamp(UIText.RepositorySelectionPage_promptHost), proto));
                return;
            }

            if (Protocol.GIT.handles(finalURI)) {
                String badField = null;
                if (uri.getUser() != null) {
                    badField = UIText.RepositorySelectionPage_promptUser;
                }
                else if (uri.getPass() != null) {
                    badField = UIText.RepositorySelectionPage_promptPassword;
                }
                if (badField != null) {
                    selectionIncomplete(NLS.bind(UIText.RepositorySelectionPage_fieldNotSupported, unamp(badField),
                            proto));
                    return;
                }
            }

            if (Protocol.HTTP.handles(finalURI) || Protocol.HTTPS.handles(finalURI)) {
                UserPasswordCredentials credentials = SecureStoreUtils.getCredentials(finalURI);
                if (credentials != null) {
                    String u = credentials.getUser();
                    String p = credentials.getPassword();
                    String uriUser = finalURI.getUser();
                    if (uriUser == null) {
                        if (setSafeUser(u) && setSafePassword(p)) {
                            setStoreInSecureStore(true);
                        }
                    } else if (uriUser.length() != 0 && uriUser.equals(u)) {
                        if (setSafePassword(p)) {
                            setStoreInSecureStore(true);
                        }
                    }
                }
            }

            selectionComplete(finalURI, null);
            return;
        } catch (URISyntaxException e) {
            selectionIncomplete(e.getReason());
            return;
        } catch (Exception e) {
            Activator.logError(NLS.bind(UIText.RepositorySelectionPage_errorValidating, getClass().getName()), e);
            selectionIncomplete(UIText.RepositorySelectionPage_internalError);
        }
    }

    private String stripGitCloneCommand(String input) {
        input = input.trim();
        if (input.startsWith(GIT_CLONE_COMMAND_PREFIX)) {
            return input.substring(GIT_CLONE_COMMAND_PREFIX.length()).trim();
        }
        return input;
    }

    private boolean setSafePassword(String p) {
        if ((password == null || password.length() == 0) && p != null && p.length() != 0) {
            password = p;
            passText.setText(p);
            return true;
        }
        return false;
    }

    private boolean setSafeUser(String u) {
        if ((user == null || user.length() == 0) && u != null && u.length() != 0) {
            user = u;
            userText.setText(u);
            return true;
        }
        return false;
    }

    private void setStoreInSecureStore(boolean store) {
        storeInSecureStore = store;
        storeCheckbox.setSelection(store);
    }

    private String unamp(String s) {
        return s.replace("&", EMPTY_STRING); //$NON-NLS-1$
    }

    private void selectionIncomplete(final String errorMessage) {
        setExposedSelection(null, null);
        setErrorMessage(errorMessage);
        setPageComplete(false);
    }

    private void selectionComplete(final URIish u, final RemoteConfig rc) {
        setExposedSelection(u, rc);
        setErrorMessage(null);
        setPageComplete(true);
    }

    private void setExposedSelection(final URIish u, final RemoteConfig rc) {
        final RepositorySelection newSelection = new RepositorySelection(u, rc);
        if (newSelection.equals(selection)) {
            return;
        }
        selection = newSelection;
    }

    private void updateRemoteAndURIPanels() {
        UIUtils.setEnabledRecursively(uriPanel, true);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            uriText.setFocus();
        }
    }

    /**
     * @return credentials
     */
    public UserPasswordCredentials getCredentials() {
        if ((user == null || user.length() == 0) && (password == null || password.length() == 0)) {
            return null;
        }
        return new UserPasswordCredentials(user, password);
    }

    /**
     * @return true if credentials should be stored
     */
    public boolean getStoreInSecureStore() {
        return this.storeInSecureStore;
    }

    private void updateFields(final String text) {
        try {
            eventDepth++;
            if (eventDepth != 1) {
                return;
            }

            String strippedText = stripGitCloneCommand(text);
            final URIish u = new URIish(strippedText);
            if (!text.equals(strippedText)) {
                uriText.setText(strippedText);
            }
            safeSet(userText, u.getUser());
            safeSet(passText, u.getPass());

            uri = u;
        } catch (URISyntaxException err) {
            // leave uriText as it is, but clean up underlying uri and
            // decomposed fields
            uri = new URIish();
            userText.setText(EMPTY_STRING);
            passText.setText(EMPTY_STRING);
        } finally {
            eventDepth--;
        }
        checkPage();
    }

    private IPreferenceStore getPreferenceStore() {
        return Activator.getDefault().getPreferenceStore();
    }

    public GitRepositoryInfo getGitRepositoryInfo() {
        GitRepositoryInfo info = new GitRepositoryInfo(uri.toString());
        info.setCredentials(user, password);
        info.setShouldSaveCredentialsInSecureStore(storeInSecureStore);
        return info;
    }

}
