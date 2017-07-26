package com.kms.katalon.execution.session;

public class RemoteMobileExecutionSession extends MobileExecutionSession {
    private String remoteType;

    public RemoteMobileExecutionSession(String title, String sessionId, String remoteUrl, String driverTypeName,
            String logFolderPath, String remoteType) {
        super(title, sessionId, remoteUrl, driverTypeName, logFolderPath);
        this.remoteType = remoteType;
    }

    public String getRemoteType() {
        return remoteType;
    }

}
