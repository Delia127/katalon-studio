package com.kms.katalon.entity.testcase;

import org.apache.commons.lang.StringUtils;

public class WSVerificationTestCaseEntity extends TestCaseEntity {

    private static final long serialVersionUID = 1L;
    
    private String script;

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
    
    @Override
    public String getRelativePathForUI() {
        return StringUtils.EMPTY;
    }
}
