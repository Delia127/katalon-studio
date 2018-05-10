package com.kms.katalon.composer.execution.debug;

import java.io.File;

import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.debug.core.JavaDebugUtils;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.logging.LogExceptionFilter;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class SourceLocator extends JavaSourceLookupDirector {

    @Override
    public Object getSourceElement(Object element) {
        if (element instanceof JDIStackFrame) {
            JDIStackFrame stackFrame = (JDIStackFrame) element;
            try {
                String className = stackFrame.getDeclaringTypeName();

                if (LogExceptionFilter.isTestCaseScript(className)) {
                    TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByScriptName(className);
                    String testCaseRelative = GroovyUtil.getScriptPackageRelativePathForTestCase(testCase);
                    String testCaseScriptName = GroovyUtil.getScriptNameForTestCase(testCase)
                            + GroovyConstants.GROOVY_FILE_EXTENSION;
                    return GroovyUtil.getGroovyProject(testCase.getProject()).getFile(
                            testCaseRelative + File.separator + testCaseScriptName);
                }
                
                if (LogExceptionFilter.isCustomKeywordScript(className)) {
                    for (IFile scriptFile : GroovyUtil.getAllScriptFiles(GroovyUtil.getCustomKeywordSourceFolder(
                            ProjectController.getInstance().getCurrentProject()))) {

                        GroovyCompilationUnit unit = (GroovyCompilationUnit) JavaCore.createCompilationUnitFrom(scriptFile);
                        if (className.equals(unit.getModuleNode().getMainClassName())) {
                            return scriptFile;
                        }
                    }
                }
                
                if (stackFrame.getThis() != null) {
                    IJavaProject javaProject = JavaCore.create(GroovyUtil.getGroovyProject(ProjectController.getInstance().getCurrentProject()));
                    return JavaDebugUtils.findElement(stackFrame.getDeclaringTypeName(), javaProject);
                }
            } catch (Exception e) {
                // cannot find test case, let the super do this.
            }
        }
        return super.getSourceElement(element);
    }

}
