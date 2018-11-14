package com.kms.katalon.composer.testcase.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.constants.TextContentType;
import com.kms.katalon.composer.components.impl.editors.MirrorEditor;
import com.kms.katalon.composer.components.impl.handler.DocumentReadyHandler;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.entity.variable.VariableEntityWrapper;

public class TestCaseVariableEditorPart extends CPart implements SavableCompositePart {
	
	MirrorEditor mirrorEditor;
	
	Composite composite;
	
	private TestCaseCompositePart parentTestCaseCompositePart;
    
	String contentScript = StringUtils.EMPTY;
	
	boolean contentChanged = false;
	
    private List<VariableEntity> variables;

    @Inject
    private EPartService partService;

    MPart mpart;
	
    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        this.mpart = mpart;
        this.variables = new ArrayList<VariableEntity>();
        if (mpart.getParent().getParent() instanceof MGenericTile
                && ((MGenericTile<?>) mpart.getParent().getParent()) instanceof MCompositePart) {
            MCompositePart compositePart = (MCompositePart) (MGenericTile<?>) mpart.getParent().getParent();
            if (compositePart.getObject() instanceof TestCaseCompositePart) {
                parentTestCaseCompositePart = ((TestCaseCompositePart) compositePart.getObject());
            }
        }
        initialize(mpart, partService);
        
        createComposite(parent);
    }

	private void createComposite(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
		
        mirrorEditor = new MirrorEditor(composite, SWT.NONE);
        mirrorEditor.setEditable(true);
        mirrorEditor.registerDocumentHandler(new DocumentReadyHandler() {
            
            @Override
            public void onDocumentReady() {
                mirrorEditor.changeMode(TextContentType.XML.getText());
            }
        });
        
        mirrorEditor.addListener(SWT.Modify, event ->{
        	if(contentChanged == false){
        		contentChanged = true;
        	}else{
        		setDirty(true);
        	}
        });
	}
	
    public void setDirty(boolean isDirty) {
    	mpart.setDirty(true);
        parentTestCaseCompositePart.getChildTestCasePart().getTreeTableInput().reloadTestCaseVariables(getVariables());
        parentTestCaseCompositePart.updateDirty();
    }

    public VariableEntity[] getVariables() {
        if (variables == null) {
            return new VariableEntity[0];
        }
        return variables.toArray(new VariableEntity[variables.size()]);
    }

    public void setScriptContentFrom(VariableEntityWrapper entityWrapper) throws Exception {
        String incomingContentScript = getScriptContentFromVariableEntityWrapper(entityWrapper);
        if (!contentScript.equals(incomingContentScript)) {
            mirrorEditor.setText(incomingContentScript);
            if (!contentScript.equals(StringUtils.EMPTY))
                contentChanged = true;
            contentScript = incomingContentScript;
        }
    }
    
    public String getScriptContentFromVariableEntityWrapper(VariableEntityWrapper entityWrapper) throws Exception{
        String content = StringUtils.EMPTY;
        if (entityWrapper != null && entityWrapper.getVariables() != null && entityWrapper.getVariables().size() != 0) {
            // Arrays.asList returns unmodifiable list and therefore
            // operations (add, delete, modify, etc) are not supported
            List<VariableEntity> incomingVariablesList = entityWrapper.getVariables();
            VariableEntityWrapper variableEntityWrapper = new VariableEntityWrapper();
            variableEntityWrapper.setVariables(incomingVariablesList);
            content = GlobalVariableController.toXmlString(variableEntityWrapper);
        }
        return content;
    }
	
	public String getScriptContent(){
		return mirrorEditor.getText();
	}

	public MPart getMPart() {
		return mpart;
	}
	
    @Persist
    @Override
	public void save() {

	}
    
    @PreDestroy
    @Override
    public void dispose() {
        super.dispose();
        partService.hidePart(mpart);
    }

	public MirrorEditor getEditor() {
		return mirrorEditor;
	}

	@Override
	public List<MPart> getChildParts() {
		List<MPart> res = new ArrayList<>();
		res.add(getMPart());
		return res;
	}

}
