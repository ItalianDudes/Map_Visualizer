package it.italiandudes.map_visualizer.master.javafx.controllers;

import it.italiandudes.map_visualizer.master.javafx.components.SceneController;
import it.italiandudes.map_visualizer.master.javafx.controllers.tabs.ControllerSceneTabElements;
import it.italiandudes.map_visualizer.master.javafx.controllers.tabs.ControllerSceneTabMap;
import it.italiandudes.map_visualizer.master.javafx.controllers.tabs.ControllerSceneTabSettings;
import it.italiandudes.map_visualizer.master.javafx.scenes.tabs.SceneTabMap;
import it.italiandudes.map_visualizer.master.javafx.scenes.tabs.SceneTabSettings;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import org.jetbrains.annotations.NotNull;

public final class ControllerSceneMapSheet {

    // Controllers
    private ControllerSceneTabMap controllerTabMap;
    private ControllerSceneTabElements controllerTabElements;
    private ControllerSceneTabSettings controllerTabSettings;

    // Tabs
    @FXML private Tab tabMap;
    @FXML private Tab tabElements;
    @FXML private Tab tabSettings;

    // Initialize
    @FXML
    private void initialize() {
        SceneController map = SceneTabMap.getScene();
        // SceneController elements = SceneTabElements.getScene();
        SceneController settings = SceneTabSettings.getScene();
        controllerTabMap = (ControllerSceneTabMap) map.getController();
        // controllerTabElements = (ControllerSceneTabElements) elements.getController();
        controllerTabSettings = (ControllerSceneTabSettings) settings.getController();
        tabMap.setContent(map.getParent());
        // tabElements.setContent(elements.getParent());
        tabSettings.setContent(settings.getParent());
    }

    // Methods
    @NotNull
    public ControllerSceneTabMap getControllerTabMap() {
        return controllerTabMap;
    }
    @NotNull
    public ControllerSceneTabElements getControllerTabElements() {
        return controllerTabElements;
    }
    @NotNull
    public ControllerSceneTabSettings getControllerTabSettings() {
        return controllerTabSettings;
    }
}
