package me.justinb.mediapad.controller;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Justin Baldwin on 10/4/2014.
 */
public class ControllerHandler {
    private static final Logger log = Logger.getLogger(ControllerHandler.class.getName());
    private static ControllerHandler instance;
    private HashMap<Class, Controller> controllerMap;

    public static ControllerHandler getInstance() {
        if(instance == null) instance = new ControllerHandler();
        return instance;
    }

    private ControllerHandler() {
        controllerMap = new HashMap<>();
    }

    public void register(Controller controller) {
        if(controllerMap.containsKey(controller.getClass())) {
            log.warning("An instance of that controller is already registered.");
        }
        controllerMap.put(controller.getClass(), controller);
        log.log(Level.INFO, "Registered controller instance " + controller.getClass().getName());
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> controllerClass) {
        T controller = (T)controllerMap.get(controllerClass);
        if(controller == null) {
            log.warning("Could not retrieve controller instance " + controllerClass.getName());
        }
        return controller;
    }
}
