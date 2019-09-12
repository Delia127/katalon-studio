package com.kms.katalon.composer.explorer.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import com.kms.katalon.application.preference.ProjectSettingPreference;
import com.kms.katalon.composer.components.impl.command.ProjectParameterizedCommandBuilder;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.project.ProjectEntity;

public class ExplorerGettingStartView {

    private Link lnkOpenProject;

    private Link lnkNewProject;

    private Composite compositeRecentProjects;

    private Composite container;

    private GridData gdCompositeRecentParent;

    public Composite createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        Label lblGettingStart = new Label(container, SWT.NONE);
        lblGettingStart.setText("START");
        ControlUtils.setFontToBeBold(lblGettingStart);

        Composite compositeProjects = new Composite(container, SWT.NONE);
        compositeProjects.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        GridLayout glCompositeProject = new GridLayout();
        glCompositeProject.marginTop = 0;
        glCompositeProject.marginLeft = 5;
        glCompositeProject.marginRight = 0;
        glCompositeProject.marginBottom = 0;
        compositeProjects.setLayout(glCompositeProject);

        lnkNewProject = new Link(compositeProjects, SWT.NONE);
        lnkNewProject.setText(String.format("<a>%s</a>", "New Project"));

        lnkOpenProject = new Link(compositeProjects, SWT.NONE);
        lnkOpenProject.setText(String.format("<a>%s</a>", "Open Project..."));
        
        Composite compositeRecentParent = new Composite(container, SWT.NONE);
        GridLayout glCompositeRecentParent = new GridLayout();
        glCompositeRecentParent.marginWidth = 0;
        glCompositeRecentParent.marginHeight = 0;
        compositeRecentParent.setLayout(glCompositeRecentParent);
        gdCompositeRecentParent = new GridData(SWT.LEFT, SWT.TOP, false, false);
        gdCompositeRecentParent.verticalIndent = 10;
        compositeRecentParent.setLayoutData(gdCompositeRecentParent);

        Label lblRecent = new Label(compositeRecentParent, SWT.NONE);
        lblRecent.setText("RECENT");
        ControlUtils.setFontToBeBold(lblRecent);
        
        compositeRecentProjects = new Composite(compositeRecentParent, SWT.NONE);
        GridLayout glCompositeRecentProjects = new GridLayout();
        glCompositeRecentProjects.marginTop = 0;
        glCompositeRecentProjects.marginLeft = 5;
        glCompositeRecentProjects.marginRight = 0;
        glCompositeRecentProjects.marginBottom = 0;
        compositeRecentProjects.setLayout(glCompositeRecentProjects);

        setLayoutForRecentComposite();

        registerControlListeners();
        return container;
    }

    public void refreshRecentProjects() {
        while (compositeRecentProjects.getChildren().length > 0) {
            compositeRecentProjects.getChildren()[0].dispose();
        }
        setLayoutForRecentComposite();
        container.layout(true, true);
    }

    private void setLayoutForRecentComposite() {
        if (getRecentProjects().isEmpty()) {
            gdCompositeRecentParent.heightHint = 0;
        } else {
            gdCompositeRecentParent.heightHint = -1;
            createRecentProjectComposite();
        }
    }

    private void createRecentProjectComposite() {
        for (ProjectEntity project : getRecentProjects()) {
            Composite compositeItemProject = new Composite(compositeRecentProjects, SWT.NONE);
            GridLayout glCompositeItemProject = new GridLayout(2, false);
            glCompositeItemProject.marginWidth = 0;
            glCompositeItemProject.marginHeight = 0;
            compositeItemProject.setLayout(glCompositeItemProject);

            Link lnkProject = new Link(compositeItemProject, SWT.NONE);
            lnkProject.setText(String.format("<a>%s</a>", project.getName()));
            lnkProject.setToolTipText(project.getFolderLocation());
            lnkProject.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    try {
                        new CommandCaller().call(new ProjectParameterizedCommandBuilder().createRecentProjectParameterizedCommand(project));
                    } catch (CommandException ex) {
                        LoggerSingleton.logError(ex);
                    }
                }
            });

            StyledText txtProjectLocation = new StyledText(compositeItemProject, SWT.NONE);
            txtProjectLocation.setText(String.format("(%s)", project.getFolderLocation()));
            StyledString styledString = new StyledString()
                    .append(String.format("(%s)", project.getFolderLocation()), StyledString.DECORATIONS_STYLER);
            txtProjectLocation.setStyleRanges(styledString.getStyleRanges());
        }
    }

    private List<ProjectEntity> getRecentProjects() {
        try {
            return new ProjectSettingPreference().getRecentProjects();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return Collections.emptyList();
        }
    }

    private void registerControlListeners() {
        lnkNewProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    new CommandCaller().call(IdConstants.NEW_PROJECT_COMMAND_ID);
                } catch (CommandException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });

        lnkOpenProject.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    new CommandCaller().call(IdConstants.OPEN_PROJECT_COMMAND_ID);
                } catch (CommandException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });
    }
}
