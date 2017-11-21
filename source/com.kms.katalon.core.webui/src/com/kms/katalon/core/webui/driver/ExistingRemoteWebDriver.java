package com.kms.katalon.core.webui.driver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.URL;
import java.util.Map;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.http.JsonHttpCommandCodec;
import org.openqa.selenium.remote.http.JsonHttpResponseCodec;

import com.kms.katalon.selenium.driver.IExistingRemoteWebDriver;

public class ExistingRemoteWebDriver extends RemoteWebDriver implements IExistingRemoteWebDriver {

    private static final int REMOTE_BROWSER_CONNECT_TIMEOUT = 60000;
    
    private static Field EXECUTOR_COMMAND_CODEC_FIELD;
    
    private static Field EXECUTOR_RESPONSE_CODEC_FIELD;
    
    static {
        try {
            EXECUTOR_COMMAND_CODEC_FIELD = HttpCommandExecutor.class.getDeclaredField("commandCodec");
            EXECUTOR_COMMAND_CODEC_FIELD.setAccessible(true);
            
            EXECUTOR_RESPONSE_CODEC_FIELD = HttpCommandExecutor.class.getDeclaredField("responseCodec");
            EXECUTOR_RESPONSE_CODEC_FIELD.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            //ignore
        }
        
    }

    private String oldSessionId;

    public ExistingRemoteWebDriver(URL remoteAddress, String oldSessionId) throws ConnectException {
        super(remoteAddress, new DesiredCapabilities());
        waitForRemoteBrowserReady(remoteAddress);
        setSessionId(oldSessionId);
        this.oldSessionId = oldSessionId;
        
        if (getCommandExecutor() instanceof HttpCommandExecutor) {
            try {
                initCommandCodecForHttpCommandExecutor();
                initResponseCodecForHttpCommandExecutor();
            } catch (IllegalArgumentException | IllegalAccessException e) {
                //ignore
            }
        }
        startClient();
        startSession(new DesiredCapabilities());
    }
    
    private void initCommandCodecForHttpCommandExecutor() throws IllegalArgumentException, IllegalAccessException {
        HttpCommandExecutor executor = (HttpCommandExecutor) getCommandExecutor();
        EXECUTOR_COMMAND_CODEC_FIELD.set(executor, new JsonHttpCommandCodec());
    }
    
    private void initResponseCodecForHttpCommandExecutor() throws IllegalArgumentException, IllegalAccessException  {
        HttpCommandExecutor executor = (HttpCommandExecutor) getCommandExecutor();
        EXECUTOR_RESPONSE_CODEC_FIELD.set(executor, new JsonHttpResponseCodec());
    }

    public ExistingRemoteWebDriver(String oldSessionId, CommandExecutor executor, Capabilities desiredCapabilities) {
        super(executor, desiredCapabilities);
        this.oldSessionId = oldSessionId;
    }
    
    @Override
    protected void startSession(Capabilities desiredCapabilities) {
        if (this.oldSessionId == null) {
            return;
        }
        super.startSession(desiredCapabilities);
    }
    
    @Override
    protected void startClient() {
        if (this.oldSessionId == null) {
            return;
        }
        super.startClient();
    }

    @Override
    protected Response execute(String driverCommand, Map<String, ?> parameters) {
        
        if (DriverCommand.NEW_SESSION.equals(driverCommand)) {
            return createResponseForNewSession(oldSessionId);
        }
        
        return super.execute(driverCommand, parameters);
    }

    private static void waitForRemoteBrowserReady(URL url) throws ConnectException {
        long waitUntil = System.currentTimeMillis() + REMOTE_BROWSER_CONNECT_TIMEOUT;
        boolean connectable = false;
        while (!connectable) {
            try {
                url.openConnection().connect();
                connectable = true;
            } catch (IOException e) {
                // Cannot connect yet.
            }

            if (waitUntil < System.currentTimeMillis()) {
                // This exception is meant for devs to see, not user so no need to externalize string
                throw new ConnectException(
                        String.format("Unable to connect to browser on host %s and port %s after %s seconds.", //$NON-NLS-1$
                                url.getHost(), url.getPort(), REMOTE_BROWSER_CONNECT_TIMEOUT));
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }
}
