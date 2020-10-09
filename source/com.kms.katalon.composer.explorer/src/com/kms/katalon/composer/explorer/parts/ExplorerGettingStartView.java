package com.kms.katalon.composer.explorer.parts;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.common.CommandException;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import com.kms.katalon.application.preference.ProjectSettingPreference;
import com.kms.katalon.composer.components.impl.command.ProjectParameterizedCommandBuilder;
import com.kms.katalon.composer.components.impl.handler.CommandCaller;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.FontUtil;
import com.kms.katalon.composer.project.constants.ImageConstants;
import com.kms.katalon.composer.project.menu.SampleProjectParameterizedCommandBuilder;
import com.kms.katalon.composer.project.sample.SampleProjectType;
import com.kms.katalon.composer.project.sample.SampleRemoteProject;
import com.kms.katalon.composer.project.sample.SampleRemoteProjectProvider;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.project.ProjectEntity;

public class ExplorerGettingStartView {

    private Composite compositeRecentProjects;
    
    private Composite compositeSampleProjects;

    private Composite container;

    private GridData gdCompositeRecentParent;
    
    private GridData gdCompositeSampleParent;

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
        glCompositeProject.verticalSpacing = 7;
        compositeProjects.setLayout(glCompositeProject);

        ComponentBuilder.label(compositeProjects)
        .text("New Project")
        .fontSize(FontUtil.SIZE_H4)
        .size(100, 30)
        .primaryButton()
        .onClick(event -> {
            onNewProjectClicked();
        })
        .build();

        ComponentBuilder.label(compositeProjects)
        .text("Open Project")
        .fontSize(FontUtil.SIZE_H4)
        .size(100, 30)
        .grayButton()
        .onClick(event -> {
            onOpenProjectClicked();
        })
        .build();
       
        Composite compositeRecentParent = new Composite(container, SWT.NONE);
        GridLayout glCompositeRecentParent = new GridLayout();
        glCompositeRecentParent.marginWidth = 0;
        glCompositeRecentParent.marginHeight = 0;
        compositeRecentParent.setLayout(glCompositeRecentParent);
        gdCompositeRecentParent = new GridData(SWT.LEFT, SWT.TOP, false, false);
        gdCompositeRecentParent.verticalIndent = 10;
        compositeRecentParent.setLayoutData(gdCompositeRecentParent);

        Label lblRecent = new Label(compositeRecentParent, SWT.NONE);
        lblRecent.setText("RECENT PROJECTS");
        ControlUtils.setFontToBeBold(lblRecent);
        
        compositeRecentProjects = new Composite(compositeRecentParent, SWT.NONE);
        GridLayout glCompositeRecentProjects = new GridLayout();
        glCompositeRecentProjects.marginTop = 0;
        glCompositeRecentProjects.marginLeft = 5;
        glCompositeRecentProjects.marginRight = 0;
        glCompositeRecentProjects.marginBottom = 0;
        compositeRecentProjects.setLayout(glCompositeRecentProjects);

        setLayoutForRecentComposite();
        
        Composite compositeSampleParent = new Composite(container, SWT.NONE);
        GridLayout glCompositeSampleParent = new GridLayout();
        glCompositeSampleParent.marginWidth = 0;
        glCompositeSampleParent.marginHeight = 0;
        compositeSampleParent.setLayout(glCompositeSampleParent);
        gdCompositeSampleParent = new GridData(SWT.LEFT, SWT.TOP, false, false);
        gdCompositeSampleParent.verticalIndent = 10;
        compositeSampleParent.setLayoutData(gdCompositeSampleParent);

        Label lblSample = new Label(compositeSampleParent, SWT.NONE);
        lblSample.setText("SAMPLE PROJECTS");
        ControlUtils.setFontToBeBold(lblSample);
        
        compositeSampleProjects = new Composite(compositeSampleParent, SWT.NONE);
        GridLayout glCompositeSampleProjects = new GridLayout();
        glCompositeSampleProjects.marginTop = 0;
        glCompositeSampleProjects.marginLeft = 5;
        glCompositeSampleProjects.marginRight = 0;
        glCompositeSampleProjects.marginBottom = 0;
        compositeSampleProjects.setLayout(glCompositeSampleProjects);

        setLayoutForSampleComposite();

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
    
    private void setLayoutForSampleComposite() {
        gdCompositeSampleParent.heightHint = -1;
        createSampleProjectComposite();
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
    
    private void createSampleProjectComposite() {
        for (SampleRemoteProject project : getSampleProjects()) {
            Composite compositeItemProject = new Composite(compositeSampleProjects, SWT.NONE);
            GridLayout glCompositeItemProject = new GridLayout(2, false);
            glCompositeItemProject.marginWidth = 0;
            glCompositeItemProject.marginHeight = 0;
            compositeItemProject.setLayout(glCompositeItemProject);
            compositeItemProject.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));
            
            Label projectImage = new Label(compositeItemProject, SWT.NONE);
            GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, true);
            projectImage.setLayoutData(layoutData);
            projectImage.setImage(getIconImageForProject(project.getType()));

            Label projectName = new Label(compositeItemProject, SWT.NONE);
            projectName.setText(project.getName());
            GridData layoutData1 = new GridData(SWT.LEFT, SWT.CENTER, false, false);
            projectName.setLayoutData(layoutData1);
            
            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseUp(MouseEvent e) {
                    try {
                        new CommandCaller().call(new SampleProjectParameterizedCommandBuilder().createRemoteProjectParameterizedCommand(project));
                    } catch (CommandException ex) {
                        LoggerSingleton.logError(ex);
                    }
                }
            };
            compositeItemProject.addMouseListener(mouseAdapter);
            projectImage.addMouseListener(mouseAdapter);
            projectName.addMouseListener(mouseAdapter);
        }
    }
    
    public Image getIconImageForProject(SampleProjectType projectType) {
        switch (projectType) {
            case MOBILE:
                return ImageConstants.SAMPLE_MOBILE_16;
            case WS:
                return ImageConstants.SAMPLE_WS_16;
            default:
                return ImageConstants.WEB_ICON;
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
    
    private List<SampleRemoteProject> getSampleProjects() {
        return SampleRemoteProjectProvider.getCachedProjects();
    }

    private void onNewProjectClicked() {
        try {
            new CommandCaller().call(IdConstants.NEW_PROJECT_COMMAND_ID);
        } catch (CommandException ex) {
            LoggerSingleton.logError(ex);
        }
    }
    
    private void onOpenProjectClicked() {
        try {
            new CommandCaller().call(IdConstants.OPEN_PROJECT_COMMAND_ID);
        } catch (CommandException ex) {
            LoggerSingleton.logError(ex);
        }
    }
}
