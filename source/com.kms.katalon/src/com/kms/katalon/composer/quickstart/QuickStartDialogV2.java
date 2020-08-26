package com.kms.katalon.composer.quickstart;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.application.userprofile.UserExperienceLevel;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ComponentBuilder;
import com.kms.katalon.composer.components.util.FontUtil;
import com.kms.katalon.composer.components.util.StyleContext;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.entity.project.QuickStartProjectType;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.collections.Pair;

public class QuickStartDialogV2 extends BaseQuickStartDialog {

    private List<Pair<?, String>> quest1Options = Arrays.asList(
            new Pair<UserExperienceLevel, String>(UserExperienceLevel.FRESHER, "No, not really"),
            new Pair<UserExperienceLevel, String>(UserExperienceLevel.EXPERIENCED, "Yes, I do"));

    private List<Pair<?, String>> quest2Options = Arrays
            .asList(QuickStartProjectType.WEBUI, QuickStartProjectType.MOBILE, QuickStartProjectType.WEBSERVICE,
                    QuickStartProjectType.BDD)
            .stream()
            .map(projectType -> new Pair<QuickStartProjectType, String>(projectType, projectType.getName()))
            .collect(Collectors.toList());

    private UserExperienceLevel userLevel = UserExperienceLevel.FRESHER;

    private QuickStartProjectType projectType = QuickStartProjectType.WEBUI;

    public QuickStartDialogV2(Shell parentShell) {
        super(parentShell);
    }

    protected String getMainButtonText() {
        return "Get Started";
    }

    protected String getTipContent() {
        return StringUtils.EMPTY;
    }

    @Override
    protected void createContent(Composite container) {
        Composite contentContainer = ComponentBuilder.gridContainer(container)
                .gridMargin(20, 120, 20)
                .gridVerticalSpacing(5)
                .build();

        createWelcomeHeader(contentContainer);
        createQuickStartQuestions(contentContainer);

        Trackings.trackQuickStartOpen();
    }

    private void createWelcomeHeader(Composite parent) {
        ComponentBuilder.image(parent, IImageKeys.LOGO_74).center().build();

        ComponentBuilder.label(parent)
                .text("Welcome to Katalon Studio")
                .fontSize(FontUtil.SIZE_H1)
                .center()
                .gridMarginTop(5)
                .build();

        ComponentBuilder.label(parent)
                .text("Let us help you and your team become automation experts")
                .fontSize(FontUtil.SIZE_H2)
                .color(ColorUtil.getColor("#636384"))
                .center()
                .build();
    }

    private void createQuickStartQuestions(Composite parent) {
        StyleContext.setColor(ColorUtil.getColor("#636384"));

        createQuickStartQuestion(parent, "1. Do you have experience with Katalon Studio before?", quest1Options,
                userLevel, option -> {
                    userLevel = (UserExperienceLevel) option;
                });
        createQuickStartQuestion(parent, "2. Which type of automation testing do you want to work on next?",
                quest2Options, projectType, option -> {
                    projectType = (QuickStartProjectType) option;
                });

        StyleContext.prevColor();
    }

    private void createQuickStartQuestion(Composite parent, String question, List<Pair<?, String>> options,
            Object defaultValue, SelectionCallback selectionCallback) {
        ComponentBuilder.label(parent).text(question).bold().gridMarginTop(30).gridMarginBottom(5).build();

        Composite optionsGroup = ComponentBuilder.gridContainer(parent, options.size())
                .gridHorizontalSpacing(30)
                .gridMarginLeft(20)
                .build();

        options.forEach(option -> {
            Button btnOption = ComponentBuilder.button(optionsGroup, SWT.RADIO)
                    .text(option.getRight())
                    .data(option.getLeft())
                    .onClick((event) -> {
                        Button button = (Button) event.widget;
                        if (button.getSelection()) {
                            selectionCallback.call(button.getData());
                        }
                    })
                    .build();
            btnOption.setSelection(option.getLeft() == defaultValue);
        });
    }

    public UserExperienceLevel getUserLevel() {
        return userLevel;
    }

    public QuickStartProjectType getProjectType() {
        return projectType;
    }
    
    @Override
    protected void okPressed() {
        boolean isNewUser = userLevel == UserExperienceLevel.FRESHER;
        Trackings.trackQuickStartFirstQuestion(isNewUser, projectType.name());
        super.okPressed();
    }

    protected boolean canClose() {
        return false;
    }
}
