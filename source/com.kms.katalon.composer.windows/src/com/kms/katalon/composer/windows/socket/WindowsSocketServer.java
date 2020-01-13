package com.kms.katalon.composer.windows.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;

public class WindowsSocketServer {

    public void start() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(50003);

        SocketIOServer server = new SocketIOServer(config);
        
        server.addConnectListener(new ConnectListener() {
            
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("Client connected");
            }
        });
    }
}
