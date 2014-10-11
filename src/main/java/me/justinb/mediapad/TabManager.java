package me.justinb.mediapad;

import me.justinb.mediapad.elements.AudioTab;
import me.justinb.mediapad.elements.FileTab;
import me.justinb.mediapad.exception.UnsupportedFileException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Justin Baldwin on 10/11/2014.
 */
public class TabManager {
    public static TabManager instance;

    public static FileTab createTab(String name, File file) throws UnsupportedFileException, IOException {
        FileTab fileTab = null;
        for(String format : AudioTab.getSupportedFormats()) {
            if(file.getName().endsWith(format)) {
                fileTab = new AudioTab(name, file);
            }
        }
        if(fileTab == null) throw new UnsupportedFileException(file.getName() + " format not supported.");
        else return fileTab;
    }
}
