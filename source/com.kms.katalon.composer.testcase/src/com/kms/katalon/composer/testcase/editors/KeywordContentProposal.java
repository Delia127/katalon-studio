package com.kms.katalon.composer.testcase.editors;

import org.eclipse.jface.fieldassist.ContentProposal;

public class KeywordContentProposal extends ContentProposal {
    
    private Object data;
    
    public KeywordContentProposal(String content, String description, Object data) {
        super(content, content, description);
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
