package it.italiandudes.map_visualizer.client.javafx.controllers;

import it.italiandudes.map_visualizer.client.javafx.Client;
import it.italiandudes.map_visualizer.client.javafx.scenes.SceneSettingsEditor;
import javafx.fxml.FXML;

public final class ControllerSceneMainMenu {

    // Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
    }

    // EDT
    @FXML
    private void newMap() {}
    @FXML
    private void openMap() {}
    @FXML
    private void openSettingsEditor() {
        Client.setScene(SceneSettingsEditor.getScene().getParent());
    }
}