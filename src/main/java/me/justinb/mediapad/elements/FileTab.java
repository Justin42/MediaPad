package me.justinb.mediapad.elements;

import javafx.scene.control.Tab;

import java.io.File;

/**
 * Created by Justin Baldwin on 10/5/2014.
 */
public abstract class FileTab extends Tab {
    protected String filePath;

    public FileTab(String name, final File file) {
        super(name);
        this.filePath = file.getPath();
    }

    public String getFilePath() {
        return filePath;
    }
}
