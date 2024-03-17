package it.italiandudes.map_visualizer.client.javafx.controllers;

import it.italiandudes.map_visualizer.client.javafx.Client;
import javafx.fxml.FXML;

public final class ControllerSceneLoading {

    //Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
    }
}