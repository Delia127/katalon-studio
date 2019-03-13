package com.kms.katalon.composer.testcase.parts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testcase.components.KeywordTreeViewerToolTipSupport;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.keywords.BuiltinKeywordFolderBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.CustomKeywordFolderBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.IKeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserControlTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserFolderTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntity;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntityTransfer;
import com.kms.katalon.composer.testcase.providers.KeywordBrowserEntityViewerFilter;
import com.kms.katalon.composer.testcase.providers.KeywordTreeContentProvider;
import com.kms.katalon.composer.testcase.providers.KeywordTreeLabelProvider;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.custom.keyword.KeywordClass;

public class KeywordBrowserPart implements EventHandler {
    private static final String SEARCH_TEXT_DEFAULT_VALUE = "Enter text to search...";

    private TreeViewer treeViewer;

    private Text txtSearchInput;

    private KeywordTreeLabelProvider labelProvider;

    private KeywordBrowserEntityViewerFilter viewerFilter;

    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        createControls(parent);
        registerListerners();
        hookDoubleClickEvent();
        hookDragEvent();
        loadTreeData();
    }

    private void registerListerners() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
        eventBroker.subscribe(EventConstants.KEYWORD_BROWSER_REFRESH, this);
    }

    private void hookDoubleClickEvent() {
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (event.getSelection() == null) {
                    return;
                }

                Object selectedElement = ((IStructuredSelection) event.getSelection()).getFirstElement();

                if (selectedElement == null || !(selectedElement instanceof KeywordBrowserFolderTreeEntity)) {
                    return;
                }

                KeywordTreeContentProvider contentProvider = (KeywordTreeContentProvider) treeViewer.getContentProvider();
                if (contentProvider.hasChildren(selectedElement)) {
                    treeViewer.setExpandedState(selectedElement, !treeViewer.getExpandedState(selectedElement));
                }
            }
        });
    }

    private void hookDragEvent() {
        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

        DragSource dragSource = new DragSource(treeViewer.getTree(), operations);
        dragSource.setTransfer(new Transfer[] { new KeywordBrowserTreeEntityTransfer() });
        dragSource.addDragListener(new DragSourceListener() {
            public void dragStart(DragSourceEvent event) {
                List<IKeywordBrowserTreeEntity> treeEntities = getKeywordTreeEntityFromTree();
                if (treeEntities.size() > 0) {
                    event.doit = true;
                } else {
                    event.doit = false;
                }
            }

            public void dragSetData(DragSourceEvent event) {
                List<IKeywordBrowserTreeEntity> treeEntities = getKeywordTreeEntityFromTree();
                if (treeEntities.size() > 0) {
                    event.data = treeEntities.toArray(new IKeywordBrowserTreeEntity[treeEntities.size()]);
                }
            }

            public void dragFinished(DragSourceEvent event) {
                // do nothing
            }
        });
    }

    private List<IKeywordBrowserTreeEntity> getKeywordTreeEntityFromTree() {
        TreeItem[] selection = treeViewer.getTree().getSelection();
        List<IKeywordBrowserTreeEntity> treeEntities = new ArrayList<IKeywordBrowserTreeEntity>();
        for (TreeItem item : selection) {
            if (item.getData() instanceof KeywordBrowserTreeEntity) {
                treeEntities.add((KeywordBrowserTreeEntity) item.getData());
            } else if (item.getData() instanceof KeywordBrowserControlTreeEntity) {
                treeEntities.add((KeywordBrowserControlTreeEntity) item.getData());
            }
        }
        return treeEntities;
    };

    private void createControls(Composite parent) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(1, false));

        Composite searchComposite = new Composite(parent, SWT.BORDER);
        searchComposite.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout glSearchComposite = new GridLayout(1, false);
        glSearchComposite.verticalSpacing = 0;
        glSearchComposite.horizontalSpacing = 0;
        glSearchComposite.marginWidth = 0;
        glSearchComposite.marginHeight = 0;
        searchComposite.setLayout(glSearchComposite);

        GridData grSearchComposite = new GridData(GridData.FILL_HORIZONTAL);
        grSearchComposite.heightHint = 24;
        searchComposite.setLayoutData(grSearchComposite);

        txtSearchInput = new Text(searchComposite, SWT.NONE);
        txtSearchInput.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        txtSearchInput.setMessage(SEARCH_TEXT_DEFAULT_VALUE);

        GridData gdTxtInput = new GridData(GridData.FILL_HORIZONTAL);
        gdTxtInput.grabExcessVerticalSpace = true;
        gdTxtInput.verticalAlignment = SWT.CENTER;
        txtSearchInput.setLayoutData(gdTxtInput);
        txtSearchInput.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                filterKeywordTreeEntitiesBySearchedText();
            }
        });

        txtSearchInput.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    filterKeywordTreeEntitiesBySearchedText();
                }
            }
        });

        treeViewer = new TreeViewer(parent, SWT.BORDER);
        treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

        treeViewer.setContentProvider(new KeywordTreeContentProvider());
        treeViewer.setLabelProvider(labelProvider = new KeywordTreeLabelProvider());
        treeViewer.addFilter(viewerFilter = new KeywordBrowserEntityViewerFilter());
        KeywordTreeViewerToolTipSupport.enableFor(treeViewer);

    }

    protected void filterKeywordTreeEntitiesBySearchedText() {
        if (treeViewer.getTree().isDisposed()) {
            return;
        }
        final String searchString = txtSearchInput.getText();
        while (treeViewer.isBusy()) {
            // wait for tree is not busy
        }
        BusyIndicator.showWhile(treeViewer.getTree().getDisplay(), new Runnable() {

            @Override
            public void run() {
                try {
                    if (searchString.equals(txtSearchInput.getText()) && treeViewer.getInput() != null) {
                        treeViewer.getTree().setRedraw(false);
                        labelProvider.setSearchString(searchString);
                        viewerFilter.setSearchString(searchString);
                        treeViewer.refresh(true);
                        if (searchString != null && !searchString.isEmpty()) {
                            treeViewer.expandAll();
                        } else {
                            treeViewer.collapseAll();
                        }
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                } finally {
                    treeViewer.getTree().setRedraw(true);
                }
            }
        });
    }

    protected void loadTreeData() {
        KeywordBrowserFolderTreeEntity builtinKeywordRootFolder = loadBuiltinKeywordFolderTreeEntity();
        CustomKeywordFolderBrowserTreeEntity customKeywordRootFolder = new CustomKeywordFolderBrowserTreeEntity(null);

        List<IKeywordBrowserTreeEntity> keywordTreeEntities = new ArrayList<IKeywordBrowserTreeEntity>();
        keywordTreeEntities.add(builtinKeywordRootFolder);
        keywordTreeEntities.add(customKeywordRootFolder);
        keywordTreeEntities.add(loadControlKeywordFolderTreeEntity());
        treeViewer.setInput(keywordTreeEntities);
        treeViewer.refresh();
    }

    private static KeywordBrowserFolderTreeEntity loadBuiltinKeywordFolderTreeEntity() {
        List<IKeywordBrowserTreeEntity> keywordTreeEntities = new ArrayList<IKeywordBrowserTreeEntity>();
        KeywordBrowserFolderTreeEntity builtinKeywordRootFolder = new KeywordBrowserFolderTreeEntity(
                StringConstants.KEYWORD_BROWSER_BUILTIN_KEYWORD_ROOT_TREE_ITEM_LABEL, null, keywordTreeEntities);
        for (KeywordClass builtInKeywordContributor : KeywordController.getInstance().getBuiltInKeywordClasses()) {
            keywordTreeEntities.add(new BuiltinKeywordFolderBrowserTreeEntity(builtInKeywordContributor.getName(),
                    builtInKeywordContributor.getSimpleName(), builtInKeywordContributor.getLabelName(),
                    builtinKeywordRootFolder));
        }
        return builtinKeywordRootFolder;
    }

    private static KeywordBrowserFolderTreeEntity loadControlKeywordFolderTreeEntity() {
        List<IKeywordBrowserTreeEntity> controlKeywordFolders = new ArrayList<IKeywordBrowserTreeEntity>();
        KeywordBrowserFolderTreeEntity controlKeywordFolderTreeEntity = new KeywordBrowserFolderTreeEntity(
                StringConstants.KEYWORD_BROWSER_CONTROL_KEYWORD_ROOT_TREE_ITEM_LABEL, null, controlKeywordFolders);

        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.CALL_TEST_CASE_MENU_ITEM_ID,
                TreeTableMenuItemConstants.CALL_TEST_CASE_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));

        controlKeywordFolders.add(loadDecisionMakingControlKeywordFolderTreeEntity(controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(loadLoopingControlKeywordFolderTreeEntity(controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(loadBranchingControlKeywordFolderTreeEntity(controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(loadExceptionHandlingControlKeywordFolderTreeEntity(controlKeywordFolderTreeEntity));

        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.BINARY_STATEMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.BINARY_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));

        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.ASSERT_STATEMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.ASSERT_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));

        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(
                TreeTableMenuItemConstants.CALL_METHOD_STATEMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.CALL_METHOD_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));

        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.METHOD_MENU_ITEM_ID,
                TreeTableMenuItemConstants.METHOD_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));

        return controlKeywordFolderTreeEntity;
    }

    private static KeywordBrowserFolderTreeEntity loadDecisionMakingControlKeywordFolderTreeEntity(
            KeywordBrowserFolderTreeEntity parentFolderTreeEntity) {
        List<IKeywordBrowserTreeEntity> controlKeywordFolders = new ArrayList<IKeywordBrowserTreeEntity>();
        KeywordBrowserFolderTreeEntity controlKeywordFolderTreeEntity = new KeywordBrowserFolderTreeEntity(
                TreeTableMenuItemConstants.DECISION_MAKING_STATEMENT_MENU_ITEM_LABEL, parentFolderTreeEntity,
                controlKeywordFolders);
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.IF_STATEMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.IF_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.ELSE_STATEMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.ELSE_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.ELSE_IF_STATEMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.ELSE_IF_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.SWITCH_STATMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.SWITCH_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.CASE_STATMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.CASE_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.DEFAULT_STATMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.DEFAULT_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        return controlKeywordFolderTreeEntity;
    }

    private static KeywordBrowserFolderTreeEntity loadLoopingControlKeywordFolderTreeEntity(
            KeywordBrowserFolderTreeEntity parentFolderTreeEntity) {
        List<IKeywordBrowserTreeEntity> controlKeywordFolders = new ArrayList<IKeywordBrowserTreeEntity>();
        KeywordBrowserFolderTreeEntity controlKeywordFolderTreeEntity = new KeywordBrowserFolderTreeEntity(
                TreeTableMenuItemConstants.LOOPING_STATEMENT_MENU_ITEM_LABEL, parentFolderTreeEntity, controlKeywordFolders);
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.FOR_STATEMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.FOR_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.WHILE_STATEMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.WHILE_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        return controlKeywordFolderTreeEntity;
    }

    private static KeywordBrowserFolderTreeEntity loadBranchingControlKeywordFolderTreeEntity(
            KeywordBrowserFolderTreeEntity parentFolderTreeEntity) {
        List<IKeywordBrowserTreeEntity> controlKeywordFolders = new ArrayList<IKeywordBrowserTreeEntity>();
        KeywordBrowserFolderTreeEntity controlKeywordFolderTreeEntity = new KeywordBrowserFolderTreeEntity(
                TreeTableMenuItemConstants.BRANCHING_STATEMENT_MENU_ITEM_LABEL, parentFolderTreeEntity, controlKeywordFolders);
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.BREAK_STATMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.BREAK_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.CONTINUE_STATMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.CONTINUE_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.RETURN_STATMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.RETURN_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));

        return controlKeywordFolderTreeEntity;
    }

    private static KeywordBrowserFolderTreeEntity loadExceptionHandlingControlKeywordFolderTreeEntity(
            KeywordBrowserFolderTreeEntity parentFolderTreeEntity) {
        List<IKeywordBrowserTreeEntity> controlKeywordFolders = new ArrayList<IKeywordBrowserTreeEntity>();
        KeywordBrowserFolderTreeEntity controlKeywordFolderTreeEntity = new KeywordBrowserFolderTreeEntity(
                TreeTableMenuItemConstants.EXCEPTION_HANDLING_STATEMENT_MENU_ITEM_LABEL, parentFolderTreeEntity,
                controlKeywordFolders);
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.TRY_STATEMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.TRY_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.CATCH_STATMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.CATCH_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.FINALLY_STATMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.FINALLY_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));
        controlKeywordFolders.add(new KeywordBrowserControlTreeEntity(TreeTableMenuItemConstants.THROW_STATMENT_MENU_ITEM_ID,
                TreeTableMenuItemConstants.THROW_STATEMENT_MENU_ITEM_LABEL, controlKeywordFolderTreeEntity));

        return controlKeywordFolderTreeEntity;
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.PROJECT_OPENED)
                || event.getTopic().equals(EventConstants.KEYWORD_BROWSER_REFRESH)) {
            loadTreeData();
        }
    }
}
