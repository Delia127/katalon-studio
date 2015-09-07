package com.kms.katalon.composer.codeassist.proposal.completion;

import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("restriction")
public class KatalonFailureHandlingCompletionProposal extends PositionBasedCompletionProposal {


	public KatalonFailureHandlingCompletionProposal(String replacementString, Position replacementPosition,
			int cursorPosition, Image image, String displayString, IContextInformation contextInformation,
			String additionalProposalInfo, char[] triggers) {
		super(replacementString, replacementPosition, cursorPosition, image, displayString, contextInformation,
				additionalProposalInfo, triggers);
		// TODO Auto-generated constructor stub
	}
}
