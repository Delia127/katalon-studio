package com.kms.katalon.composer.codeassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.eclipse.codeassist.processors.IProposalProvider;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.eclipse.core.resources.IFile;

import com.kms.katalon.composer.codeassist.proposal.KatalonLocalVariableProposal;
import com.kms.katalon.composer.codeassist.proposal.KatalonMethodNodeProposal;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class KatalonProposalProvider implements IProposalProvider {

	@Override
	public List<IGroovyProposal> getStatementAndExpressionProposals(ContentAssistContext context,
			ClassNode completionType, boolean isStatic, Set<ClassNode> categories) {
		List<IGroovyProposal> groovyProposals = new ArrayList<IGroovyProposal>();
		String completionExpression = context.completionExpression;
		
		for (MethodNode methodNode : completionType.getAllDeclaredMethods()) {
			if (methodNode.getName().startsWith(completionExpression.trim())) {
				groovyProposals.add(new KatalonMethodNodeProposal(methodNode));
			}
		}

		if (context.getEnclosingGroovyType().equals(completionType)) {
			TestCaseEntity testCaseEntity = isTestCaseScriptContext(context);
			if (testCaseEntity != null) {
				for (String variableName : getTestCaseVariableStrings(testCaseEntity)) {
					KatalonLocalVariableProposal testCaseVariableProposal = new KatalonLocalVariableProposal(
							variableName);
					groovyProposals.add(testCaseVariableProposal);
				}
			}
		}

		return groovyProposals;
	}

	@Override
	public List<MethodNode> getNewMethodProposals(ContentAssistContext context) {
		return null;

	}

	/**
	 * Generates test case's variables as field proposals. For test case's
	 * script only.
	 */
	@Override
	public List<String> getNewFieldProposals(ContentAssistContext context) {
		return null;
	}

	@SuppressWarnings("restriction")
	private TestCaseEntity isTestCaseScriptContext(ContentAssistContext context) {
		if (context.unit.getResource() instanceof IFile) {
			IFile scriptContextFile = (IFile) context.unit.getResource();
			String scriptFilePath = scriptContextFile.getRawLocation().toString();
			if (!GroovyUtil.isScriptFile(scriptFilePath, ProjectController.getInstance().getCurrentProject()))
				return null;
			try {
				return TestCaseController.getInstance().getTestCaseByScriptFilePath(scriptFilePath);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	private List<String> getTestCaseVariableStrings(TestCaseEntity testCaseEntity) {
		if (testCaseEntity == null) return Collections.emptyList();
		List<String> testCaseVariableStrings = new ArrayList<String>();
		for (VariableEntity variableEntity : testCaseEntity.getVariables()) {
			testCaseVariableStrings.add(variableEntity.getName());
		}
		return testCaseVariableStrings;
	}

}
