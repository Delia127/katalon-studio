package com.kms.katalon.composer.codeassist.proposal.completion;

import groovyjarjarasm.asm.Opcodes;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.eclipse.codeassist.proposals.GroovyFieldProposal;

public class KatalonJavaLocalVariableCompletionProposal extends GroovyFieldProposal {

	public static KatalonJavaLocalVariableCompletionProposal createProposal(String variableName, ClassNode declaring) {
		FieldNode fieldNode = new FieldNode(variableName, Opcodes.ACC_PUBLIC, new ClassNode(Object.class), declaring, null);
		return new KatalonJavaLocalVariableCompletionProposal(fieldNode);
	}
	
	public KatalonJavaLocalVariableCompletionProposal(FieldNode fieldNode) {
		super(fieldNode);
		
	}
}
