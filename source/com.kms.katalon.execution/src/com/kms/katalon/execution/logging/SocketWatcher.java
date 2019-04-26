package com.kms.katalon.execution.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import javax.net.ServerSocketFactory;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.IOUtils;

import com.kms.katalon.core.logging.XMLLoggerParser;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.logging.LogUtil;

public class SocketWatcher extends AbstractLogWatcher {
    protected BufferedReader reader;
    protected InputStreamReader is;

    private int port;

    private ILogCollection logCollection;

    public SocketWatcher(int port, int delayInMillis, ILogCollection logCollection) {
        super(delayInMillis);
        this.port = port;
        this.logCollection = logCollection;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
            socket = serverSocket.accept();

            is = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(is);

            while (!isStopSignal() || reader.ready()) {
                StringBuilder builder = new StringBuilder();

                Thread.sleep(delayInMillis);
                while (reader.ready()) {
                    String line = reader.readLine();
                    if (line == null || isIgnoredLine(line)) {
                        break;
                    }

                    builder.append(LINE_SEPERATOR + line);

                    if (TAG_END_RECORD.equals(line.trim()) && builder.length() > 0) {
                        List<XmlLogRecord> records = XMLLoggerParser.readFromString(prepareString(builder));
                        logCollection.addLogRecords(records);
                        break;
                    }
                }
            }
            logCollection.finish();

        } catch (IOException | InterruptedException e) {
            // Don't need to log here
        } catch (XMLStreamException e) {
            LogUtil.logError(e);
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(socket);
            IOUtils.closeQuietly(serverSocket);
        }
    }
}
