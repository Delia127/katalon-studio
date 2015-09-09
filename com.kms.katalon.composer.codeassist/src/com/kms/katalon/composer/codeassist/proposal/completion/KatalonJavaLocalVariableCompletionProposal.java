package com.kms.katalon.composer.codeassist.proposal.completion;

import org.codehaus.groovy.eclipse.codeassist.proposals.GroovyJavaFieldCompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.codeassist.util.KatalonContextUtil;
import com.kms.katalon.composer.testcase.constants.ImageConstants;

public class KatalonJavaLocalVariableCompletionProposal extends GroovyJavaFieldCompletionProposal {

    private static final Image KATALON_IMAGE = ImageConstants.IMG_16_VARIABLE;

    public KatalonJavaLocalVariableCompletionProposal(KatalonLocalVariableCompletionProposal proposal) {
        super(proposal, KATALON_IMAGE, new StyledString(proposal.getVariableName()).append(KatalonContextUtil
                .getKatalonSignature()));
    }
}
