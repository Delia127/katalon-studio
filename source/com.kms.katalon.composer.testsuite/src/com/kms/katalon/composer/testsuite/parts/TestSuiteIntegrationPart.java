package com.kms.katalon.composer.testsuite.parts;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.testsuite.integration.TestSuiteIntegrationFactory;
import com.kms.katalon.composer.testsuite.parts.integration.AbstractTestSuiteIntegrationView;
import com.kms.katalon.composer.testsuite.parts.integration.TestSuiteIntegrationViewBuilder;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuiteIntegrationPart {
    private ToolBar toolBar;

    private Composite container;

    private MPart mpart;

    private ParentTestSuiteCompositePart parentTestSuiteCompositePart;

    // Used to store the products that is integrating with test suite view.
    // The key represents for product name
    private Map<String, AbstractTestSuiteIntegrationView> integratingCompositeMap;

    private Map<String, IntegratedEntity> editingIntegratedEntities;

    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        this.mpart = mpart;

        if (mpart.getParent().getParent() instanceof MGenericTile
                && ((MGenericTile<?>) mpart.getParent().getParent()) instanceof MCompositePart) {
            MCompositePart compositePart = (MCompositePart) (MGenericTile<?>) mpart.getParent().getParent();
            if (compositePart.getObject() instanceof ParentTestSuiteCompositePart) {
                parentTestSuiteCompositePart = ((ParentTestSuiteCompositePart) compositePart.getObject());
            }
        }

        createControls(parent);
    }

    private void createControls(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout glMainComposite = new GridLayout(2, false);
        glMainComposite.horizontalSpacing = 20;
        mainComposite.setLayout(glMainComposite);

        Composite toolBarComposite = new Composite(mainComposite, SWT.NONE);
        GridLayout glToolBarComposite = new GridLayout(1, false);
        toolBarComposite.setLayout(glToolBarComposite);
        toolBarComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));

        toolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        container = new Composite(mainComposite, SWT.NONE);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    }

    public MPart getMPart() {
        return mpart;
    }

    public void setDirty(boolean dirty) {
        mpart.setDirty(true);
        parentTestSuiteCompositePart.setDirty(true);
    }

    /**
     * Creates a tool-bar that each owns item represent for integrating product.
     * Sets the first item for default integrating product.
     */
    public void loadInput() {
        clearToolbar();

        integratingCompositeMap = new HashMap<String, AbstractTestSuiteIntegrationView>();

        for (TestSuiteIntegrationViewBuilder builderEntry : TestSuiteIntegrationFactory.getInstance()
                .getSortedViewBuilders()) {
            ToolItem item = new ToolItem(toolBar, SWT.CHECK);
            item.setText(builderEntry.getName());
            integratingCompositeMap.put(builderEntry.getName(), builderEntry.getIntegrationView(
                    parentTestSuiteCompositePart.getTestSuiteClone(), mpart, parentTestSuiteCompositePart));
        }

        for (ToolItem item : toolBar.getItems()) {
            item.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    ToolItem toolItem = (ToolItem) e.getSource();
                    if (toolItem.getSelection()) {
                        changeContainer(toolItem.getText());
                    } else {
                        clearContainer();
                    }
                }
            });
        }

        if (toolBar.getItems().length > 0) {
            toolBar.getItems()[0].setSelection(true);
            changeContainer(toolBar.getItems()[0].getText());
        }
    }

    private void clearToolbar() {
        while (toolBar.getItems().length > 0) {
            toolBar.getItems()[0].dispose();
        }
    }

    private void clearContainer() {
        while (container.getChildren().length > 0) {
            container.getChildren()[0].dispose();
        }
    }

    private void changeContainer(String productName) {
        clearContainer();

        integratingCompositeMap.get(productName).createContainer(container);

        container.layout(true, true);
    }

    public Map<String, IntegratedEntity> getEditingIntegrated() {
        integratingCompositeMap.entrySet().stream().forEach(entry -> {
            if (entry.getValue().needsSaving()) {
                editingIntegratedEntities.put(entry.getKey(), entry.getValue().getEditingIntegrated());
            }
        });

        return editingIntegratedEntities;
    }

    public void onSaveSuccess(TestSuiteEntity testSuite) {
        integratingCompositeMap.entrySet().stream().forEach(entry -> {
            entry.getValue().onSaveSuccess(testSuite);
        });
    }

    public void onSaveFailure(Exception e) {
        integratingCompositeMap.entrySet().stream().forEach(entry -> {
            entry.getValue().onSaveFailure(e);
        });
    }

}
