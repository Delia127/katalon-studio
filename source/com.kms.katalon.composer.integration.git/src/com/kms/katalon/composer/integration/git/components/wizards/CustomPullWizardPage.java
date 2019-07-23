package com.kms.katalon.composer.integration.git.components.wizards;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.egit.core.op.CreateLocalBranchOperation;
import org.eclipse.egit.ui.Activator;
import org.eclipse.egit.ui.UIUtils;
import org.eclipse.egit.ui.internal.UIIcons;
import org.eclipse.egit.ui.internal.UIText;
import org.eclipse.egit.ui.internal.components.RefContentAssistProvider;
import org.eclipse.egit.ui.internal.push.PushBranchPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.lib.BranchConfig;
import org.eclipse.jgit.lib.BranchConfig.BranchRebaseMode;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.integration.git.constants.GitStringConstants;

/**
 * This Wizard Page allows to configure a Push operation (remote, reference,
 * rebase/merge)
 *
 * It is heavily inspired/copy-pasted from the {@link PushBranchPage} and a lot
 * of code could be factorized.
 */
@SuppressWarnings("restriction")
public class CustomPullWizardPage extends WizardPage {
    private RemoteConfig remoteConfig;

    private RefContentAssistProvider assist;

    private Repository repository;

    private String fullBranch;

    private BranchRebaseMode upstreamConfig;

    private ComboViewer remoteBranchNameComboViewer;

    private Button refreshButton;

    private ControlDecoration missingBranchDecorator;

    private boolean configureUpstream;

    private Set<Resource> disposables = new HashSet<>();

    /**
     * Create the page.
     *
     * @param repository
     */
    public CustomPullWizardPage(Repository repository) {
        super(UIText.PullWizardPage_PageName);
        setTitle(UIText.PullWizardPage_PageTitle);
        setMessage(UIText.PullWizardPage_PageMessage);
        setImageDescriptor(UIIcons.WIZBAN_PULL);
        this.repository = repository;
        try {
            this.fullBranch = repository.getFullBranch();
        } catch (IOException ex) {
            Activator.logError(ex.getMessage(), ex);
        }
    }

