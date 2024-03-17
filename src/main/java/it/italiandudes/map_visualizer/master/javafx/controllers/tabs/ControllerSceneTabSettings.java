package it.italiandudes.map_visualizer.master.javafx.controllers.tabs;

import it.italiandudes.map_visualizer.master.javafx.Client;
import it.italiandudes.map_visualizer.master.javafx.alerts.ConfirmationAlert;
import it.italiandudes.map_visualizer.master.javafx.scenes.SceneLoading;
import it.italiandudes.map_visualizer.master.javafx.scenes.SceneMainMenu;
import it.italiandudes.map_visualizer.master.utils.DBManager;
import javafx.fxml.FXML;

public final class ControllerSceneTabSettings {

    // EDT
    @FXML
    private void backToMenu() {
        if(!new ConfirmationAlert("MENU", "Tornare al Menu", "Vuoi davvero tornare al menu principale?\nRicorda: Map Visualizer salva costantemente quindi i tuoi progressi sono al sicuro!").result) return;
        Client.setScene(SceneLoading.getScene().getParent());
        DBManager.closeConnection();
        Client.setScene(SceneMainMenu.getScene().getParent());
    }
    @FXML
    private void quit() {
        if(!new ConfirmationAlert("CHIUSURA", "Chiusura Applicazione", "Vuoi davvero chiudere l'applicazione?\nRicorda: Map Visualizer salva costantemente quindi i tuoi progressi sono al sicuro!").result) return;
        Client.setScene(SceneLoading.getScene().getParent());
        DBManager.closeConnection();
        System.exit(0);
    }
}
