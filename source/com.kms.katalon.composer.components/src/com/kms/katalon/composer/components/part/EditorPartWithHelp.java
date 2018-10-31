package com.kms.katalon.composer.components.part;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPartConstants;

import com.kms.katalon.composer.components.controls.HelpToolBarForCompositePart;
import com.kms.katalon.composer.util.groovy.GroovyEditorUtil;

public class EditorPartWithHelp implements SavableCompositePart {

    @Inject
    private EPartService partService;
    
    @Inject
    private MDirtyable dirtyable;
    
    private MPart editorPart;
    
    private MCompositePart compositePart;
    
    private String helpUrl;
    
    @PostConstruct
    public void createComposite(Composite parent, MCompositePart compositePart) {
       this.compositePart = compositePart;
       createToolbar(compositePart);
    }
    
    public void init(MPart editorPart, String helpUrl) {
        this.editorPart = editorPart;
        this.helpUrl = helpUrl;
        registerListenersForEditor(editorPart);
    }
    
    private void registerListenersForEditor(MPart editorPart) {
        IEditorPart editor = GroovyEditorUtil.getEditor(editorPart);
        if (editor != null) {
            editor.addPropertyListener(new IPropertyListener() {

                @Override
                public void propertyChanged(Object source, int propId) {
                    if (propId == IWorkbenchPartConstants.PROP_DIRTY) {
                        EditorPartWithHelp.this.dirtyable.setDirty(editor.isDirty());
                    }
                }
            });
        }
    }

    private void createToolbar(MCompositePart compositePart) {
        new HelpToolBarForCompositePart(compositePart, partService) {
            
            @Override
            protected String getDocumentationUrlForPartObject(Object partObject) {
                return EditorPartWithHelp.this.helpUrl;
            }
        };
    }
    
    public IEditorPart getEditor() {
        return GroovyEditorUtil.getEditor(editorPart);
    }
    
    @Override
    public List<MPart> getChildParts() {
        return Arrays.asList(editorPart);
    }

    @Override
    public void save() throws Exception {
        IEditorPart editor = GroovyEditorUtil.getEditor(editorPart);
        editor.doSave(new NullProgressMonitor());
    }
}
