package com.katalon.plugin.smart_xpath.helpers;

import java.io.File;
import java.nio.file.WatchEvent;

public interface IWatchListener {

    public void call(WatchEvent.Kind<?> eventKind, File file);

}
