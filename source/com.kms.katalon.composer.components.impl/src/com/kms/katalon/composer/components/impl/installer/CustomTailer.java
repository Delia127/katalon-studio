package com.kms.katalon.composer.components.impl.installer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class CustomTailer extends Tailer {
    private TailerListener listener;
    
    public CustomTailer(File file, TailerListener listener) {
        super(file, listener);
        this.listener = listener;
    }

    public CustomTailer(File file, TailerListener listener, long delayMillis) {
        super(file, listener, delayMillis);
        this.listener = listener;
    }

    public CustomTailer(File file, TailerListener listener, long delayMillis, boolean end) {
        super(file, listener, delayMillis, end);
        this.listener = listener;
    }

    public CustomTailer(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen) {
        super(file, listener, delayMillis, end, reOpen);
        this.listener = listener;
    }

    public CustomTailer(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen,
            int bufSize) {
        super(file, listener, delayMillis, end, reOpen, bufSize);
        this.listener = listener;
    }

    public CustomTailer(File file, TailerListener listener, long delayMillis, boolean end, int bufSize) {
        super(file, listener, delayMillis, end, bufSize);
        this.listener = listener;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void run() {
        File file = getFile();
        WatchService watcher;
        RandomAccessFile reader = null;
        Long[] latestPos = { 0L };
        try {
            watcher = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(file.getParent());
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            while (true) {
                WatchKey key = watcher.take();

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == OVERFLOW) {
                        continue;
                    }
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();
                    if (kind == ENTRY_MODIFY && filename.toFile().getName().equals(file.getName())) {
                        LoggerSingleton.logDebug(filename.getFileName().toString());
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        reader = new RandomAccessFile(file, "r");
                        reader.seek(latestPos[0]);
                        BufferedReader buffer = new BufferedReader(new FileReader(reader.getFD()));
                        reader.getChannel().position();
                        String line;
                        while ((line = buffer.readLine()) != null) {
                            latestPos[0] = reader.getFilePointer();
                            listener.handle(line);
                        }
                        buffer.close();
                        reader.close();
                        reader = null;
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException | InterruptedException error) {
            try {
                reader.close();
            } catch (IOException e) {
                //
            }
            LoggerSingleton.logError(error);
        }
    }
}
