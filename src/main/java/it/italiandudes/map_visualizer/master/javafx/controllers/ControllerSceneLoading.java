package it.italiandudes.map_visualizer.master.javafx.controllers;

import it.italiandudes.map_visualizer.master.javafx.Client;
import javafx.fxml.FXML;

public final class ControllerSceneLoading {

    //Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
    }
}