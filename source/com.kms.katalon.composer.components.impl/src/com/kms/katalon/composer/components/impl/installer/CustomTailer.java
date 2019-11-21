package com.kms.katalon.composer.components.impl.installer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;

import com.kms.katalon.composer.components.log.LoggerSingleton;

public class CustomTailer extends Tailer {

    private static final int DEFAULT_DELAY_MILLIS = 1000;

    private static final String RAF_MODE = "r";

    private static final int DEFAULT_BUFSIZE = 4096;

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private final byte inbuf[];

    private final Charset charset;

    private final boolean end;

    private final TailerListener listener;

    private final boolean reOpen;

    public CustomTailer(final File file, final TailerListener listener) {
        this(file, listener, DEFAULT_DELAY_MILLIS);
    }

    public CustomTailer(final File file, final TailerListener listener, final long delayMillis) {
        this(file, listener, delayMillis, false);
    }

    public CustomTailer(final File file, final TailerListener listener, final long delayMillis, final boolean end) {
        this(file, listener, delayMillis, end, DEFAULT_BUFSIZE);
    }

    public CustomTailer(final File file, final TailerListener listener, final long delayMillis, final boolean end,
            final boolean reOpen) {
        this(file, listener, delayMillis, end, reOpen, DEFAULT_BUFSIZE);
    }

    public CustomTailer(final File file, final TailerListener listener, final long delayMillis, final boolean end,
            final int bufSize) {
        this(file, listener, delayMillis, end, false, bufSize);
    }

    public CustomTailer(final File file, final TailerListener listener, final long delayMillis, final boolean end,
            final boolean reOpen, final int bufSize) {
        this(file, DEFAULT_CHARSET, listener, delayMillis, end, reOpen, bufSize);
    }

    public CustomTailer(final File file, final Charset charset, final TailerListener listener, final long delayMillis,
            final boolean end, final boolean reOpen, final int bufSize) {
        super(file, listener, delayMillis, end, bufSize);
        this.listener = listener;
        listener.init(this);

        this.end = end;
        this.reOpen = reOpen;
        this.inbuf = new byte[bufSize];
        this.charset = charset;
    }

    public static Tailer create(final File file, final TailerListener listener, final long delayMillis,
            final boolean end, final int bufSize) {
        return create(file, listener, delayMillis, end, false, bufSize);
    }

    public static Tailer create(final File file, final TailerListener listener, final long delayMillis,
            final boolean end, final boolean reOpen, final int bufSize) {
        return create(file, DEFAULT_CHARSET, listener, delayMillis, end, reOpen, bufSize);
    }

    public static Tailer create(final File file, final Charset charset, final TailerListener listener,
            final long delayMillis, final boolean end, final boolean reOpen, final int bufSize) {
        final Tailer tailer = new CustomTailer(file, charset, listener, delayMillis, end, reOpen, bufSize);
        final Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }

    public static Tailer create(final File file, final TailerListener listener, final long delayMillis,
            final boolean end) {
        return create(file, listener, delayMillis, end, DEFAULT_BUFSIZE);
    }

    public static Tailer create(final File file, final TailerListener listener, final long delayMillis,
            final boolean end, final boolean reOpen) {
        return create(file, listener, delayMillis, end, reOpen, DEFAULT_BUFSIZE);
    }

    public static Tailer create(final File file, final TailerListener listener, final long delayMillis) {
        return create(file, listener, delayMillis, false);
    }

    public static Tailer create(final File file, final TailerListener listener) {
        return create(file, listener, DEFAULT_DELAY_MILLIS, false);
    }

    private long position = 0L;

    private long lastModified = 0L;

    private RandomAccessFile reader = null;

    private boolean getRun() {
        return !Thread.currentThread().isInterrupted();
    }

    @Override
    public void run() {
        File file = getFile();

        try {
            openFile();
            if (end) {
                position = file.length();
                seek(position);
            }

            while (getRun()) {
                if (reader == null) {
                    openFile();
                }

                final boolean isModified = FileUtils.isFileNewer(file, lastModified);
                final long length = file.length();
                LoggerSingleton.logInfo(isModified + " - " + position + "/" + length + " : " + file.getName());

                if (position > length) {
                    listener.fileRotated();
                    try (RandomAccessFile save = reader) {
                        seek(position);
                        checkFile(save);
                        position = 0;
                        reOpenFile();
                    } catch (final FileNotFoundException e) {
                        listener.fileNotFound();
                        Thread.sleep(getDelay());
                    }
                    continue;
                }

                if (position < length) {
                    seek(position);
                    checkFile(reader);
                    lastModified = file.lastModified();
                } else if (isModified) {
                    if (length == 0) {
                        position = 0;
                    }
                    seek(position);
                    checkFile(reader);
                    lastModified = file.lastModified();
                    LoggerSingleton.logInfo("New File or File Truncated");
                }

                if (reOpen) {
                    closeFile();
                }
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
                // position = end ? file.length() : 0;
                // lastModified = file.lastModified();
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

    private void checkFile(final RandomAccessFile reader) throws InterruptedException, IOException {
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
                            listener.handle(new String(lineBuf.toByteArray(), charset));
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
                                listener.handle(new String(lineBuf.toByteArray(), charset));
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

            position = reader.getFilePointer();
        }
    }
}
