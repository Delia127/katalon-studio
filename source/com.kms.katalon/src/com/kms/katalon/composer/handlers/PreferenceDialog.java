package com.kms.katalon.composer.handlers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;

import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.constants.DocumentationMessageConstants;

@SuppressWarnings("restriction")
public class PreferenceDialog extends WorkbenchPreferenceDialog {

    private static final String GENERAL_PAGE_NAME = "General";
    
    private Composite helpComposite;

    public PreferenceDialog(Shell parentShell, PreferenceManager manager) {
        super(parentShell, manager);
    }

    @Override
    public String getSelectedNodePreference() {
        String selectedNode = super.getSelectedNodePreference();
        return StringUtils.isNotEmpty(selectedNode) ? selectedNode : PreferenceHandler.DEFAULT_PREFERENCE_PAGE_ID;
    }

    @Override
    protected void setContentAndLabelProviders(TreeViewer treeViewer) {
        super.setContentAndLabelProviders(treeViewer);
        treeViewer.setLabelProvider(new TypeCheckedStyleCellLabelProvider<IPreferenceNode>(0) {

            @Override
            protected Class<IPreferenceNode> getElementType() {
                return IPreferenceNode.class;
            }

            @Override
            protected Image getImage(IPreferenceNode element) {
                return null;
            }

            @Override
            protected String getText(IPreferenceNode element) {
                return element.getLabelText();
            }
        });
    }
    
    @Override
    protected boolean showPage(IPreferenceNode node) {
        boolean success = super.showPage(node);
        if (success) {
            IPreferencePage shownPage = getCurrentPage();
            showHelpButtonForPage(shownPage);
        }
        return success;
    }
    
    private void showHelpButtonForPage(IPreferencePage page) {
        Control[] helpCompositeChildren = helpComposite.getChildren();
        if (helpCompositeChildren.length > 0) {
            Composite oldHelpButton = (Composite) helpCompositeChildren[0];
            oldHelpButton.dispose(); //dispose old help button
        }
        
        if (page.getTitle().equals(GENERAL_PAGE_NAME)) {
            String documentationUrl = DocumentationMessageConstants.PREFERENCE_GENERAL;
            createAndShowHelpButton(helpComposite, documentationUrl);
        } else if (page instanceof PreferencePageWithHelp) {
            PreferencePageWithHelp prefPageWithHelp = (PreferencePageWithHelp) page;
            if (prefPageWithHelp.hasDocumentation()) {
                String documentationUrl = prefPageWithHelp.getDocumentationUrl();
                createAndShowHelpButton(helpComposite, documentationUrl);
            } else {
               helpComposite.setVisible(false);
            }
        } else {
            helpComposite.setVisible(false);
        }
        
    }
    
    private void createAndShowHelpButton(Composite helpComposite, String documentationUrl) {
        new HelpCompositeForDialog(helpComposite, documentationUrl) {

            @Override
            protected GridData createGridData() {
                return new GridData(SWT.RIGHT, SWT.CENTER, true, false);
            }

            @Override
            protected GridLayout createLayout() {
                GridLayout layout = new GridLayout();
                layout.marginHeight = 0;
                layout.marginBottom = 0;
                layout.marginWidth = 0;
                return layout;
            }
        };

        helpComposite.setVisible(true);
        helpComposite.getParent().layout(true, true);
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns++;
        helpComposite = new Composite(parent, SWT.NONE);
        GridLayout glHelp = new GridLayout();
        glHelp.marginWidth = 0;
        glHelp.marginHeight = 0;
        helpComposite.setLayout(glHelp);
        helpComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        super.createButtonsForButtonBar(parent);
    }

}