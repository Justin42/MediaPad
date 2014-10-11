package me.justinb.mediapad.elements;

import javafx.event.EventHandler;
import javafx.scene.control.TreeItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Justin Baldwin on 10/4/2014.
 */
public class FilePathTreeItem extends TreeItem<String> {
    private String fullPath;
    private boolean isDirectory;

    private static EventHandler expandEventHandler = event -> {
        FilePathTreeItem source = (FilePathTreeItem) event.getSource();
        try {
            if(source.getChildren().isEmpty()) {
                Path path = Paths.get(source.getFullPath());
                DirectoryStream<Path> dir = Files.newDirectoryStream(path);
                for(Path p : dir) {
                    source.getChildren().add(new FilePathTreeItem(p));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public FilePathTreeItem(Path file) {
        super();
        fullPath = file.toString();
        if(Files.isDirectory(file)) {
            isDirectory = true;
            setValue(fullPath.substring(fullPath.lastIndexOf(File.separator)+1));
            addEventHandler(TreeItem.<String>branchExpandedEvent(), expandEventHandler);
        }
        else {
            isDirectory = false;
            setValue(fullPath.substring(fullPath.lastIndexOf(File.separator)+1));
        }
    }

    @Override
    public boolean isLeaf() {
        return !isDirectory;
    }

    public String getFullPath() {
        return fullPath;
    }

    public boolean isDirectory() {
        return isDirectory;
    }
}
