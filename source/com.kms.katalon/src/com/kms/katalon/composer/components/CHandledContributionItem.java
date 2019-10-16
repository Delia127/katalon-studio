package com.kms.katalon.composer.components;

import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.renderers.swt.HandledContributionItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ImageUtil;

@SuppressWarnings("restriction")
public class CHandledContributionItem extends HandledContributionItem {

    private static final String ICON_URI = "iconURI"; //$NON-NLS-1$

    private static final String DISABLED_URI = "disabledURI"; //$NON-NLS-1$

    @Override
    public void update(String id) {
        super.update(id);
        updateIcons();
    }

    @Override
    protected void updateIcons() {
        if (!(getWidget() instanceof Item)) {
            return;
        }
        Item item = (Item) getWidget();
        String iconURI = getModel().getIconURI() != null ? getModel().getIconURI() : StringUtils.EMPTY; // $NON-NLS-1$
        String disabledURI = getDisabledIconURI(getModel());
        Object disabledData = item.getData(DISABLED_URI);
        if (disabledData == null) {
            disabledData = StringUtils.EMPTY; // $NON-NLS-1$
        }
        Image iconImage = getImage(iconURI);
        item.setImage(iconImage);
        item.setData(ICON_URI, iconURI);
        if (item instanceof ToolItem) {
            ToolItem toolItem = (ToolItem) item;
            toolItem.setToolTipText(getModel().getLabel());
            toolItem.setText("");
            if (toolItem.getDisabledImage() == null) {
                iconImage = getImage(disabledURI);
                if (iconImage != null) {
                    toolItem.setDisabledImage(iconImage);
                    item.setData(DISABLED_URI, disabledURI);
                }
            }
        }

    }

    private String getDisabledIconURI(MItem toolItem) {
        Object obj = toolItem.getTransientData().get(IPresentationEngine.DISABLED_ICON_IMAGE_KEY);
        return obj instanceof String ? (String) obj : StringUtils.EMPTY; // $NON-NLS-1$
    }

    private Image getImage(String iconURI) {
        if (StringUtils.isBlank(iconURI)) {
            return null;
        }
        try {
            return ImageUtil.loadImage(iconURI);
        } catch (MalformedURLException e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

}
