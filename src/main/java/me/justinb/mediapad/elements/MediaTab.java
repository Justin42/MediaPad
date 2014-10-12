package me.justinb.mediapad.elements;

import me.justinb.mediapad.exception.UnsupportedFileException;

import java.io.File;

/**
 * Created by Justin Baldwin on 10/10/2014.
 */
public class MediaTab extends FileTab {
    public MediaTab(String name, File file) throws UnsupportedFileException {
        super(name, file);
    }
}
