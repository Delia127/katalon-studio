package com.kms.katalon.composer.components.impl.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.control.StyledTextMessage;
import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;

public class TreeEntitySelectionDialog extends ElementTreeSelectionDialog {
    private StyledText txtInput;

    private CLabel lblSearch;

    private IEntityLabelProvider labelProvider;

    private AbstractEntityViewerFilter entityViewerFilter;

    private boolean isSearched;

    private TreeViewer treeViewer;

    public TreeEntitySelectionDialog(Shell parent, IEntityLabelProvider labelProvider,
            ITreeContentProvider contentProvider, AbstractEntityViewerFilter entityViewerFilter) {
        super(parent, labelProvider, contentProvider);
        this.labelProvider = labelProvider;
        this.entityViewerFilter = entityViewerFilter;
    }

    @Override
    public TreeViewer createTreeViewer(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        Composite searchComposite = new Composite(parent, SWT.BORDER);
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

        txtInput = new StyledText(searchComposite, SWT.SINGLE);
        txtInput.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
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

        StyledTextMessage styledTextMessage = new StyledTextMessage(txtInput);
        styledTextMessage.setMessage(StringConstants.DIA_SEARCH_TEXT_DEFAULT_VALUE);
        
        Canvas canvasSearch = new Canvas(searchComposite, SWT.NONE);
        canvasSearch.setLayout(new FillLayout(SWT.HORIZONTAL));
        canvasSearch.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        isSearched = false;
        lblSearch = new CLabel(canvasSearch, SWT.NONE);
        lblSearch.setBackground(searchComposite.getBackground());
        updateStatusSearchLabel();

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

        treeViewer = super.createTreeViewer(parent);
        expandTreeViewerToInitialElements();
        treeViewer.addFilter(entityViewerFilter);
        treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        treeViewer.getTree().setFocus();

        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateOKButtonStatus();
            }
        });

        return treeViewer;
    }

    @Override
    protected Control createContents(Composite parent) {
        try {
            return super.createContents(parent);
        } finally {
            updateOKButtonStatus();
        }
    }

    private void updateOKButtonStatus() {
        Button btnOk = getButton(OK);
        if (btnOk != null && !btnOk.isDisposed()) {
            btnOk.setEnabled(treeViewer.getStructuredSelection().size() > 0);
        }
    }

    @Override
    public void create() {
        super.create();
        updateOKButtonStatus();
    }

    protected TreeViewer getTreeViewer() {
        return treeViewer;
    }

    @Override
    protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
        return new CTreeViewer(parent, style);
    }

    protected void expandTreeViewerToInitialElements() {
        if (getInitialElementSelections() != null && !getInitialElementSelections().isEmpty()) {
            getTreeViewer().expandToLevel(getInitialElementSelections().get(0), TreeViewer.ALL_LEVELS);
        }
    }

    protected void updateStatusSearchLabel() {
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
                    if (txtInput.isDisposed())
                        return;
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
