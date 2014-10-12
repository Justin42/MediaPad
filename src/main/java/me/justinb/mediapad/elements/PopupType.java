package me.justinb.mediapad.elements;

/**
 * Created by Justin Baldwin on 10/12/2014.
 */
public enum PopupType {
    ERROR("Error");

    private String title;
    PopupType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
