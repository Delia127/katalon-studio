package com.kms.katalon.composer.KatalonQuickStart;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.wizard.IWizardPage;
import com.kms.katalon.composer.components.impl.wizard.SimpleWizardDialog;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.ImageConstants;

public class QuickStartDialog extends SimpleWizardDialog {

    private int lastTreeWidth;

    // Controls
    private Composite stepArea;

    private CTableViewer tableViewer;

    private TableViewerColumn pageViewerColumn;

    public QuickStartDialog(Shell parentShell) {
        super(parentShell);
        lastTreeWidth = 220;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite imageComposite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(SWT.BOTTOM, SWT.BOTTOM, false, false);
        Image imageTitle = ImageConstants.IMG_INTRO_SCREEN_TITLE;
        gridData.widthHint = imageTitle.getBounds().width;
        gridData.heightHint = imageTitle.getBounds().height;
        imageComposite.setLayoutData(gridData);
        imageComposite.setBackgroundImage(imageTitle);
        
        // create the top level composite for the dialog
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        applyDialogFont(composite);
        // initialize the dialog units
        initializeDialogUnits(composite);
        // create the dialog area and button bar
        dialogArea = createDialogArea(composite);

        return composite;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogComposite = (Composite) super.oldDialogArea(parent);
        GridLayout glDialogComposite = (GridLayout) dialogComposite.getLayout();
        glDialogComposite.numColumns = 4;
        glDialogComposite.marginHeight = 0;
        glDialogComposite.marginWidth = 0;
        glDialogComposite.verticalSpacing = 0;
        glDialogComposite.horizontalSpacing = 0;
        
        Composite stepTreeComposite = createStepTableComposite(dialogComposite);

        layoutTreeAreaControl(stepTreeComposite);

        createSash(dialogComposite, stepTreeComposite);

        Label label = new Label(dialogComposite, SWT.SEPARATOR);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));

        createWizardArea(dialogComposite);

        return dialogComposite;
    }

    private Composite createStepTableComposite(Composite dialogComposite) {
        Composite stepTreeComposite = new Composite(dialogComposite, SWT.NONE);
        stepTreeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        stepTreeComposite.setLayout(new GridLayout(1, false));
        stepTreeComposite.setBackground(ColorUtil.getWhiteBackgroundColor());

        tableViewer = new CTableViewer(stepTreeComposite, SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        pageViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        pageViewerColumn.getColumn().setWidth(200);

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        return stepTreeComposite;
    }

    protected Composite createStepAreaComposite(Composite dialogComposite) {
        stepArea = new Composite(dialogComposite, SWT.NONE);
        GridLayout glStepArea = new GridLayout(1, false);
        glStepArea.horizontalSpacing = 0;
        glStepArea.verticalSpacing = 0;
        glStepArea.marginWidth = 0;
        glStepArea.marginHeight = 0;
        stepArea.setLayout(glStepArea);
        stepArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Label separator = new Label(stepArea, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        stepDetailsComposite = new Composite(stepArea, SWT.NONE);
        stepDetailsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        stepDetailsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        return stepArea;
    }

    protected void layoutTreeAreaControl(Control control) {
        GridData gd = new GridData(GridData.FILL_VERTICAL);
        gd.horizontalAlignment = SWT.FILL;
        gd.widthHint = getLastRightWidth();
        gd.verticalSpan = 1;
        control.setLayoutData(gd);
    }

    protected int getLastRightWidth() {
        return lastTreeWidth;
    }

    protected Sash createSash(final Composite composite, final Control rightControl) {
        final Sash sash = new Sash(composite, SWT.VERTICAL);
        sash.setLayoutData(new GridData(GridData.FILL_VERTICAL));
        sash.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
        sash.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (event.detail == SWT.DRAG) {
                    return;
                }
                int shift = event.x - sash.getBounds().x;
                GridData data = (GridData) rightControl.getLayoutData();
                int newWidthHint = data.widthHint + shift;
                if (newWidthHint < 20) {
                    return;
                }
                Point computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
                Point currentSize = getShell().getSize();
                // if the dialog wasn't of a custom size we know we can shrink
                // it if necessary based on sash movement.
                boolean customSize = !computedSize.equals(currentSize);
                data.widthHint = newWidthHint;
                setLastTreeWidth(newWidthHint);
                composite.layout(true);
                // recompute based on new widget size
                computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
                // if the dialog was of a custom size then increase it only if
                // necessary.
                if (customSize) {
                    computedSize.x = Math.max(computedSize.x, currentSize.x);
                }
                computedSize.y = Math.max(computedSize.y, currentSize.y);
                if (computedSize.equals(currentSize)) {
                    return;
                }
                setShellSize(computedSize.x, computedSize.y);
            }
        });
        return sash;
    }

    protected void setLastTreeWidth(int newWidthHint) {
        lastTreeWidth = newWidthHint;
    }

    private void setShellSize(int width, int height) {
        Rectangle preferred = getShell().getBounds();
        preferred.width = width;
        preferred.height = height;
        getShell().setBounds(getConstrainedShellBounds(preferred));
    }

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(arg | SWT.RESIZE);
    }

    @Override
    protected void registerControlModifyListeners() {
        super.registerControlModifyListeners();
        // Disable user click on step table
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    tableViewer.setSelection(StructuredSelection.EMPTY);
                }
            }
        });

        // Disable color change when selected item changed
        tableViewer.getTable().addListener(SWT.EraseItem, new Listener() {
            public void handleEvent(Event event) {
                // Selection:
                event.detail &= ~SWT.SELECTED;
                // Expect: selection now has no visual effect.
                // Actual: selection remains but changes from light blue to white.

                // MouseOver:
                event.detail &= ~SWT.HOT;
                // Expect: mouse over now has no visual effect.
                // Actual: behavior remains unchanged.
            }
        });

        // Set items's height of Step Table to 32 pixel
        tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
            public void handleEvent(Event event) {
                // height cannot be per row so simply set
                event.height = 32;
            }
        });

    }
    
    @Override
    protected Collection<IWizardPage> getWizardPages() {
        return Arrays.asList(new IWizardPage[] {
                new WebTestingWizardPage(),
                new APITestingWizardPage(),
                new MobileTestingWizardPage(),
                new DatadrivenTestingWizardPage(),
                new BDDTestingWizardPage(),
                new CIIntegrationWizardPage(),
                new SDLCIntergrationWizardPage(),
                new PluginStoreWizardPage(),
                new AdvancedReportWizardPage()
        });
    }

    @Override
    protected void setInput() {
        super.setInput();
        pageViewerColumn.setLabelProvider(new WizardTableLabelTest(wizardManager));
        tableViewer.setInput(wizardManager.getWizardPages());
    }

    @Override
    protected void showPage(IWizardPage page) {
        super.showPage(page);
        tableViewer.refresh(true);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(1000, 900);
    }

    @Override
    protected String getDialogTitle() {
        return "Katalon Studio Quick Start";
    }

    @Override
    protected void finishPressed() {
        super.okPressed();
    }
}
