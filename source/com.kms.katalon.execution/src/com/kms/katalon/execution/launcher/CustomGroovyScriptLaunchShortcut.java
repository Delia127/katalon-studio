package com.kms.katalon.execution.launcher;

import org.codehaus.groovy.eclipse.launchers.GroovyScriptLaunchShortcut;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.util.SyntaxUtil;
import com.kms.katalon.groovy.util.GroovyUtil;

public class CustomGroovyScriptLaunchShortcut extends GroovyScriptLaunchShortcut {

	public CustomGroovyScriptLaunchShortcut() {
		super();
	}

	@Override
	protected String classToRun() {
		return "groovy.ui.GroovyMain";
	}
	
	public static void cleanConfiguration(ILaunch launch) {
		getLaunchManager().removeLaunch(launch);
	}

	public static void cleanAllConfigurations() throws CoreException {
		while (getLaunchManager().getLaunchConfigurations().length > 0) {
			getLaunchManager().getLaunchConfigurations()[0].delete();
		}
	}

	public void launch(TestCaseEntity testCase, IFile scriptFile, LaunchMode launchMode) throws Exception {
		if (testCase != null && testCase.getProject() != null) {
			ICompilationUnit compilationUnit = GroovyUtil.getGroovyScriptForTestCase(testCase);
			IJavaProject javaProject = JavaCore.create(GroovyUtil.getGroovyProject(testCase.getProject()));
            if (compilationUnit != null && javaProject != null) {
                SyntaxUtil.checkScriptSyntax(compilationUnit.getSource());
                launch(scriptFile, testCase.getProject(), launchMode);
            }
		}
	}

	public void launch(IFile scriptFile, ProjectEntity project, LaunchMode launchMode) throws Exception {
		if (scriptFile != null && project != null) {
			ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(scriptFile);
			IJavaProject javaProject = JavaCore.create(GroovyUtil.getGroovyProject(project));
			if (compilationUnit != null && javaProject != null) {
				launchGroovy(compilationUnit, javaProject, launchMode.toString());
			}
		}
	}
	

	@Override
	protected String generateClasspath(IJavaProject javaProject) {
		return super.generateClasspath(javaProject);
	}
}
