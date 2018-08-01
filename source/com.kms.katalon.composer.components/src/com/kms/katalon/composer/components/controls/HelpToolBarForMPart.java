package com.kms.katalon.composer.components.controls;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.kms.katalon.composer.components.constants.ComposerComponentsMessageConstants;

public class HelpToolBarForMPart extends ToolBarForMPart {
    
    public HelpToolBarForMPart(MPart part, String documentationLink) {
        super(part);
        new HelpToolItem(this, documentationLink, ComposerComponentsMessageConstants.LABEL_HELP_WITH_DOCUMENTATION);
    }

}