package com.kms.katalon.composer.integration.git.handlers;

import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.egit.ui.internal.selection.SelectionUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.ui.ISources;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.services.IEvaluationService;

import com.kms.katalon.composer.components.impl.handler.WorkbenchUtilizer;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

/**
 * Abstract class to re-direct command to Egit Handlers
 *
 */

@SuppressWarnings("restriction")
public abstract class AbstractGitProjectHandler extends WorkbenchUtilizer {
    protected ExecutionEvent createExecutionEventForCommand(String id) {
        IProject groovyProject = getCurrentIProject();
        if (groovyProject == null) {
            return null;
        }
        IEvaluationContext currentState = getService(IEvaluationService.class).getCurrentState();
        currentState.addVariable(ISources.ACTIVE_MENU_SELECTION_NAME, new StructuredSelection(groovyProject));
        Command command = getService(ICommandService.class).getCommand(id);
        return new ExecutionEvent(command, new HashMap<Object, Object>(), null, currentState);
    }

    @CanExecute
    public boolean canExecute() {
        return getCurrentProject() != null && getHandler().isEnabled();
    }

    protected IProject getCurrentIProject() {
        return GroovyUtil.getGroovyProject(getCurrentProject());
    }

    protected ProjectEntity getCurrentProject() {
        return ProjectController.getInstance().getCurrentProject();
    }

    @Execute
    public void execute() {
        ExecutionEvent executionEvent = createExecutionEventForCommand(getEgitCommandId());
        if (executionEvent == null) {
            return;
        }
        try {
            getHandler().execute(executionEvent);
        } catch (ExecutionException e) {
            LoggerSingleton.logError(e);
        }
    }

    protected IStructuredSelection createIProjectSelection() {
        return new StructuredSelection(getCurrentIProject());
    }

    /**
     * @return the original egit command's id
     */
    public abstract String getEgitCommandId();

    /**
     * @return the original egit command's handler
     */
    public abstract AbstractHandler getHandler();

    /**
     * @return current project's repository if it has one
     * @throws ExecutionException
     */
    protected Repository getRepository() {
        return SelectionUtils.getRepository(createIProjectSelection());
    }
}
