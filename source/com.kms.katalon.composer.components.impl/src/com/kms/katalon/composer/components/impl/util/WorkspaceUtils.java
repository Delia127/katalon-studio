package com.kms.katalon.composer.components.impl.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class WorkspaceUtils {
    public static void cleanWorkspace() {
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            try {
                // Remote all existing projects out of workspace
                project.delete(false, true, null);
            } catch (CoreException ignored) {}
        }
    }
}
