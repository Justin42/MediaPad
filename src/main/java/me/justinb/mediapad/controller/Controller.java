package me.justinb.mediapad.controller;

import me.justinb.mediapad.MediaPad;

/**
 * Created by Justin Baldwin on 10/4/2014.
 */
public abstract class Controller {
    protected MediaPad mediaPad;
    protected ControllerHandler controllerHandler = ControllerHandler.getInstance();

    public Controller() {
        this.mediaPad = MediaPad.getInstance();
        ControllerHandler.getInstance().register(this);
    }
}
