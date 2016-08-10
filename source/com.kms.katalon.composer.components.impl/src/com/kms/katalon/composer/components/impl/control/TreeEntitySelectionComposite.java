package com.kms.katalon.composer.components.impl.control;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;

public class TreeEntitySelectionComposite extends Composite {
    private Text txtInput;

    private CLabel lblSearch;

    private IEntityLabelProvider labelProvider;

    private AbstractEntityViewerFilter entityViewerFilter;

    private ITreeContentProvider contentProvider;

    private boolean isSearched;

    private TreeViewer treeViewer;

    private int treeViewerStyle;

    public TreeEntitySelectionComposite(Composite parent, int treeViewerStyle, ITreeContentProvider contentProvider,
            AbstractEntityViewerFilter entityViewerFilter, IEntityLabelProvider labelProvider) {
        super(parent, SWT.NONE);
        this.treeViewerStyle = treeViewerStyle;
        this.contentProvider = contentProvider;
        this.entityViewerFilter = entityViewerFilter;
        this.labelProvider = labelProvider;
        treeViewer = createTreeView();
        isSearched = false;
        updateStatusSearchLabel();
    }

    private TreeViewer createTreeView() {
        setLayout(new GridLayout(1, false));
        
        Composite searchComposite = new Composite(this, SWT.BORDER);
        searchComposite.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout glSearchComposite = new GridLayout(2, false);
        glSearchComposite.verticalSpacing = 0;
        glSearchComposite.horizontalSpacing = 0;
        glSearchComposite.marginWidth = 0;
        glSearchComposite.marginHeight = 0;
        searchComposite.setLayout(glSearchComposite);
        GridData grSearchComposite = new GridData(GridData.FILL_HORIZONTAL);
        grSearchComposite.heightHint = 24;
        searchComposite.setLayoutData(grSearchComposite);

        txtInput = new Text(searchComposite, SWT.NONE);
        txtInput.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        txtInput.setMessage(StringConstants.DIA_SEARCH_TEXT_DEFAULT_VALUE);
        GridData gdTxtInput = new GridData(GridData.FILL_HORIZONTAL);
        gdTxtInput.grabExcessVerticalSpace = true;
        gdTxtInput.verticalAlignment = SWT.CENTER;
        txtInput.setLayoutData(gdTxtInput);
        txtInput.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                filterSearchedText();
            }
        });
        Canvas canvasSearch = new Canvas(searchComposite, SWT.NONE);
        canvasSearch.setLayout(new FillLayout(SWT.HORIZONTAL));
        canvasSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        lblSearch = new CLabel(canvasSearch, SWT.NONE);
        lblSearch.setBackground(searchComposite.getBackground());
        lblSearch.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
        lblSearch.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if (isSearched) {
                    isSearched = !isSearched;
                    txtInput.setText(StringUtils.EMPTY);
                }
            }
        });

        TreeViewer treeViewer = new CTreeViewer(this, treeViewerStyle | SWT.NONE);
        expandTreeViewerToInitialElements();
        treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        treeViewer.getTree().setFocus();
        treeViewer.setLabelProvider(labelProvider);
        treeViewer.addFilter(entityViewerFilter);
        treeViewer.setContentProvider(contentProvider);
        return treeViewer;
    }

    public void setInput(Object[] treeEntities) {
        treeViewer.setInput(treeEntities);
    }

    protected void expandTreeViewerToInitialElements() {
        if (getInitialElementSelections() != null && !getInitialElementSelections().isEmpty()) {
            getTreeViewer().expandToLevel(getInitialElementSelections().get(0), TreeViewer.ALL_LEVELS);
        }
    }

    private List<ITreeEntity> getInitialElementSelections() {
        return Collections.emptyList();
    }

    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    private void updateStatusSearchLabel() {
        if (isSearched) {
            lblSearch.setImage(ImageConstants.IMG_16_CLOSE_SEARCH);
            lblSearch.setToolTipText(StringConstants.DIA_IMAGE_CLOSE_SEARCH_TOOLTIP);
        } else {
            lblSearch.setImage(ImageConstants.IMG_16_SEARCH);
            lblSearch.setToolTipText(StringConstants.DIA_IMAGE_SEARCH_TOOLTIP);
        }
    }

    protected String getSearchMessage() {
        try {
            return StringConstants.DIA_KEYWORD_SEARCH_ALL + ":" + txtInput.getText();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return StringUtils.EMPTY;
        }
    }

    protected void filterSearchedText() {
        final String searchString = txtInput.getText();
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    if (txtInput.isDisposed()) {
                        return;
                    }
                    if (searchString.equals(txtInput.getText()) && getTreeViewer().getInput() != null) {
                        String broadcastMessage = getSearchMessage();
                        labelProvider.setSearchString(broadcastMessage);
                        entityViewerFilter.setSearchString(broadcastMessage);
                        getTreeViewer().refresh();
                        if (searchString != null && !searchString.isEmpty()) {
                            isSearched = true;
                            getTreeViewer().expandAll();
                        } else {
                            isSearched = false;
                            getTreeViewer().collapseAll();
                        }
                        updateStatusSearchLabel();
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }
        });
    }
}
