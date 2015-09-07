package com.kms.katalon.composer.codeassist.proposal;

import java.lang.reflect.Method;
import java.util.List;

import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.eclipse.codeassist.ProposalUtils;
import org.codehaus.groovy.eclipse.codeassist.completions.NamedArgsMethodNode;
import org.codehaus.groovy.eclipse.codeassist.processors.GroovyCompletionProposal;
import org.codehaus.groovy.eclipse.codeassist.proposals.GroovyMethodProposal;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistContext;
import org.codehaus.groovy.eclipse.codeassist.requestor.ContentAssistLocation;
import org.codehaus.groovy.eclipse.codeassist.requestor.MethodInfoContentAssistContext;
import org.eclipse.jdt.core.CompletionFlags;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;

import com.kms.katalon.composer.codeassist.proposal.completion.KatalonMethodCompletionProposal;
import com.kms.katalon.controller.KeywordController;

@SuppressWarnings("restriction")
public class KatalonMethodNodeProposal extends GroovyMethodProposal {

	public KatalonMethodNodeProposal(MethodNode method) {
		super(method);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IJavaCompletionProposal createJavaProposal(ContentAssistContext context,
			JavaContentAssistInvocationContext javaContext) {

		GroovyCompletionProposal proposal = new GroovyCompletionProposal(CompletionProposal.METHOD_REF,
				context.completionLocation);

		if (context.location == ContentAssistLocation.METHOD_CONTEXT) {
			// only show context information and only for methods
			// that exactly match the name. This happens when we are at the
			// start
			// of an argument or an open paren
			MethodInfoContentAssistContext methodContext = (MethodInfoContentAssistContext) context;
			if (!methodContext.methodName.equals(method.getName())) {
				return null;
			}
			proposal.setReplaceRange(context.completionLocation, context.completionLocation);
			proposal.setCompletion(CharOperation.NO_CHAR);
		} else {
			// otherwise this is a normal method proposal
			proposal.setCompletion(completionName(!isParens(context, javaContext)));
			proposal.setReplaceRange(context.completionLocation - context.completionExpression.length(),
					context.completionEnd);
		}
		proposal.setDeclarationSignature(ProposalUtils.createTypeSignature(method.getDeclaringClass()));
		proposal.setName(method.getName().toCharArray());
		if (method instanceof NamedArgsMethodNode) {
			fillInExtraParameters((NamedArgsMethodNode) method, proposal);
		} else {
			proposal.setParameterNames(createAllParameterNames(context.unit));
			proposal.setParameterTypeNames(getParameterTypeNames(method.getParameters()));
		}
		proposal.setFlags(getModifiers());
		proposal.setAdditionalFlags(CompletionFlags.Default);
		char[] methodSignature = createMethodSignature();
		proposal.setKey(methodSignature);
		proposal.setSignature(methodSignature);
		proposal.setRelevance(computeRelevance());

		return KatalonMethodCompletionProposal.createProposal(proposal, javaContext, context, true, method);

	}

	private void fillInExtraParameters(NamedArgsMethodNode namedArgsMethod, GroovyCompletionProposal proposal) {
		proposal.setParameterNames(getSpecialParameterNames(namedArgsMethod.getParameters()));
		proposal.setRegularParameterNames(getSpecialParameterNames(namedArgsMethod.getRegularParams()));
		proposal.setNamedParameterNames(getSpecialParameterNames(namedArgsMethod.getNamedParams()));
		proposal.setOptionalParameterNames(getSpecialParameterNames(namedArgsMethod.getOptionalParams()));

		proposal.setParameterTypeNames(getParameterTypeNames(namedArgsMethod.getParameters()));
		proposal.setRegularParameterTypeNames(getParameterTypeNames(namedArgsMethod.getRegularParams()));
		proposal.setNamedParameterTypeNames(getParameterTypeNames(namedArgsMethod.getNamedParams()));
		proposal.setOptionalParameterTypeNames(getParameterTypeNames(namedArgsMethod.getOptionalParams()));
	}

	private char[][] getSpecialParameterNames(Parameter[] params) {
		// as opposed to getAllParameterNames, we can assume that the names are
		// correct as is
		// because these parameters were explicitly set by a script
		char[][] paramNames = new char[params.length][];
		for (int i = 0; i < params.length; i++) {
			paramNames[i] = params[i].getName().toCharArray();
		}
		return paramNames;
	}

	private boolean isParens(ContentAssistContext context, JavaContentAssistInvocationContext javaContext) {
		if (javaContext.getDocument().getLength() > context.completionEnd) {
			try {
				return javaContext.getDocument().getChar(context.completionEnd) == '(';
			} catch (BadLocationException e) {

			}
		}
		return false;
	}

	protected char[] completionName(ContentAssistContext context, JavaContentAssistInvocationContext javaContext,
			boolean includeParens) {
		char[] parentCompletionName = super.completionName(false);

		int methodNameEnd = ((MethodInfoContentAssistContext) context).methodNameEnd;
		try {
			String insideParens = javaContext.getDocument().get(methodNameEnd, context.completionEnd - methodNameEnd);

			return new StringBuilder(String.valueOf(parentCompletionName)).append(insideParens).append(")").toString()
					.toCharArray();
		} catch (BadLocationException e) {
			return parentCompletionName;
		}
	}

	@Override
	protected char[][] createAllParameterNames(ICompilationUnit unit) {
		try {
			Method methodNode = 
					KeywordController.getInstance().getBuiltInKeywordByName(method.getDeclaringClass().getName(), 
							method.getName());

			Parameter[] params = method.getParameters();
			int numParams = params == null ? 0 : params.length;

			// short circuit
			if (numParams == 0) {
				return new char[0][];
			}

			char[][] paramNames = new char[numParams][];
			List<String> paramNameStrings = KeywordController.getInstance().getParameterName(methodNode);
			for (int index =0; index < paramNameStrings.size(); index++) {
				paramNames[index] = paramNameStrings.get(index).toCharArray();
			}

			return paramNames;
		} catch (Exception e) {
			return super.createAllParameterNames(unit);
		}
	}

}
