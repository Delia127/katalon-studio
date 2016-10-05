package com.kms.katalon.composer.components;

import java.util.List;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarSeparator;
import org.eclipse.e4.ui.workbench.renderers.swt.MenuManagerRenderer;
import org.eclipse.e4.ui.workbench.renderers.swt.ToolBarManagerRenderer;
import org.eclipse.jface.action.AbstractGroupMarker;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;

import com.kms.katalon.processors.ToolbarProcessor;

@SuppressWarnings("restriction")
public class CToolBarManagerRenderer extends ToolBarManagerRenderer {

    @Override
    public void processContents(MElementContainer<MUIElement> container) {
        if (container == null) {
            return;
        }

        // For other toolbars, use as default
        if (!ToolbarProcessor.KATALON_TOOLBAR_ID.equals(container.getElementId())) {
            super.processContents(container);
            return;
        }

        // For top toolbar
        ToolBarManager toolbarManager = getManager((MToolBar) (Object) container);
        if (toolbarManager == null) {
            return;
        }

        // Process any contents of the newly created ME
        List<MUIElement> parts = container.getChildren();
        if (parts != null) {
            MUIElement[] plist = parts.toArray(new MUIElement[parts.size()]);
            for (int i = 0; i < plist.length; i++) {
                modelProcessSwitch(toolbarManager, (MToolBarElement) plist[i]);
            }
        }
        toolbarManager.update(true);

        ToolBar toolbar = getToolbarFrom(container.getWidget());
        if (toolbar != null) {
            toolbar.pack(true);
            toolbar.getShell().layout(new Control[] { toolbar }, SWT.DEFER);
        }
    }

    private ToolBar getToolbarFrom(Object widget) {
        if (widget instanceof ToolBar) {
            return (ToolBar) widget;
        }

        if (widget instanceof Composite) {
            Composite intermediate = (Composite) widget;
            if (intermediate.isDisposed()) {
                return null;
            }

            for (Control control : intermediate.getChildren()) {
                if (control.getData() instanceof ToolBarManager) {
                    return (ToolBar) control;
                }
            }
        }

        return null;
    }

    private void modelProcessSwitch(ToolBarManager toolbarManager, MToolBarElement toolbarElement) {
        if (toolbarElement instanceof MHandledToolItem) {
            processHandledItem(toolbarManager, (MHandledToolItem) toolbarElement);
            return;
        }

        if (toolbarElement instanceof MToolBarSeparator) {
            processSeparator(toolbarManager, (MToolBarSeparator) toolbarElement);
        }
    }

    private void processSeparator(ToolBarManager toolbarManager, MToolBarSeparator toolbarSeparator) {
        IContributionItem contributionItem = getContribution(toolbarSeparator);
        if (contributionItem != null) {
            return;
        }

        toolbarSeparator.setRenderer(this);

        AbstractGroupMarker marker = getGroupMaker(toolbarSeparator);
        if (marker != null) {
            addToManager(toolbarManager, toolbarSeparator, marker);
            linkModelToContribution(toolbarSeparator, marker);
        }
    }

    private AbstractGroupMarker getGroupMaker(MToolBarSeparator toolbarSeparator) {
        if (toolbarSeparator.isVisible() && !toolbarSeparator.getTags().contains(MenuManagerRenderer.GROUP_MARKER)) {
            Separator marker = new Separator();
            marker.setId(toolbarSeparator.getElementId());
            return marker;
        }

        if (toolbarSeparator.getElementId() != null) {
            return new GroupMarker(toolbarSeparator.getElementId());
        }

        return null;
    }

    private void processHandledItem(ToolBarManager parentManager, MHandledToolItem handledToolItem) {
        IContributionItem contributionItem = getContribution(handledToolItem);
        if (contributionItem != null) {
            return;
        }

        handledToolItem.setRenderer(this);
        final IEclipseContext itemContext = getContext(handledToolItem);
        CHandledContributionItem cHandledContributionItem = ContextInjectionFactory.make(
                CHandledContributionItem.class, itemContext);
        cHandledContributionItem.setModel(handledToolItem);
        cHandledContributionItem.setVisible(handledToolItem.isVisible());
        addToManager(parentManager, handledToolItem, cHandledContributionItem);
        linkModelToContribution(handledToolItem, cHandledContributionItem);
    }

    private void addToManager(ToolBarManager toolbarManager, MToolBarElement toolbarElement,
            IContributionItem ccontributionItem) {
        MElementContainer<MUIElement> parent = toolbarElement.getParent();
        // technically this shouldn't happen
        if (parent == null) {
            toolbarManager.add(ccontributionItem);
            return;
        }

        int index = parent.getChildren().indexOf(toolbarElement);

        // shouldn't be -1, but better safe than sorry
        if (index > toolbarManager.getSize() || index == -1) {
            toolbarManager.add(ccontributionItem);
            return;
        }

        toolbarManager.insert(index, ccontributionItem);
    }

}
