package com.kms.katalon.composer.webservice.view;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;

public class ExpandableComposite {

    /** The default margin of bodyComposite. This is the sum of arrow icon width (16) and CLabel.DEFAULT_MARGIN. */
    private static final int DEFAULT_BODY_MARGIN = 24;

    private CLabel lblHeaderTitle;

    private Composite parent, bodyComposite, composite;

    boolean isExpanded;

    private String title;

    private int detailColumns;

    public ExpandableComposite(Composite parent, String title, int colums, boolean expandOnShow) {
        this.parent = parent;
        this.title = title;
        this.detailColumns = colums;
        this.isExpanded = expandOnShow;
    }

    public Composite createControl() {
        composite = new Composite(parent, SWT.NONE);
        GridLayout glCompositeInfo = new GridLayout();
        glCompositeInfo.verticalSpacing = 0;
        glCompositeInfo.horizontalSpacing = 0;
        glCompositeInfo.marginWidth = 0;
        glCompositeInfo.marginHeight = 0;
        composite.setLayout(glCompositeInfo);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        lblHeaderTitle = new CLabel(composite, SWT.NONE);
        lblHeaderTitle.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
        lblHeaderTitle.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        lblHeaderTitle.setText(title);
        lblHeaderTitle.setFont(JFaceResources.getFontRegistry().getBold(""));

        bodyComposite = new Composite(composite, SWT.NONE);
        GridLayout glBodyComposite = new GridLayout(detailColumns, false);
        glBodyComposite.marginRight = DEFAULT_BODY_MARGIN;
        glBodyComposite.marginLeft = DEFAULT_BODY_MARGIN;
        glBodyComposite.marginHeight = 0;
        glBodyComposite.marginWidth = 0;
        bodyComposite.setLayout(glBodyComposite);
        bodyComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        hookControlSelectListerners();

        expandCollapseBody();

        return bodyComposite;
    }

    private void redrawHeaderTitleIndicator() {
        lblHeaderTitle.getParent().setRedraw(false);
        lblHeaderTitle.setImage(isExpanded ? ImageConstants.IMG_16_ARROW_DOWN : ImageConstants.IMG_16_ARROW);
        lblHeaderTitle.getParent().setRedraw(true);
    }

    private void hookControlSelectListerners() {
        lblHeaderTitle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                expandCollapseBody();
            }
        });
    }

    private void expandCollapseBody() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                bodyComposite.setVisible(isExpanded);
                GridData gdBodyComposite = (GridData) bodyComposite.getLayoutData();
                gdBodyComposite.exclude = !isExpanded;
                composite.layout(true, true);
                composite.getParent().layout();
                redrawHeaderTitleIndicator();
                isExpanded = !isExpanded;
            }
        });
    }

}
