package com.kms.katalon.composer.codeassist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.eclipse.codeassist.processors.IProposalProvider;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;

import com.kms.katalon.composer.codeassist.proposal.KatalonLocalVariableProposal;
import com.kms.katalon.composer.codeassist.proposal.KatalonMethodNodeProposal;
import com.kms.katalon.composer.codeassist.util.KatalonContextUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.entity.testcase.TestCaseEntity;

/**
 * Provides proposal for Katalon's keywords as methods and test case's binding
 * variables.
 * 
 * @see KatalonMethodNodeProposal
 * @see KatalonLocalVariableProposal
 */
public class KatalonProposalProvider implements IProposalProvider {

    @Override
    public List<IGroovyProposal> getStatementAndExpressionProposals(ContentAssistContext context,
            ClassNode completionType, boolean isStatic, Set<ClassNode> categories) {
        List<IGroovyProposal> groovyProposals = new ArrayList<IGroovyProposal>();
        String completionExpression = context.completionExpression;

        // Add keyword proposals for BuiltinKeyword class
        if (KatalonContextUtil.isBuiltinKeywordCompletionClassNode(context)) {
            for (MethodNode methodNode : completionType.getAllDeclaredMethods()) {
                if (methodNode.getName().startsWith(completionExpression.trim())
                        && isKeywordNode(methodNode)) {
                    groovyProposals.add(new KatalonMethodNodeProposal(methodNode));
                }
            }
        }

        // Add keyword proposals for CustomKeyword class
        if (KatalonContextUtil.isCustomKeywordCompletionClassNode(context)) {
            try {
                for (MethodNode methodNode : KeywordController.getInstance().getCustomKeywords(
                        ProjectController.getInstance().getCurrentProject())) {
                    groovyProposals.add(new KatalonMethodNodeProposal(methodNode));
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }

        // Add test case's binded variable for test case's script
        if (context.getEnclosingGroovyType().equals(completionType)) {
            TestCaseEntity testCaseEntity = KatalonContextUtil.isTestCaseScriptContext(context);
            if (testCaseEntity != null) {
                for (String variableName : KatalonContextUtil.getTestCaseVariableStrings(testCaseEntity)) {
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

    @Override
    public List<String> getNewFieldProposals(ContentAssistContext context) {
        return null;
    }
    
    private boolean isKeywordNode(MethodNode methodNode) {
        if (!methodNode.isStatic() || !methodNode.isPublic()) { return false; }
        for (AnnotationNode annotatedNode : methodNode.getAnnotations()) {
            if (annotatedNode.getClassNode().getName().equals(Keyword.class.getName())) {
                return true;
            }
        }
        return false;
    }

}