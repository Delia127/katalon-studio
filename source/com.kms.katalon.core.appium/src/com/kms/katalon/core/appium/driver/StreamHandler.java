package com.kms.katalon.core.appium.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

public class StreamHandler extends Thread {
    private InputStream is;

    private List<PrintStream> printStreams;

    private StreamHandler(InputStream is, List<PrintStream> printStreams) {
        this.is = is;
        this.printStreams = printStreams;
    }

    public void run() {
        try (InputStreamReader isr = new InputStreamReader(is); 
                BufferedReader br = new BufferedReader(isr);) {
            String line = null;
            while ((line = br.readLine()) != null) {
                println(line);
            }
        } catch (IOException e) {
            // Stream closed
        }
    }

    private void println(String line) {
        Iterator<PrintStream> iterator = printStreams.iterator();
        while (iterator.hasNext()) {
            iterator.next().println(line);
        }
    }

    public static StreamHandler create(InputStream is, List<PrintStream> printStreams) {
        return new StreamHandler(is, printStreams);
    }

}