    @Override
    public void createControl(Composite parent) {
        setDefaultUpstreamConfig();

        Composite res = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(res);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(res);

        Composite sourceComposite = new Composite(res, SWT.NONE);
        sourceComposite.setLayoutData(GridDataFactory.fillDefaults().indent(UIUtils.getControlIndent(), 0).create());
        RowLayout rowLayout = RowLayoutFactory.fillDefaults().create();
        rowLayout.center = true;
        sourceComposite.setLayout(rowLayout);

        Label sourceLabel = new Label(sourceComposite, SWT.NONE);
        sourceLabel.setText(GitStringConstants.LBL_CURRENT_BRANCH_NAME);

        if (fullBranch != null) {
            Label localBranchLabel = new Label(sourceComposite, SWT.NONE);
            localBranchLabel.setText(Repository.shortenRefName(fullBranch));

            Image branchIcon = UIIcons.BRANCH.createImage();
            disposables.add(branchIcon);

            Label branchIconLabel = new Label(sourceComposite, SWT.NONE);
            branchIconLabel.setLayoutData(new RowData(branchIcon.getBounds().width, branchIcon.getBounds().height));
            branchIconLabel.setImage(branchIcon);
        }

        Composite remoteGroup = new Composite(res, SWT.NONE);
        remoteGroup.setLayoutData(GridDataFactory.fillDefaults().indent(UIUtils.getControlIndent(), 0).create());
        remoteGroup.setLayout(GridLayoutFactory.fillDefaults().numColumns(4).create());

        Label branchNameLabel = new Label(remoteGroup, SWT.NONE);
        branchNameLabel.setText(GitStringConstants.LBL_REMOTE_BRANCH_NAME);

        remoteBranchNameComboViewer = new ComboViewer(remoteGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridDataFactory.fillDefaults()
                .grab(true, false)
                .span(2, 1)
                .hint(300, SWT.DEFAULT)
                .applyTo(remoteBranchNameComboViewer.getControl());

        remoteBranchNameComboViewer.setContentProvider(ArrayContentProvider.getInstance());
        remoteBranchNameComboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Ref) {
                    return Repository.shortenRefName(((Ref) element).getName());
                }
                return super.getText(element);
            }
        });
        remoteBranchNameComboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                checkPage();
            }
        });

        refreshButton = new Button(remoteGroup, SWT.NONE);
        refreshButton.setText(GitStringConstants.LBL_REFRESH_BUTTON);
        refreshButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (assist == null) {
                    return;
                }
                refreshRemoteBranchList(assist.getRemoteURI());
            }
        });

        setPageComplete(isPageComplete());
        setControl(res);
    }

    private void setRefAssist(RemoteConfig config) {
        if (config != null && config.getURIs().size() > 0) {
            refreshRemoteBranchList(config.getURIs().get(0));
        }
    }

    public void refreshRemoteBranchList(final URIish uri) {
        assist = new RefContentAssistProvider(repository, uri, getShell());
        List<Ref> refs = assist.getRefsForContentAssist(false, true);
        Collections.sort(refs, new Comparator<Ref>() {
            @Override
            public int compare(Ref ref1, Ref ref2) {
                return StringUtils.compareWithCase(Repository.shortenRefName(ref1.getName()),
                        Repository.shortenRefName(ref2.getName()));
            }
        });
        remoteBranchNameComboViewer.setInput(refs);
        String suggestedBranchName = getSuggestedBranchName();
        for (Ref ref : refs) {
            if (Repository.shortenRefName(ref.getName()).equals(suggestedBranchName)) {
                remoteBranchNameComboViewer.setSelection(new StructuredSelection(ref));
                break;
            }
        }
    }

    void setSelectedRemote(RemoteConfig config) {
        remoteConfig = config;
        setRefAssist(remoteConfig);
        checkPage();
    }

    @Override
    public boolean isPageComplete() {
        return remoteConfig != null && !remoteBranchNameComboViewer.getSelection().isEmpty();
    }

    private void checkPage() {
        try {
            if (remoteConfig == null) {
                setErrorMessage(UIText.PushBranchPage_ChooseRemoteError);
                return;
            }
            String branchNameMessage = null;
            if (assist.getRefsForContentAssist(false, true).isEmpty()) {
                branchNameMessage = GitStringConstants.HAND_ERROR_MSG_NO_REMOTE_BRANCHES_FOUND;
            } else if (remoteBranchNameComboViewer.getSelection().isEmpty()) {
                branchNameMessage = MessageFormat.format(UIText.PullWizardPage_ChooseReference, remoteConfig.getName());
            }
            if (branchNameMessage != null) {
                setErrorMessage(branchNameMessage);
                if (this.missingBranchDecorator == null) {
                    this.missingBranchDecorator = new ControlDecoration(remoteBranchNameComboViewer.getControl(),
                            SWT.TOP | SWT.LEFT);
                    this.missingBranchDecorator.setImage(FieldDecorationRegistry.getDefault()
                            .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR)
                            .getImage());
                }
                this.missingBranchDecorator.setDescriptionText(branchNameMessage);
                this.missingBranchDecorator.show();
                return;
            } else if (this.missingBranchDecorator != null) {
                this.missingBranchDecorator.hide();
            }

            if (overrideUpstreamConfiguration() && hasDifferentUpstreamConfiguration()) {
                setMessage(UIText.PushBranchPage_UpstreamConfigOverwriteWarning, IMessageProvider.WARNING);
            } else {
                setMessage(UIText.PullWizardPage_PageMessage);
            }
            setErrorMessage(null);
        } finally {
            setPageComplete(getErrorMessage() == null);
        }
    }

    private String getSuggestedBranchName() {
        if (fullBranch == null) {
            return ""; //$NON-NLS-1$
        }
        String branchName = Repository.shortenRefName(fullBranch);
        StoredConfig config = repository.getConfig();
        BranchConfig branchConfig = new BranchConfig(config, branchName);
        String merge = branchConfig.getMerge();
        if (!branchConfig.isRemoteLocal() && merge != null && merge.startsWith(Constants.R_HEADS)) {
            return Repository.shortenRefName(merge);
        }
        if (merge == null && fullBranch.startsWith(Constants.R_HEADS)) {
            return branchName;
        }
        return "";
    }

    boolean overrideUpstreamConfiguration() {
        return this.configureUpstream;
    }

    boolean isRebaseSelected() {
        return upstreamConfig == BranchRebaseMode.REBASE;
    }

    BranchRebaseMode getUpstreamConfig() {
        return this.upstreamConfig;
    }

    private boolean hasDifferentUpstreamConfiguration() {
        String branchName = Repository.shortenRefName(fullBranch);
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

    private void setDefaultUpstreamConfig() {
        String branchName = Repository.shortenRefName(fullBranch);
        BranchConfig branchConfig = new BranchConfig(repository.getConfig(), branchName);
        BranchRebaseMode config = (branchConfig.isRebase() ? BranchRebaseMode.REBASE : BranchRebaseMode.NONE);
        if (branchConfig.getMerge() == null) {
            config = CreateLocalBranchOperation.getDefaultUpstreamConfig(repository, Constants.R_REMOTES + Constants.DEFAULT_REMOTE_NAME + "/"
                    + branchName);
        }
        this.upstreamConfig = config;
    }

    /**
     * @return the chosen short name of the branch on the remote
     */
    String getFullRemoteReference() {
        IStructuredSelection selection = (IStructuredSelection) remoteBranchNameComboViewer.getSelection();
        return ((Ref) selection.getFirstElement()).getName();
    }

    RemoteConfig getRemoteConfig() {
        return this.remoteConfig;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.missingBranchDecorator != null) {
            this.missingBranchDecorator.dispose();
        }
        for (Resource disposable : this.disposables) {
            disposable.dispose();
        }
    }
}
