package com.kms.katalon.composer.integration.git.components.wizards;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIUtils;
import org.eclipse.egit.ui.internal.UIIcons;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.components.RefContentAssistProvider;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.BranchConfig.BranchRebaseMode;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.integration.git.constants.GitStringConstants;

/**
 * Page that is part of the "Push Branch..." wizard, where the user selects the
 * remote, the branch name and the upstream config.
 */
@SuppressWarnings("restriction")
public class CustomPushBranchPage extends WizardPage {

    private final Repository repository;

    private final Ref ref;

    private RemoteConfig remoteConfig;

    private List<RemoteConfig> remoteConfigs;

    private RefContentAssistProvider assist;

    private BranchRebaseMode upstreamConfig = BranchRebaseMode.NONE;

    private boolean forceUpdateSelected = false;

    private Set<Resource> disposables = new HashSet<>();

    private ComboViewer remoteBranchNameComboViewer;

    /**
     * Create the page.
     *
     * @param repository
     * @param commitToPush
     * @param ref
     * An optional ref to give hints
     */
    public CustomPushBranchPage(Repository repository, ObjectId commitToPush, Ref ref) {
        super(UIText.PushBranchPage_PageName);
        setTitle(UIText.PushBranchPage_PageTitle);
        setMessage(GitStringConstants.MSG_PUSH_BRANCH_DIALOG);

        this.repository = repository;
        this.ref = ref;
    }

    RemoteConfig getRemoteConfig() {
        return remoteConfig;
    }

    /**
     * @return the chosen short name of the branch on the remote
     */
    String getFullRemoteReference() {
        String branchName = remoteBranchNameComboViewer.getCombo().getText();
        if (!branchName.startsWith(Constants.R_REFS)) {
            return Constants.R_HEADS + branchName;
        }
        return branchName;
    }

    boolean isConfigureUpstreamSelected() {
        return upstreamConfig != BranchRebaseMode.NONE;
    }

    boolean isRebaseSelected() {
        return upstreamConfig == BranchRebaseMode.REBASE;
    }

    boolean isForceUpdateSelected() {
        return forceUpdateSelected;
    }

