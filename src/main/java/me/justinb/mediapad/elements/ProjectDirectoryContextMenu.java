package me.justinb.mediapad.elements;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeView;
import me.justinb.mediapad.controller.ControllerHandler;
import me.justinb.mediapad.controller.OverviewController;

/**
 * Created by Justin Baldwin on 10/4/2014.
 */
public class ProjectDirectoryContextMenu extends ContextMenu {
    private static ProjectDirectoryContextMenu instance;
    private TreeView<String> projectTree;
    private FilePathTreeItem selectedItem;

    public static ProjectDirectoryContextMenu getInstance() {
        if(instance == null) instance = new ProjectDirectoryContextMenu();
        return instance;
    }

    private ProjectDirectoryContextMenu() {
        OverviewController overviewController = ControllerHandler.getInstance().get(OverviewController.class);
        projectTree = overviewController.getProjectTree();

        MenuItem item;
        Menu newMenu = new Menu("New");
        item = new MenuItem("Folder");
        item.setOnAction(event -> overviewController.handleNewFolder());
        newMenu.getItems().add(item);


        Menu newNoteMenu = new Menu("Note");

        item = new MenuItem("Text");
        item.setOnAction(event -> overviewController.handleNewTextNote());
        newNoteMenu.getItems().add(item);

        item = new MenuItem("Audio");
        item.setOnAction(event -> overviewController.handleNewAudioNote());
        newNoteMenu.getItems().add(item);

        item = new MenuItem("Image");
        item.setOnAction(event -> overviewController.handleNewImageNote());
        newNoteMenu.getItems().add(item);

        item = new MenuItem("Video");
        item.setOnAction(event -> overviewController.handleNewVideoNote());
        newNoteMenu.getItems().add(item);

        newMenu.getItems().add(newNoteMenu);
        getItems().add(newMenu);
    }

    public void update() {
        selectedItem = (FilePathTreeItem) projectTree.getSelectionModel().getSelectedItem();
        if(selectedItem == null) hide();
    }
}
