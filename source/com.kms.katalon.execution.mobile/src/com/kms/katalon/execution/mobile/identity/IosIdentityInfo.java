package com.kms.katalon.execution.mobile.identity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IosIdentityInfo {

    private String fullname;
    
    private String id;

    public IosIdentityInfo(String identityLine) {
        Matcher fullnameMatcher = Pattern.compile(".+? \"(.+)\"$").matcher(identityLine);
        if (fullnameMatcher.find()) {
            this.setFullname(fullnameMatcher.group(1));
        }
        
        Matcher idMatcher = Pattern.compile("\\((\\w+)\\)\"$").matcher(identityLine);
        if (idMatcher.find()) {
            this.setId(idMatcher.group(1));
        }
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
