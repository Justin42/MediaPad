package me.justinb.mediapad.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.DirectoryChooser;
import me.justinb.mediapad.MediaPad;
import me.justinb.mediapad.TabManager;
import me.justinb.mediapad.elements.FilePathTreeItem;
import me.justinb.mediapad.elements.FileTab;
import me.justinb.mediapad.elements.ProjectDirectoryContextMenu;
import me.justinb.mediapad.exception.UnsupportedFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Justin Baldwin on 10/3/2014.
 */
public class OverviewController extends Controller {
    @FXML
    private TreeView<String> projectTree;

    @FXML
    private TabPane mediaTabPane;

    @FXML
    private void initialize() {
        projectTree.setRoot(new FilePathTreeItem(Paths.get("C:\\Users\\Owner\\Desktop\\MediaPad")));
        projectTree.setContextMenu(ProjectDirectoryContextMenu.getInstance());

        // Context menu event
        projectTree.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED,
                event -> ProjectDirectoryContextMenu.getInstance().update());

        // Double click event
        projectTree.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2) {
                openMediaInNewTab((FilePathTreeItem)projectTree.getSelectionModel().getSelectedItem());
            }
        });
    }

    /**
     * Open a file dialog to select the project path
     */
    @FXML
    public void handleOpenProjectFolder() {
        File path = new DirectoryChooser().showDialog(null);
        if(path != null) {
            projectTree.setRoot(new FilePathTreeItem(Paths.get(path.getAbsolutePath())));
        }
    }

    @FXML
    private void handleExit() {
        MediaPad.getInstance().exit();
    }

    public TreeView<String> getProjectTree() {
        return projectTree;
    }

    public void handleNewFolder() {

    }

    public void handleNewImageNote(){}

    public void handleNewAudioNote(){

    }

    public void handleNewTextNote(){}

    public void handleNewVideoNote(){}

    public void handleDisplaySelection(){}

    public void openMediaInNewTab(FilePathTreeItem treeItem) {
        String filePath = treeItem.getFullPath();
        // Focus if already open
        for(Tab tab : mediaTabPane.getTabs()) {
            if(tab instanceof FileTab) {
                FileTab fileTab = (FileTab) tab;
                if(fileTab.getFilePath().equals(filePath)) {
                    mediaTabPane.getSelectionModel().select(tab);
                    return;
                }
            }
        }
        try {
            if (!treeItem.isDirectory()) {
                mediaTabPane.getTabs().add(TabManager.createTab(treeItem.getValue(), new File(treeItem.getFullPath())));
            }
        } catch (UnsupportedFileException | IOException e) {
            e.printStackTrace();
        }
    }
}
