package com.kms.katalon.composer.execution.debug;

import java.io.File;

import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;

import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.logging.LogExceptionFilter;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class SourceLocator extends JavaSourceLookupDirector {	
	@Override
	public Object getSourceElement(Object element) {
		Object parent = super.getSourceElement(element);
		
		if (parent == null && element instanceof JDIStackFrame) {
			JDIStackFrame stackFrame = (JDIStackFrame) element;
			String className;
			try {
				className = stackFrame.getDeclaringTypeName();

				if (LogExceptionFilter.isTestCaseScript(className)) {
					TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByScriptName(className);
					String testCaseRelative = GroovyUtil.getScriptPackageRelativePathForTestCase(testCase);
					String testCaseScriptName = GroovyUtil.getScriptNameForTestCase(testCase)
							+ GroovyConstants.GROOVY_FILE_EXTENSION;
					return GroovyUtil.getGroovyProject(testCase.getProject()).getFile(
							testCaseRelative + File.separator + testCaseScriptName);
				}
			} catch (Exception e) {
				//cannot find test case, let the super do this.
			}
		}
		return parent;
	}

}
