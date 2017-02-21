package com.kms.katalon.addons;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;

/**
 * To resolve issues which are related to
 * 
 * <pre>
 * java.lang.IllegalStateException: Application does not have an active window
 * </pre>
 */
public class ApplicationDefaultSelectedWindowAddon {

    @PostConstruct
    public void init(MApplication application) {
        if (application.getSelectedElement() == null) {
            Optional<MWindow> optionalWindow = application.getChildren()
                    .stream()
                    .filter(window -> window != null)
                    .findFirst();
            if (optionalWindow.isPresent()) {
                application.setSelectedElement(optionalWindow.get());
            }
        }

        application.getContext().activate();
    }

}
