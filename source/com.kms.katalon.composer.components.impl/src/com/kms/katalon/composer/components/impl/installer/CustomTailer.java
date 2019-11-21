package com.kms.katalon.composer.components.impl.installer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class CustomTailer extends Tailer {
    private TailerListener listener;

    private static final String RAF_MODE = "r";

    private final byte inbuf[];

    private static final int DEFAULT_BUFSIZE = 4096;

    private final boolean end = true;

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    public CustomTailer(File file, TailerListener listener) {
        super(file, listener);
        this.listener = listener;
        this.inbuf = new byte[DEFAULT_BUFSIZE];
    }

    public CustomTailer(File file, TailerListener listener, long delayMillis) {
        super(file, listener, delayMillis);
        this.listener = listener;
        this.inbuf = new byte[DEFAULT_BUFSIZE];
    }

    public CustomTailer(File file, TailerListener listener, long delayMillis, final boolean end) {
        super(file, listener, delayMillis, end);
        this.listener = listener;
        this.inbuf = new byte[DEFAULT_BUFSIZE];
    }

    public CustomTailer(File file, TailerListener listener, long delayMillis, final boolean end, boolean reOpen) {
        super(file, listener, delayMillis, end, reOpen);
        this.listener = listener;
        this.inbuf = new byte[DEFAULT_BUFSIZE];
    }

    public CustomTailer(File file, TailerListener listener, long delayMillis, final boolean end, boolean reOpen,
            int bufSize) {
        super(file, listener, delayMillis, end, reOpen, bufSize);
        this.listener = listener;
        this.inbuf = new byte[DEFAULT_BUFSIZE];
    }

    public CustomTailer(File file, TailerListener listener, long delayMillis, final boolean end, int bufSize) {
        super(file, listener, delayMillis, end, bufSize);
        this.listener = listener;
        this.inbuf = new byte[DEFAULT_BUFSIZE];
    }

    private long position = 0L;

    private long lastModified;

    private RandomAccessFile reader = null;

    private boolean getRun() {
        return !Thread.currentThread().isInterrupted();
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void run() {
        File file = getFile();

        try {
            WatchService watcher;
            watcher = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(file.getParent());
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

            while (getRun()) {
                final boolean isNewer = FileUtils.isFileNewer(file, lastModified);
                final long length = file.length();

                if (reader == null) {
                    openFile();
                }

                if (length < position) {
                    listener.fileRotated();
                    try (RandomAccessFile save = reader) {
//                        checkFile(save);
                        seek(position);
                        position = readLines(save);
                        position = 0;
                        reOpenFile();
                    } catch (final FileNotFoundException e) {
                        listener.fileNotFound();
                        Thread.sleep(getDelay());
                    }
                    continue;
                }

                if (length > position) {
//                    checkFile(reader);
                    seek(position);
                    position = readLines(reader);
                    lastModified = file.lastModified();
                } else if (isNewer) {
                    position = 0;
                    // checkFile(reader);
                    seek(position);
                    position = readLines(reader);
                    lastModified = file.lastModified();
                }

//                WatchKey key = watcher.take();
//                for (WatchEvent<?> event : key.pollEvents()) {
//                    WatchEvent.Kind<?> kind = event.kind();
//                    if (kind == OVERFLOW) {
//                        continue;
//                    }
//                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
//                    Path filename = ev.context();
//                    if (kind == ENTRY_MODIFY && filename.toFile().getName().equals(file.getName())) {
//                        position = readLines(reader);
//                        lastModified = file.lastModified();
//                    }
//                }
//
//                boolean valid = key.reset();
//                if (!valid) {
//                    break;
//                }

//                closeFile();
                try {
                    Thread.sleep(getDelay());
                } catch (InterruptedException error) {
                    break;
                }
            }
        } catch (InterruptedException | IOException error) {
            LoggerSingleton.logError(error);
            listener.handle(error);
            Thread.currentThread().interrupt();
        } finally {
            closeFile();
        }
    }

    private void openFile() throws InterruptedException {
        File file = getFile();

        while (getRun() && reader == null) {
            try {
                reader = new RandomAccessFile(file, RAF_MODE);
            } catch (final FileNotFoundException e) {
                listener.fileNotFound();
            }

            if (reader == null) {
                Thread.sleep(getDelay());
            } else {
                position = end ? file.length() : 0;
                lastModified = file.lastModified();
            }
        }
    }

    private void closeFile() {
        if (reader != null) {
            try {
                reader.close();
                reader = null;
            } catch (IOException error) {
                listener.handle(error);
            }
        }
    }
    
    private void reOpenFile() throws InterruptedException {
        closeFile();
        openFile();
    }

    private void seek(long position) throws InterruptedException {
        while (getRun()) {
            try {
                reader.seek(position);
                break;
            } catch (IOException e) {
                reOpenFile();
            }
        }
    }

    private void checkFile(RandomAccessFile reader) throws InterruptedException {
        seek(position);
        BufferedReader buffer;
        try {
            buffer = new BufferedReader(new FileReader(reader.getFD()));
            buffer.lines().forEachOrdered(line -> {
                listener.handle(line);
                try {
                    position = reader.getFilePointer();
                } catch (IOException e) {
                    //
                }
            });
            buffer.close();
        } catch (IOException e1) {
            if (getRun()) {
                reOpenFile();
                checkFile(reader);
            }
        }
    }

    private long readLines(final RandomAccessFile reader) throws IOException, InterruptedException {
        try (ByteArrayOutputStream lineBuf = new ByteArrayOutputStream(64)) {
            long pos = reader.getFilePointer();
            long rePos = pos; // position to re-read
            int num;
            boolean seenCR = false;
            while ((num = reader.read(inbuf)) != -1) {
                for (int i = 0; i < num; i++) {
                    final byte ch = inbuf[i];
                    switch (ch) {
                        case '\n':
                            seenCR = false; // swallow CR before LF
                            listener.handle(new String(lineBuf.toByteArray(), DEFAULT_CHARSET));
                            lineBuf.reset();
                            rePos = pos + i + 1;
                            break;
                        case '\r':
                            if (seenCR) {
                                lineBuf.write('\r');
                            }
                            seenCR = true;
                            break;
                        default:
                            if (seenCR) {
                                seenCR = false; // swallow final CR
                                listener.handle(new String(lineBuf.toByteArray(), DEFAULT_CHARSET));
                                lineBuf.reset();
                                rePos = pos + i + 1;
                            }
                            lineBuf.write(ch);
                    }
                }
                pos = reader.getFilePointer();
            }

            reader.seek(rePos); // Ensure we can re-read if necessary

            // if (listener instanceof TailerListenerAdapter) {
            // ((TailerListenerAdapter) listener).endOfFileReached();
            // }

            return reader.getFilePointer();
        }
    }
}
