package com.kms.katalon.composer.testcase.editors;

import java.util.ArrayList;

import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.adapter.CComboContentAdapter;
import com.kms.katalon.composer.testcase.model.ContentProposalCheck;

public class ComboBoxCellEditorWithContentProposal extends TooltipComboBoxCellEditor {
    private ContentProposalCheck contentProposalCheck;
    private String[] items;
    private String[] toolTips;
    private ContentProposalAdapter adapter;

    public ComboBoxCellEditorWithContentProposal(Composite parent, String[] items, String[] toolTips,
            ContentProposalCheck contentProposalCheck) {
        super(parent, items, toolTips);
        this.contentProposalCheck = contentProposalCheck;
        this.items = items;
        this.toolTips = toolTips;
    }

    @Override
    protected void focusLost() {
        if (contentProposalCheck.isProposing()) {
            // Do Nothing
        } else {
            super.focusLost();
        }
    }

    @Override
    protected boolean dependsOnExternalFocusListener() {
        return false;
    }

    public void loseFocus() {
        focusLost();
    }

    @Override
    protected Control createControl(Composite parent) {
        Control control = super.createControl(parent);
        if (control instanceof CCombo) {
            final CCombo combo = (CCombo) control;

            IContentProposalProvider proposalProvider = new IContentProposalProvider() {
                @Override
                public IContentProposal[] getProposals(String contents, int position) {
                    ArrayList<ContentProposal> list = new ArrayList<ContentProposal>();
                    for (int i = 0; i < items.length; i++) {
                        if (items[i].length() >= contents.length()
                                && items[i].substring(0, contents.length()).equalsIgnoreCase(contents)) {
                            list.add(new ContentProposal(items[i], toolTips[i]));
                        }
                    }
                    return list.toArray(new IContentProposal[list.size()]);
                }
            };

            adapter = new ContentProposalAdapter(combo, new CComboContentAdapter(),
                    proposalProvider, null, null);
            adapter.setPropagateKeys(true);
            adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
            adapter.addContentProposalListener(new IContentProposalListener2() {

                @Override
                public void proposalPopupOpened(ContentProposalAdapter adapter) {
                    contentProposalCheck.setProposing(true);
                }

                @Override
                public void proposalPopupClosed(ContentProposalAdapter adapter) {
                    contentProposalCheck.setProposing(false);
                }
            });

            adapter.addContentProposalListener(new IContentProposalListener() {

                @Override
                public void proposalAccepted(IContentProposal proposal) {
                    loseFocus();
                }
            });

            combo.addKeyListener(new KeyListener() {
                @Override
                public void keyReleased(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    adapter.setEnabled(!combo.getListVisible());
                }
            });
        }
        return control;
    }

}
