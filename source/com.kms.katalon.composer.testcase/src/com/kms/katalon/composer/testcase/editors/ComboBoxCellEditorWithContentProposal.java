package com.kms.katalon.composer.testcase.editors;

import java.util.ArrayList;

import org.codehaus.groovy.ast.MethodNode;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.adapter.CComboContentAdapter;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.components.IContentProposalListener3;
import com.kms.katalon.composer.testcase.components.KeywordContentProposalAdapter;
import com.kms.katalon.composer.testcase.model.ContentProposalCheck;
import com.kms.katalon.custom.keyword.KeywordMethod;

public class ComboBoxCellEditorWithContentProposal extends TooltipComboBoxCellEditor {
    private ContentProposalCheck contentProposalCheck;

    protected Object[] items;

    protected String[] toolTips;

    private KeywordContentProposalAdapter adapter;

    public ComboBoxCellEditorWithContentProposal(Composite parent, Object[] items, String[] toolTips) {
        super(parent, items, toolTips);
        this.contentProposalCheck = new ContentProposalCheck();
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
                        String itemText = getItemText(items[i]);
                        if (itemText.length() >= contents.length()
                                && itemText.substring(0, contents.length()).equalsIgnoreCase(contents)) {
                            list.add(new ContentProposal(itemText, toolTips[i]));
                        }
                    }
                    return list.toArray(new IContentProposal[list.size()]);
                }
            };

            adapter = new KeywordContentProposalAdapter(combo, new CComboContentAdapter(), proposalProvider, null, null);
            adapter.setPropagateKeys(true);
            adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
            adapter.addContentProposalListener(new IContentProposalListener3() {

                @Override
                public void proposalPopupOpened(KeywordContentProposalAdapter adapter) {
                    contentProposalCheck.setProposing(true);
                }

                @Override
                public void proposalPopupClosed(KeywordContentProposalAdapter adapter) {
                    contentProposalCheck.setProposing(false);
                }
            });

            adapter.addContentProposalListener(new IContentProposalListener() {

                @Override
                public void proposalAccepted(IContentProposal proposal) {
                    loseFocus();
                }
            });

            combo.addKeyListener(new KeyAdapter() {
                
                @Override
                public void keyPressed(KeyEvent e) {
                    adapter.setEnabled(!combo.getListVisible());
                }
            });
        }
        return control;
    }

    private String getItemText(Object item) {
        if (item instanceof KeywordMethod) {
            return TreeEntityUtil.getReadableKeywordName(((KeywordMethod) item).getName());
        }
        if (item instanceof MethodNode) {
            return TreeEntityUtil.getReadableKeywordName(((MethodNode) item).getName());
        }
        return item.toString();
    }
}
