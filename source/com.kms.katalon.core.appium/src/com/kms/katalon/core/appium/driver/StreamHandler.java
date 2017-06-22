package com.kms.katalon.core.appium.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class StreamHandler extends Thread {
    private InputStream is;

    private List<OutputStream> oses;

    private StreamHandler(InputStream is, List<OutputStream> oses) {
        this.is = is;
        this.oses = oses;
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
        try {
            Iterator<OutputStream> iterator = oses.iterator();
            while (iterator.hasNext()) {
                OutputStream os = iterator.next();

                os.write(line.getBytes());
                os.write(System.lineSeparator().getBytes());
            }
        } catch (IOException ignored) {}
    }

    public static StreamHandler create(InputStream is, List<OutputStream> oses) {
        return new StreamHandler(is, oses);
    }

}
