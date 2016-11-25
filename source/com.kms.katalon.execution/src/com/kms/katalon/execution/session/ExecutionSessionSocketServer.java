package com.kms.katalon.execution.session;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;

import com.kms.katalon.logging.LogUtil;

public class ExecutionSessionSocketServer implements Runnable {
    private static final String LOCAL_HOST_MAC = "localhost";

    private static ExecutionSessionSocketServer instance;

    private ServerSocket serverSocket;

    private List<ExecutionSession> executionSessions;

    private int localPort;

    private String hostAddress;

    public static ExecutionSessionSocketServer getInstance() {
        if (instance == null) {
            try {
                instance = new ExecutionSessionSocketServer();
            } catch (IOException e) {
                LogUtil.logError(e);
            }
        }
        return instance;
    }

    private ExecutionSessionSocketServer() throws IOException {
        serverSocket = new ServerSocket(0);
        localPort = serverSocket.getLocalPort();
        hostAddress = serverSocket.getInetAddress().getHostAddress();
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            hostAddress = LOCAL_HOST_MAC;
        }
        executionSessions = new ArrayList<>();
    }

    public int getServerPort() {
        return localPort;
    }

    public String getServerHost() {
        return hostAddress;
    }

    public void addExecutionSession(ExecutionSession executionSession) {
        synchronized (executionSessions) {
            executionSessions.add(executionSession);
        }
    }

    public void removeExecutionSession(ExecutionSession executionSession) {
        synchronized (executionSessions) {
            executionSessions.remove(executionSession);
        }
    }

    public List<ExecutionSession> getAllAvailableExecutionSessionByDriverTypeName(String driverTypeName) {
        List<ExecutionSession> executionSessions = new ArrayList<>();
        for (ExecutionSession executionSession : getAllExecutionSession()) {
            if (driverTypeName.equals(executionSession.getDriverTypeName()) && executionSession.isAvailable()) {
                executionSessions.add(executionSession);
            }
        }
        return executionSessions;
    }

    public ExecutionSession getExecutionSessionBySessionAndRemoteURL(String sessionId, String remoteServerUrl) {
        for (ExecutionSession executionSession : getAllExecutionSession()) {
            if (executionSession.getSessionId().equals(sessionId)
                    && executionSession.getRemoteUrl().equals(remoteServerUrl)) {
                return executionSession;
            }
        }
        return null;
    }

    public ExecutionSession getExecutionSessionByLogFolderPath(String logFolderPath) {
        for (ExecutionSession executionSession : getAllExecutionSession()) {
            if (logFolderPath.equals(executionSession.getLogFolderPath())) {
                return executionSession;
            }
        }
        return null;
    }

    public List<ExecutionSession> getAllAvailableExecutionSessions() {
        List<ExecutionSession> executionSessions = new ArrayList<>();
        for (ExecutionSession executionSession : getAllExecutionSession()) {
            if (executionSession.isAvailable()) {
                executionSessions.add(executionSession);
            }
        }
        return executionSessions;
    }

    public List<ExecutionSession> getAllExecutionSession() {
        synchronized (executionSessions) {
            return new ArrayList<>(executionSessions);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ExecutionSessionServiceRunnable clientThread = new ExecutionSessionServiceRunnable(clientSocket, this);
                new Thread(clientThread).start();
            } catch (IOException e) {
                LogUtil.logError(e);
            }
        }
    }
}
