package com.kms.katalon.composer.checkpoint.parts.providers;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.entity.checkpoint.CheckpointCell;

public class CheckpointCellLabelProvider extends TypeCheckedStyleCellLabelProvider<List<CheckpointCell>> {

    public CheckpointCellLabelProvider(int columnIndex) {
        super(columnIndex);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<List<CheckpointCell>> getElementType() {
        return (Class<List<CheckpointCell>>) (Object) List.class;
    }

    @Override
    protected Image getImage(List<CheckpointCell> element) {
        return element.get(columnIndex).isChecked() ? ImageConstants.IMG_16_CHECKED : ImageConstants.IMG_16_UNCHECKED;
    }

    @Override
    protected String getText(List<CheckpointCell> element) {
        return ObjectUtils.toString(element.get(columnIndex).getValue());
    }

}
