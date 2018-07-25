package com.kms.katalon.composer.toolbar;

import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.project.constants.ImageConstants;
import com.kms.katalon.core.webservice.support.UrlEncoder;

public class ForumSearchToolControl implements EventHandler {

    @PostConstruct
    private void createWidget(Composite parent, MApplication app) {
        Text txtSearch = new Text(parent, SWT.SEARCH);
        GridData gdSearch = new GridData(SWT.RIGHT, SWT.FILL, false, true);
        gdSearch.widthHint = 100;
        
        txtSearch.setMessage("Forum Search");
        
        txtSearch.addListener(SWT.Traverse, new Listener() {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                if(event.detail == SWT.TRAVERSE_RETURN) {
                    String searchContent = txtSearch.getText();
                    if (!StringUtils.isBlank(searchContent)) {
                        String encodedSearchContent = UrlEncoder.encode(searchContent, StandardCharsets.UTF_8);
                        Program.launch(String.format("https://forum.katalon.com/search?Search=%s",
                                encodedSearchContent));
                    }
                }
            }
            
        });
    }
    
    @Override
    public void handleEvent(Event event) {
        
    }

    
}
