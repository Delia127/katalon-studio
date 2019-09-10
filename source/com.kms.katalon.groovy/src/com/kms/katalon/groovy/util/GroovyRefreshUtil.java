package com.kms.katalon.groovy.util;

import static com.kms.katalon.constants.GlobalStringConstants.ENTITY_ID_SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;

public class GroovyRefreshUtil {

    /**
     * Refresh all children files of the folder by the given relative id.
     * 
     * @param folderRelativePath : Relative path of folder (Warning: eclipse relative path not OS relative path).
     * @param projectEntity : the entire project.
     * @throws CoreException : an exception that invoked when system cannot refresh that folder.
     */
    public static void refreshFolder(String folderRelativePath, ProjectEntity projectEntity, IProgressMonitor monitor)
            throws CoreException {
        IProject project = GroovyUtil.getGroovyProject(projectEntity);
        IFolder resource = project.getFolder(folderRelativePath.replace(File.separator, ENTITY_ID_SEPARATOR));
        resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
    }

    /**
     * Refresh or sync the file with system by the given relative eclipse path.
     * 
     * @param fileRelativePath : Relative path of folder (Warning: eclipse relative path not OS relative path).
     * @param projectEntity : the entire project.
     * @throws CoreException : an exception that invoked when system cannot refresh that folder.
     */
    public static void refreshFile(String fileRelativePath, ProjectEntity projectEntity) throws CoreException {
        IProject project = GroovyUtil.getGroovyProject(projectEntity);
        IFile resource = project.getFile(fileRelativePath.replace(File.separator, ENTITY_ID_SEPARATOR));
        resource.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
    }

    public static void updateScriptReferencesInTestCaseAndCustomScripts(String oldScript, String newScript,
            ProjectEntity projectEntity) throws CoreException, IOException {

        updateScriptReferencesInTestCaseScripts(oldScript, newScript, projectEntity);
        updateScriptReferencesInCustomKeywordScripts(oldScript, newScript, projectEntity);
    }

    private static void updateScriptFile(String oldScript, String newScript, IFile scriptFile) throws CoreException,
            IOException {
        InputStream scriptFileStreamContent = scriptFile.getContents();
        InputStream newScriptFileInputStream = null;
        try {
            String testCaseContent = IOUtils.toString(scriptFileStreamContent, GroovyConstants.DF_CHARSET);

            scriptFileStreamContent.close();
            scriptFileStreamContent = null;

            if (testCaseContent.contains(oldScript)) {
                String newTestCaseContent = testCaseContent.replace(oldScript, newScript);
                newScriptFileInputStream = IOUtils.toInputStream(newTestCaseContent, GroovyConstants.DF_CHARSET);
                scriptFile.setContents(newScriptFileInputStream, true, false, new NullProgressMonitor());

                scriptFile.getParent().refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
            }
        } finally {
            if (scriptFileStreamContent != null) {
                scriptFileStreamContent.close();
            }
            if (newScriptFileInputStream != null) {
                newScriptFileInputStream.close();
            }
        }

    }

    private static void updateScriptReferencesInTestCaseScripts(String oldScript, String newScript,
            ProjectEntity projectEntity) throws CoreException, IOException {
        List<IFile> testCaseFiles = GroovyUtil.getAllTestCaseScripts(projectEntity);

        for (IFile scriptFile : testCaseFiles) {
            updateScriptFile(oldScript, newScript, scriptFile);
        }
    }

    private static void updateScriptReferencesInCustomKeywordScripts(String oldScript, String newScript,
            ProjectEntity projectEntity) throws CoreException, IOException {
        List<IFile> customKeywordFiles = GroovyUtil.getAllCustomKeywordsScripts(projectEntity);

        for (IFile scriptFile : customKeywordFiles) {
            updateScriptFile(oldScript, newScript, scriptFile);
        }
    }
}
