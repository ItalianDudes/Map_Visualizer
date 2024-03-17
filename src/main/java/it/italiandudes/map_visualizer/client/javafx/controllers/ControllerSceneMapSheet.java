package it.italiandudes.map_visualizer.client.javafx.controllers;

import it.italiandudes.map_visualizer.client.javafx.components.SceneController;
import it.italiandudes.map_visualizer.client.javafx.controllers.tabs.ControllerSceneTabMap;
import it.italiandudes.map_visualizer.client.javafx.scenes.tabs.SceneTabMap;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import org.jetbrains.annotations.NotNull;

public final class ControllerSceneMapSheet {

    // Controllers
    private ControllerSceneTabMap controllerTabMap;

    // Tabs
    @FXML private Tab tabMap;

    // Initialize
    @FXML
    private void initialize() {
        SceneController map = SceneTabMap.getScene();
        controllerTabMap = (ControllerSceneTabMap) map.getController();
        tabMap.setContent(map.getParent());
    }

    // Methods
    @NotNull
    public ControllerSceneTabMap getControllerTabMap() {
        return controllerTabMap;
    }
}
