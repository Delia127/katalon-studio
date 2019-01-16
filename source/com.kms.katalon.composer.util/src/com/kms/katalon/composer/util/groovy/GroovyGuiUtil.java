package com.kms.katalon.composer.util.groovy;

import static com.kms.katalon.groovy.util.GroovyUtil.getDefaultPackageForTestCase;
import static com.kms.katalon.groovy.util.GroovyUtil.getGroovyClassName;
import static com.kms.katalon.groovy.util.GroovyUtil.getGroovyProject;
import static com.kms.katalon.groovy.util.GroovyUtil.getScriptNameForTestCase;
import static com.kms.katalon.groovy.util.GroovyUtil.getScriptPackageRelativePathForTestCase;
import static com.kms.katalon.groovy.util.GroovyUtil.getTestCaseScriptFolder;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.codehaus.jdt.groovy.model.GroovyCompilationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;

import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class GroovyGuiUtil {

    public static ICompilationUnit createGroovyScriptForCustomKeyword(IPackageFragment parentPackage, String typeName)
            throws CoreException {
        return GroovyCompilationHelper.createGroovyType(parentPackage, typeName, false, ImportType.KEYWORD_IMPORTS);
    }

    public static ICompilationUnit createGroovyScriptForCustomKeywordFromTemplate(IPackageFragment parentPackage, String typeName, String template) throws CoreException {
        return GroovyCompilationHelper.createGroovyTypeFromString(parentPackage, typeName, template);
    }
    
    public static ICompilationUnit getOrCreateGroovyScriptForTestCase(TestCaseEntity testCase) throws CoreException,
            IOException {
        getTestCaseScriptFolder(testCase);
        IProject groovyProject = getGroovyProject(testCase.getProject());

        String parentRelativeFolder = getScriptPackageRelativePathForTestCase(testCase);
        IFolder parentFolder = groovyProject.getFolder(parentRelativeFolder);
        if (!parentFolder.exists()) {
            parentFolder.getParent().refreshLocal(IResource.DEPTH_ONE, null);
        }
        parentFolder.refreshLocal(IResource.DEPTH_ONE, null);
        String scriptFileName = getScriptNameForTestCase(testCase);
        IFile scriptFile = null;

        if (scriptFileName == null) {
            scriptFileName = getGroovyClassName(testCase);
            scriptFile = parentFolder.getFile(scriptFileName + GroovyConstants.GROOVY_FILE_EXTENSION);
            scriptFile.getLocation().toFile().createNewFile();

            GroovyCompilationUnit newCompilationunit = (GroovyCompilationUnit) GroovyCompilationHelper.createGroovyType(
                    getDefaultPackageForTestCase(testCase.getProject()), scriptFileName);
            StringBuilder importBuilder = new StringBuilder();
            GroovyParser parser = new GroovyParser(importBuilder);
            parser.parseGroovyAstIntoScript(Arrays.asList(newCompilationunit.getModuleNode().getClasses().get(0)));
            FileUtils.writeStringToFile(scriptFile.getLocation().toFile(), importBuilder.toString());
            scriptFile.refreshLocal(IResource.DEPTH_ZERO, null);
            newCompilationunit.getResource().delete(true, null);
        } else {
            scriptFile = parentFolder.getFile(scriptFileName + GroovyConstants.GROOVY_FILE_EXTENSION);
        }

        return JavaCore.createCompilationUnitFrom(scriptFile);
    }
    
    public static void addContentToTestCase(TestCaseEntity testCase, String content) throws Exception {
        ICompilationUnit unit = getOrCreateGroovyScriptForTestCase(testCase);
        if (unit == null) {
            return;
        }
        FileUtils.writeStringToFile(unit.getResource().getLocation().toFile(), content, java.nio.charset.Charset.forName("UTF-8"));
    }
    
    public static void addContentToTestCase(TestCaseEntity testCase, InputStream content) throws Exception {
        ICompilationUnit unit = getOrCreateGroovyScriptForTestCase(testCase);
        if (unit == null) {
            return;
        }
        FileUtils.copyInputStreamToFile(content, unit.getResource().getLocation().toFile());
    }

    public static URLClassLoader getProjectClasLoader(ProjectEntity projectEntity) throws MalformedURLException,
            CoreException {
        IJavaProject project = JavaCore.create(GroovyUtil.getGroovyProject(projectEntity));
        return GroovyUtil.getProjectClasLoader(project, JavaRuntime.computeDefaultRuntimeClassPath(project));
    }
}
