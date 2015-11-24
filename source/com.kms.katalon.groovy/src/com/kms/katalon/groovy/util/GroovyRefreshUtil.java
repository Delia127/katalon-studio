package com.kms.katalon.groovy.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
        IFolder resource = project.getFolder(folderRelativePath.replace(File.separator, "/"));
        resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
    }

    public static void refreshFolder(String folderRelativePath, ProjectEntity projectEntity, int depth)
            throws CoreException {
        IProject project = GroovyUtil.getGroovyProject(projectEntity);
        IFolder resource = project.getFolder(folderRelativePath.replace(File.separator, "/"));
        resource.refreshLocal(depth, new NullProgressMonitor());
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
        IFile resource = project.getFile(fileRelativePath.replace(File.separator, "/"));
        resource.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
    }

    public static void updateStringScriptReferences(String oldScript, String newScript, ProjectEntity projectEntity)
            throws CoreException, IOException {
        updateScriptReferencesInTestCaseAndCustomScripts("'" + oldScript + "'", "'" + newScript + "'", projectEntity);
        updateScriptReferencesInTestCaseAndCustomScripts("\"" + oldScript + "\"", "\"" + newScript + "\"",
                projectEntity);
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
            String testCaseContent = IOUtils.toString(scriptFileStreamContent);

            scriptFileStreamContent.close();
            scriptFileStreamContent = null;

            if (testCaseContent.contains(oldScript)) {
                String newTestCaseContent = testCaseContent.replace(oldScript, newScript);
                newScriptFileInputStream = IOUtils.toInputStream(newTestCaseContent);
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

    public static void updateScriptReferencesInTestCaseScripts(String oldScript, String newScript,
            ProjectEntity projectEntity) throws CoreException, IOException {

        IFolder testCaseRootFolder = GroovyUtil.getTestCaseScriptSourceFolder(projectEntity);
        List<IFile> testCaseFiles = GroovyUtil.getAllScriptFiles(testCaseRootFolder);

        for (IFile scriptFile : testCaseFiles) {
            updateScriptFile(oldScript, newScript, scriptFile);
        }
    }

    private static void updateScriptReferencesInCustomKeywordScripts(String oldScript, String newScript,
            ProjectEntity projectEntity) throws CoreException, IOException {
        IFolder customKeywordRootFolder = GroovyUtil.getCustomKeywordSourceFolder(projectEntity);
        List<IFile> customKeywordFiles = GroovyUtil.getAllScriptFiles(customKeywordRootFolder);

        for (IFile scriptFile : customKeywordFiles) {
            updateScriptFile(oldScript, newScript, scriptFile);
        }
    }

    /**
     * Find reference data from all Test Case
     * 
     * @param ref a reference to looking for
     * @param projectEntity Project Entity
     * @return List of Test Case script
     * @throws CoreException
     * @throws IOException
     */
    public static List<IFile> findReferencesInTestCaseScripts(String ref, ProjectEntity projectEntity)
            throws CoreException, IOException {
        IFolder testCaseRootFolder = GroovyUtil.getTestCaseScriptSourceFolder(projectEntity);
        List<IFile> testCaseFiles = GroovyUtil.getAllScriptFiles(testCaseRootFolder);
        List<IFile> affectedTestCases = new ArrayList<IFile>();
        for (IFile scriptFile : testCaseFiles) {
            if (hasRefInScript(ref, scriptFile)) {
                affectedTestCases.add(scriptFile);
            }
        }
        return affectedTestCases;
    }

    /**
     * Has any reference data in Test Case scripts
     * 
     * @param ref a reference to looking for
     * @param projectEntity Project Entity
     * @return true if reference has been found in script. Otherwise, false will be returned.
     * @throws CoreException
     * @throws IOException
     */
    public static boolean hasReferencesInTestCaseScripts(String ref, ProjectEntity projectEntity) throws CoreException,
            IOException {
        IFolder testCaseRootFolder = GroovyUtil.getTestCaseScriptSourceFolder(projectEntity);
        List<IFile> testCaseFiles = GroovyUtil.getAllScriptFiles(testCaseRootFolder);
        for (IFile scriptFile : testCaseFiles) {
            if (hasRefInScript(ref, scriptFile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * To check whether a file contains the reference data or not
     * 
     * @param ref a reference to looking for
     * @param scriptFile Test Case script file
     * @return true if found any reference in Test Case script. Otherwise, false will be returned.
     * @throws CoreException
     * @throws IOException
     */
    private static boolean hasRefInScript(String ref, IFile scriptFile) throws CoreException, IOException {
        InputStream scriptFileStreamContent = scriptFile.getContents();
        boolean hasRef = false;
        try {
            String testCaseContent = IOUtils.toString(scriptFileStreamContent);
            if (testCaseContent.contains(ref)) {
                hasRef = true;
            }
        } finally {
            if (scriptFileStreamContent != null) {
                scriptFileStreamContent.close();
            }
        }
        return hasRef;
    }

    /**
     * Update reference entity ID in Test Case script to null value
     * 
     * @param entityId entity ID to be replaced in Test Case script
     * @param affectedTestCaseScripts List of affected Test Case script
     * @throws CoreException
     * @throws IOException
     */
    public static void removeReferencesInTestCaseScripts(String entityId, List<IFile> affectedTestCaseScripts)
            throws CoreException, IOException {
        String singleQuoteRef = "'" + entityId + "'";
        String doubleQuoteRef = "\"" + entityId + "\"";
        for (IFile scriptFile : affectedTestCaseScripts) {
            updateScriptFile(singleQuoteRef, "null", scriptFile);
            updateScriptFile(doubleQuoteRef, "null", scriptFile);
        }
    }
}
