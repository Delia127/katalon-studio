package com.kms.katalon.composer.global.part;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;

import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class ExecutionProfilePartUI {

    private static final String BUNDLE_URI_EXECUTION_PROFILE = "bundleclass://com.kms.katalon.composer.global/";

    private static final String EXECUTION_PROFILE_COMPOSITE_PART = BUNDLE_URI_EXECUTION_PROFILE
            + ExecutionProfileCompositePart.class.getName();

    private static final String GLOBAL_VARIABLE_PART_URI = BUNDLE_URI_EXECUTION_PROFILE
            + GlobalVariablePart.class.getName();

    private static final String GLOBAL_VARIABLE_EDITOR_PART_URI = BUNDLE_URI_EXECUTION_PROFILE
            + GlobalVariableEditorPart.class.getName();

    private MCompositePart executionProfileCompositePart;

    private MPart globalVariablePart;

    private MPart globalVariableEditorPart;

    private CTabFolder tabFolder;

    public static ExecutionProfilePartUI create(ExecutionProfileEntity executionProfileEntity, MPartStack stack)
            throws IOException, CoreException {
        return new ExecutionProfilePartUI(stack, executionProfileEntity);
    }

    private ExecutionProfilePartUI(MPartStack stack, ExecutionProfileEntity executionProfileEntity)
            throws IOException, CoreException {

        // Somehow use the injected modelService fails, use singleton instead
        EPartService partService = PartServiceSingleton.getInstance().getPartService();
        EModelService modelService = ModelServiceSingleton.getInstance().getModelService();

        String executionProfileCompositePartId = getCompositePartId(executionProfileEntity);

        executionProfileCompositePart = (MCompositePart) modelService.find(executionProfileCompositePartId, stack);
        if (executionProfileCompositePart == null) {
            executionProfileCompositePart = modelService.createModelElement(MCompositePart.class);
            executionProfileCompositePart.setElementId(executionProfileCompositePartId);
            executionProfileCompositePart.setLabel(executionProfileEntity.getName());
            executionProfileCompositePart.setCloseable(true);
            executionProfileCompositePart.setContributionURI(EXECUTION_PROFILE_COMPOSITE_PART);
            executionProfileCompositePart.setTooltip(executionProfileEntity.getIdForDisplay());
            executionProfileCompositePart.setObject(executionProfileEntity);
            executionProfileCompositePart.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
            stack.getChildren().add(executionProfileCompositePart);
        }

        String subPartStackId = executionProfileCompositePartId + IdConstants.TEST_CASE_SUB_PART_STACK_ID_SUFFIX;

        MPartStack subPartStack = (MPartStack) modelService.find(subPartStackId, executionProfileCompositePart);
        if (subPartStack == null) {
            subPartStack = modelService.createModelElement(MPartStack.class);
            subPartStack.setElementId(subPartStackId);
            executionProfileCompositePart.getChildren().add(subPartStack);
        }

        String globalVariablePartId = executionProfileCompositePartId + IdConstants.TEST_CASE_GENERAL_PART_ID_SUFFIX;
        globalVariablePart = (MPart) modelService.find(globalVariablePartId, subPartStack);
        if (globalVariablePart == null) {
            globalVariablePart = modelService.createModelElement(MPart.class);
            globalVariablePart.setElementId(globalVariablePartId);
            globalVariablePart.setLabel("Manual view");
            globalVariablePart.setObject(executionProfileEntity);
            globalVariablePart.setContributionURI(GLOBAL_VARIABLE_PART_URI);
            globalVariablePart.getTags().add(IPresentationEngine.NO_MOVE);
            subPartStack.getChildren().add(globalVariablePart);
            subPartStack.setSelectedElement(globalVariablePart);
        }

        String editorPartId = executionProfileCompositePartId + IdConstants.TEST_CASE_EDITOR_PART_ID_SUFFIX;
        globalVariableEditorPart = (MPart) modelService.find(editorPartId, subPartStack);
        if (globalVariableEditorPart == null) {
            globalVariableEditorPart = modelService.createModelElement(MPart.class);
            globalVariableEditorPart.setElementId(editorPartId);
            globalVariableEditorPart.setLabel("Script view");
            globalVariableEditorPart.setObject(executionProfileEntity);
            globalVariableEditorPart.setContributionURI(GLOBAL_VARIABLE_EDITOR_PART_URI);
            globalVariableEditorPart.getTags().add(IPresentationEngine.NO_MOVE);
            subPartStack.getChildren().add(globalVariableEditorPart);
            subPartStack.setSelectedElement(globalVariableEditorPart);
        }

        stack.setSelectedElement(executionProfileCompositePart);

        partService.activate(executionProfileCompositePart);
        partService.activate(globalVariableEditorPart);
        partService.activate(globalVariablePart);
        tabFolder = (CTabFolder) subPartStack.getWidget();

        initComponents();
    }

    public void initComponents() {
        ExecutionProfileCompositePart executionProfileParentCompositePart = (ExecutionProfileCompositePart) executionProfileCompositePart
                .getObject();
        executionProfileParentCompositePart.initComponents(this);
    }

    private String getCompositePartId(ExecutionProfileEntity executionProfileEntity) {
        return EntityPartUtil.getExecutionProfilePartId(executionProfileEntity.getId());
    }

    public CTabFolder getTabFolder() {
        return tabFolder;
    }

    public CTabItem getGlobalVariableTab() {
        return tabFolder.getItem(0);
    }

    public CTabItem getGlobalVariableEditorTab() {
        return tabFolder.getItem(1);
    }

    public MPart getGlobalVariablePart() {
        return globalVariablePart;
    }

    public MPart getGlobalVariableEditorPart() {
        return globalVariableEditorPart;
    }
}
