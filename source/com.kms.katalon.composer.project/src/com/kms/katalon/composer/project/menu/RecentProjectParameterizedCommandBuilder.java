package com.kms.katalon.composer.project.menu;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.ui.commands.ICommandService;

import com.kms.katalon.composer.components.impl.handler.WorkbenchUtilizer;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.project.ProjectEntity;

public class RecentProjectParameterizedCommandBuilder extends WorkbenchUtilizer {

    public ParameterizedCommand createRecentProjectParameterizedCommand(ProjectEntity project) throws CommandException {
        Command recentProjectCommand = getService(ICommandService.class).getCommand(
                IdConstants.OPEN_RECENT_PROJECT_COMMAND_ID);
        List<Parameterization> parameterization = new ArrayList<>();
        IParameter param = recentProjectCommand.getParameter(IdConstants.OPEN_RECENT_PROJECT_COMMAND_PARAMETER_ID);
        Parameterization params = new Parameterization(param, project.getId());
        parameterization.add(params);
        return new ParameterizedCommand(recentProjectCommand,
                parameterization.toArray(new Parameterization[parameterization.size()]));
    }
}
