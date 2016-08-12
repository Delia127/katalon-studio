package com.kms.katalon.composer.testdata.parts.provider;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ViewerCell;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.editors.CustomColumViewerEditor;
import com.kms.katalon.composer.testdata.parts.InternalDataRow;
import com.kms.katalon.composer.testdata.parts.InternalTestDataPart;

public class InternalDataColumViewerEditor extends CustomColumViewerEditor {

    private InternalDataColumViewerEditor(CTableViewer viewer,
            ColumnViewerEditorActivationStrategy editorActivationStrategy, int feature) {
        super(viewer, editorActivationStrategy, feature);
    }

    protected boolean isLastEditableCell(ViewerCell focusCell) {
        return isNextNeighborLastCell(focusCell) && isBelowLastRow(focusCell);
    }

    private boolean isNextNeighborLastCell(ViewerCell focusCell) {
        ViewerCell nextNeightbor = focusCell.getNeighbor(ViewerCell.RIGHT, true);
        if (nextNeightbor == null) {
            return false;
        }
        InternalDataRow cell = (InternalDataRow) nextNeightbor.getElement();
        return cell.getCells()
                .get(nextNeightbor.getColumnIndex() - InternalTestDataPart.BASE_COLUMN_INDEX)
                .isLastCell();
    }

    private boolean isBelowLastRow(ViewerCell focusCell) {
        ViewerCell belowNeighbor = focusCell.getNeighbor(ViewerCell.BELOW, false);
        if (belowNeighbor == null) {
            return false;
        }
        return ((InternalDataRow) belowNeighbor.getElement()).isLastRow();
    }

    @Override
    protected int firstEditableColumnIndex() {
        return InternalTestDataPart.BASE_COLUMN_INDEX;
    }

    public static void create(CTableViewer tableViewer,
            ColumnViewerEditorActivationStrategy columnViewerEditorActivationStrategy, int feature) {
        new InternalDataColumViewerEditor(tableViewer, columnViewerEditorActivationStrategy, feature);
    }
}
