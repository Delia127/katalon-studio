package com.kms.katalon.composer.codeassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.eclipse.codeassist.processors.IProposalProvider;
import org.codehaus.groovy.eclipse.codeassist.proposals.IGroovyProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;

import com.kms.katalon.composer.codeassist.proposal.CheckpointIDProposalProvider;
import com.kms.katalon.composer.codeassist.proposal.KatalonBuitInKeywordAliasProposal;
import com.kms.katalon.composer.codeassist.proposal.KatalonLocalVariableProposal;
import com.kms.katalon.composer.codeassist.proposal.KatalonMethodNodeProposal;
import com.kms.katalon.composer.codeassist.proposal.ProposalProvider;
import com.kms.katalon.composer.codeassist.proposal.TestCaseIDProposalProvider;
import com.kms.katalon.composer.codeassist.proposal.TestDataIDProposalProvider;
import com.kms.katalon.composer.codeassist.proposal.TestObjectIDProposalProvider;
import com.kms.katalon.composer.codeassist.util.KatalonContextUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.entity.testcase.TestCaseEntity;

/**
 * Provides proposal for Katalon's keywords as methods and test case's binding variables.
 * 
 * @see KatalonMethodNodeProposal
 * @see KatalonLocalVariableProposal
 */
public class KatalonProposalProvider implements IProposalProvider {

    @Override
    public List<IGroovyProposal> getStatementAndExpressionProposals(ContentAssistContext context,
            ClassNode completionType, boolean isStatic, Set<ClassNode> categories) {
        List<IGroovyProposal> groovyProposals = new ArrayList<>();
        String completionExpression = StringUtils.trimToEmpty(context.completionExpression);

        // Add keyword proposals for BuiltinKeyword class
        if (KatalonContextUtil.isBuiltinKeywordCompletionClassNode(context)) {
            ClassNode classNode = new ClassNode(
                    KatalonContextUtil.getBuiltInKeywordCompletionClassNode(context).getType());
            for (MethodNode methodNode : classNode.getAllDeclaredMethods()) {
                if (StringUtils.startsWithIgnoreCase(methodNode.getName(), completionExpression.trim())
                        && isKeywordNode(methodNode)) {
                    groovyProposals.add(new KatalonMethodNodeProposal(methodNode));
                }
            }
        }

        // Add keyword proposals for CustomKeyword class
        if (KatalonContextUtil.isCustomKeywordCompletionClassNode(context)) {
            try {
                for (MethodNode methodNode : KeywordController.getInstance()
                        .getCustomKeywords(ProjectController.getInstance().getCurrentProject())) {
                    groovyProposals.add(new KatalonMethodNodeProposal(methodNode));
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }

        // Add test case's binded variable for test case's script
        ClassNode enclosingGroovyType = context.getEnclosingGroovyType();
        if (completionType.equals(enclosingGroovyType)) {
            TestCaseEntity testCaseEntity = KatalonContextUtil.isTestCaseScriptContext(context);
            if (testCaseEntity != null) {
                for (String variableName : KatalonContextUtil.getTestCaseVariableStrings(testCaseEntity)) {
                    KatalonLocalVariableProposal testCaseVariableProposal = new KatalonLocalVariableProposal(
                            variableName);
                    groovyProposals.add(testCaseVariableProposal);
                }
            }
        }

        for (KeywordClass keywordClass : KeywordController.getInstance().getBuiltInKeywordClasses()) {
            if (StringUtils.startsWithIgnoreCase(keywordClass.getAliasName(),
                    StringUtils.trimToEmpty(context.fullCompletionExpression))) {
                groovyProposals.add(new KatalonBuitInKeywordAliasProposal(keywordClass));
            }
        }

        // Support completion type of imported static method
        if (isStatic && enclosingGroovyType != null && !completionType.equals(enclosingGroovyType)
                && !completionType.isRedirectNode()) {
            for (MethodNode methodNode : completionType.getMethods()) {
                if (!methodNode.isPublic()) {
                    continue;
                }
                groovyProposals.add(new KatalonMethodNodeProposal(methodNode));
            }
        }

        groovyProposals.addAll(collectArtifactIDProposals(context, completionType));
        return groovyProposals;
    }

    private List<IGroovyProposal> collectArtifactIDProposals(ContentAssistContext context,
            ClassNode completionType) {
        ProposalProvider[] proposalProviders = new ProposalProvider[] {
                new TestCaseIDProposalProvider(),
                new TestObjectIDProposalProvider(),
                new TestDataIDProposalProvider(),
                new CheckpointIDProposalProvider()
        };
        List<IGroovyProposal> artifactIDProposals = new ArrayList<>();
        for (ProposalProvider provider : proposalProviders) {
            artifactIDProposals.addAll(provider.getProposals(context, completionType));
        }
        return artifactIDProposals;
    }

    @Override
    public List<MethodNode> getNewMethodProposals(ContentAssistContext context) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getNewFieldProposals(ContentAssistContext context) {
        return Collections.emptyList();
    }

    private boolean isKeywordNode(MethodNode methodNode) {
        if (!methodNode.isStatic() || !methodNode.isPublic()) {
            return false;
        }
        for (AnnotationNode annotatedNode : methodNode.getAnnotations()) {
            if (annotatedNode.getClassNode().getName().equals(Keyword.class.getName())) {
                return true;
            }
        }
        return false;
    }

}
