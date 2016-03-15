package com.kms.katalon.execution.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class LaunchOutputStreamHandler extends Thread implements IOutputStream{
    private InputStream is;
    private OutputStream os;

    public LaunchOutputStreamHandler(InputStream is) {
        this(is, null);
    }

    public LaunchOutputStreamHandler(InputStream is, OutputStream redirect) {
        this.is = is;
        this.os = redirect;
    }

    public void run() {
        try {
            PrintWriter pw = null;
            if (os != null) {
                pw = new PrintWriter(os);
            }
            
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (pw != null) {
                    pw.println(line);
                    pw.flush();
                }
            }
            
            if (pw != null) {
                pw.flush();
            }
                
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public synchronized void println(String line) {
        PrintWriter pw = null;
        if (os != null) {
            pw = new PrintWriter(os);
        }
        pw.println(line);
        pw.flush();
    }

    @Override
    public void close() throws IOException {
    }
}
