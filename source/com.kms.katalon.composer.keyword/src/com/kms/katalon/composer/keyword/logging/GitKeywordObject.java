package com.kms.katalon.composer.keyword.logging;

import java.util.LinkedList;
import java.util.List;

public class GitKeywordObject {

    private String git_url;

    private String commit_id;

    private String timestamp;

    private List<ChangeFile> files;

    private ActionType type;

    public enum ActionType {
        KEYWORD_IMPORT
    }

    public GitKeywordObject(String git_url, String commitId, String timestamp) {
        this.git_url = git_url;
        this.commit_id = commitId;
        this.timestamp = timestamp;
        this.files = new LinkedList<>();
    }

    public void addChangeFile(ChangeFile file) {
        files.add(file);
    }

    public String getCommitId() {
        return commit_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public List<ChangeFile> getChangeFiles() {
        return files;
    }

    public ActionType getType() {
        return type;
    }
    
    public String getGitUrl() {
        return git_url;
    }
    
    public void setType(ActionType type) {
        this.type = type;
    }

    public void setCommitId(String commitId) {
        this.commit_id = commitId;
    }
    
    public void setTimeStamp(String time) {
        this.timestamp = time;
    }

    public void setListChangeFiles(List<ChangeFile> files) {
        this.files = files;
    }

}
