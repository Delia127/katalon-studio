package com.kms.katalon.composer.keyword.logging;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.kms.katalon.util.FileHashUtil;

public class UpdatedKeywordObject {

    private final String TAB_DELIMITER = "\t";

    private final String NEW_LINE_DELIMITER = "\n";

    private final String absolutePath;

    private final ACTION_TYPE action;

    public enum ACTION_TYPE {
        OVERWRITE, CREATE_DUPLICATE, SKIP_KEEP_OLD_FILE
    }
    
    public UpdatedKeywordObject(String absolutePath, ACTION_TYPE action) {
        this.absolutePath = absolutePath;
        this.action = action;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public ACTION_TYPE getUpdateStatus() {
        return action;
    }

    public String buildSingleRow() throws NoSuchAlgorithmException, IOException {
        return absolutePath + TAB_DELIMITER + FileHashUtil.hash(absolutePath, "MD5") + TAB_DELIMITER + TAB_DELIMITER
                + action.toString() + NEW_LINE_DELIMITER;
    }

}