    @Override
    public void createControl(Composite parent) {
        try {
            this.remoteConfigs = RemoteConfig.getAllRemoteConfigs(repository.getConfig());
            Collections.sort(remoteConfigs, new Comparator<RemoteConfig>() {
                @Override
                public int compare(RemoteConfig first, RemoteConfig second) {
                    return String.CASE_INSENSITIVE_ORDER.compare(first.getName(), second.getName());
                }
            });
        } catch (URISyntaxException e) {
            this.remoteConfigs = new ArrayList<>();
            handleError(e);
        }

        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(GridLayoutFactory.swtDefaults().create());

        Composite inputPanel = new Composite(main, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(inputPanel);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(inputPanel);

        Composite sourceComposite = new Composite(inputPanel, SWT.NONE);
        sourceComposite.setLayoutData(GridDataFactory.fillDefaults().indent(UIUtils.getControlIndent(), 0).create());
        RowLayout rowLayout = RowLayoutFactory.fillDefaults().create();
        rowLayout.center = true;
        sourceComposite.setLayout(rowLayout);

        Label sourceLabel = new Label(sourceComposite, SWT.NONE);
        sourceLabel.setText(GitStringConstants.LBL_CURRENT_BRANCH_NAME);

        if (this.ref != null) {
            Label localBranchLabel = new Label(sourceComposite, SWT.NONE);
            localBranchLabel.setText(Repository.shortenRefName(this.ref.getName()));

            Image branchIcon = UIIcons.BRANCH.createImage();
            this.disposables.add(branchIcon);

            Label branchIconLabel = new Label(sourceComposite, SWT.NONE);
            branchIconLabel.setLayoutData(new RowData(branchIcon.getBounds().width, branchIcon.getBounds().height));
            branchIconLabel.setImage(branchIcon);
        }

        Composite remoteGroup = new Composite(inputPanel, SWT.NONE);
        remoteGroup.setLayoutData(GridDataFactory.fillDefaults().indent(UIUtils.getControlIndent(), 0).create());
        remoteGroup.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

        Label branchNameLabel = new Label(remoteGroup, SWT.NONE);
        branchNameLabel.setText(GitStringConstants.LBL_REMOTE_BRANCH_NAME);

        remoteBranchNameComboViewer = new ComboViewer(remoteGroup, SWT.DROP_DOWN);
        GridDataFactory.fillDefaults()
                .grab(true, false)
                .span(2, 1)
                .hint(300, SWT.DEFAULT)
                .applyTo(remoteBranchNameComboViewer.getControl());

        remoteBranchNameComboViewer.setContentProvider(ArrayContentProvider.getInstance());
        remoteBranchNameComboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof String) {
                    return (String) element;
                }
                if (element instanceof Ref) {
                    return Repository.shortenRefName(((Ref) element).getName());
                }
                return super.getText(element);
            }
        });

        remoteBranchNameComboViewer.getCombo().setText(getSuggestedBranchName());

        setControl(main);

        checkPage();

        remoteBranchNameComboViewer.getCombo().addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                checkPage();
            }
        });
    }

    private void checkPage() {
        try {
            if (remoteConfig == null) {
                setErrorMessage(UIText.PushBranchPage_ChooseRemoteError);
                return;
            }
            String branchName = remoteBranchNameComboViewer.getCombo().getText();
            if (branchName.length() == 0) {
                setErrorMessage(MessageFormat.format(GitStringConstants.MSG_ERR_MISSING_REMOTE_BRANCH,
                        remoteConfig.getName()));
                return;
            }
            if (!Repository.isValidRefName(Constants.R_HEADS + branchName)) {
                setErrorMessage(UIText.PushBranchPage_InvalidBranchNameError);
                return;
            }
            if (isConfigureUpstreamSelected() && hasDifferentUpstreamConfiguration()) {
                setMessage(UIText.PushBranchPage_UpstreamConfigOverwriteWarning, IMessageProvider.WARNING);
            } else {
                setMessage(GitStringConstants.MSG_PUSH_BRANCH_DIALOG);
            }
            setErrorMessage(null);
        } finally {
            setPageComplete(getErrorMessage() == null);
        }
    }

    void setSelectedRemote(String remoteName, URIish urIish) {
        try {
            RemoteConfig config = new RemoteConfig(repository.getConfig(), remoteName);
            config.addURI(urIish);
            this.remoteConfig = config;
            setRefAssist(this.remoteConfig);
            checkPage();
        } catch (URISyntaxException e) {
            handleError(e);
        }
    }

    private String getSuggestedBranchName() {
        if (ref != null && !ref.getName().startsWith(Constants.R_REMOTES)) {
            String branchName = Repository.shortenRefName(ref.getName());
            BranchConfig branchConfig = new BranchConfig(repository.getConfig(), branchName);
            String merge = branchConfig.getMerge();
            if (!branchConfig.isRemoteLocal() && merge != null && merge.startsWith(Constants.R_HEADS)) {
                return Repository.shortenRefName(merge);
            }
            return branchName;
        } else {
            return "";
        }
    }

    private void setRefAssist(RemoteConfig config) {
        if (config == null || config.getURIs().isEmpty()) {
            return;
        }
        assist = new RefContentAssistProvider(CustomPushBranchPage.this.repository, config.getURIs().get(0), getShell());
        String currentText = remoteBranchNameComboViewer.getCombo().getText();
        List<Ref> data = assist.getRefsForContentAssist(false, true);
        Collections.sort(data, new Comparator<Ref>() {
            @Override
            public int compare(Ref ref1, Ref ref2) {
                return StringUtils.compareWithCase(Repository.shortenRefName(ref1.getName()),
                        Repository.shortenRefName(ref2.getName()));
            }
        });
        remoteBranchNameComboViewer.setInput(data);
        remoteBranchNameComboViewer.getCombo().setText(currentText);
    }

    private boolean hasDifferentUpstreamConfiguration() {
        String branchName = Repository.shortenRefName(ref.getName());
        BranchConfig branchConfig = new BranchConfig(repository.getConfig(), branchName);

        String remote = branchConfig.getRemote();
        // No upstream config -> don't show warning
        if (remote == null) {
            return false;
        }
        if (!remote.equals(remoteConfig.getName())) {
            return true;
        }

        String merge = branchConfig.getMerge();
        if (merge == null || !merge.equals(getFullRemoteReference())) {
            return true;
        }
        return branchConfig.isRebase() != isRebaseSelected();
    }

    private void handleError(URISyntaxException e) {
        Activator.handleError(e.getMessage(), e, false);
        setErrorMessage(e.getMessage());
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Resource disposable : this.disposables) {
            disposable.dispose();
        }
    }
}
