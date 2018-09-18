package com.kms.katalon.composer.toolbar;

import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.core.webservice.support.UrlEncoder;
import com.kms.katalon.tracking.service.Trackings;

public class ForumSearchToolControl implements EventHandler {

    @PostConstruct
    private void createWidget(Composite parent, MApplication app) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        
        Canvas canvas = new Canvas(composite, SWT.NONE);
        GridData gdCanvas = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdCanvas.widthHint = 140;
        gdCanvas.heightHint = 24;
        canvas.setLayoutData(gdCanvas);
        
        Text txtSearch = new Text(canvas, SWT.NONE);
        txtSearch.setBounds(23, 4, 100, 15);
        txtSearch.setMessage(StringConstants.MSG_FORUM_SEARCH);
        
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
                    Trackings.trackForumSearch(searchContent);
                }
            }
        });
        
        canvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                e.gc.drawImage(ImageConstants.FORUM_SEARCH_BOX, 0, 0);                
            }
          });
    }
    
    @Override
    public void handleEvent(Event event) { 
    }
}
