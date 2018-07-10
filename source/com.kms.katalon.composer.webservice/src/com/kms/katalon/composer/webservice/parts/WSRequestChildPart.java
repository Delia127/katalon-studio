package com.kms.katalon.composer.webservice.parts;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class WSRequestChildPart {
    
    protected Composite partComposite;
    
    protected MPart mPart;
    
    @PostConstruct
    public void init(Composite parent, MPart part) {
        this.partComposite = parent;
        this.mPart = part;
    }
    
    public Composite getComposite() {
        return partComposite;
    }
}
