package it.italiandudes.map_visualizer.master.javafx.controllers;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.master.javafx.Client;
import it.italiandudes.map_visualizer.master.javafx.alerts.ErrorAlert;
import it.italiandudes.map_visualizer.master.javafx.scenes.SceneLoading;
import it.italiandudes.map_visualizer.master.javafx.scenes.SceneMapSheet;
import it.italiandudes.map_visualizer.master.javafx.scenes.SceneSettingsEditor;
import it.italiandudes.map_visualizer.master.utils.DBManager;
import it.italiandudes.map_visualizer.master.utils.KeyParameters;
import it.italiandudes.map_visualizer.master.utils.SheetDataHandler;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public final class ControllerSceneMainMenu {

    // Initialize
    @FXML
    private void initialize() {
        Client.getStage().setResizable(true);
    }

    // EDT
    @FXML
    private void newMap() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Create the Sheet");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DND5E Map", "*." + Defs.Resources.MAP_EXTENSION));
        fileChooser.setInitialDirectory(new File(Defs.JAR_POSITION).getParentFile());
        File fileSheet;
        try {
            fileSheet = fileChooser.showSaveDialog(Client.getStage().getScene().getWindow());
        } catch (IllegalArgumentException e) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileSheet = fileChooser.showSaveDialog(Client.getStage().getScene().getWindow());
        }
        if (fileSheet == null) return;

        Parent thisScene = Client.getScene();
        Client.setScene(SceneLoading.getScene().getParent());
        File finalSheetDB = fileSheet;
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            DBManager.createSheet(finalSheetDB.getAbsolutePath());
                        } catch (SQLException e) {
                            Logger.log(e);
                            Platform.runLater(() -> {
                                Client.setScene(thisScene);
                                new ErrorAlert("ERROR", "I/O Error", "An error has occurred during sheet opening");
                            });
                            return null;
                        }
                        Platform.runLater(() -> Client.setScene(SceneMapSheet.getScene().getParent()));
                        return null;
                    }
                };
            }
        }.start();
    }
    @FXML
    private void openMap() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the Sheet");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DND5E Map", "*." + Defs.Resources.MAP_EXTENSION));
        fileChooser.setInitialDirectory(new File(Defs.JAR_POSITION).getParentFile());
        File fileSheet;
        try {
            fileSheet = fileChooser.showOpenDialog(Client.getStage().getScene().getWindow());
        } catch (IllegalArgumentException e) {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileSheet = fileChooser.showOpenDialog(Client.getStage().getScene().getWindow());
        }
        if (fileSheet == null) return;

        Parent thisScene = Client.getScene();
        Client.setScene(SceneLoading.getScene().getParent());
        File finalSheetDB = fileSheet;
        new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            DBManager.connectToDB(finalSheetDB);
                        } catch (IOException | SQLException e) {
                            Logger.log(e);
                            Platform.runLater(() -> {
                                Client.setScene(thisScene);
                                new ErrorAlert("ERROR", "I/O Error", "An error has occurred during sheet opening");
                            });
                            return null;
                        }

                        String dbVersion = SheetDataHandler.readKeyParameter(KeyParameters.DB_VERSION);

                        if  (dbVersion == null || !dbVersion.equals(Defs.DB_VERSION)) {
                            String sheetVersion = (dbVersion!=null?dbVersion:"NA");
                            String supportedVersion = Defs.DB_VERSION;
                            Platform.runLater(() -> {
                                Client.setScene(thisScene);
                                new ErrorAlert("ERRORE", "Errore della Scheda", "La versione della scheda selezionata non e' supportata.\nVersione Supportata: "+supportedVersion+"\nVersione Scheda: "+sheetVersion);
                            });
                            return null;
                        }

                        Platform.runLater(() -> Client.setScene(SceneMapSheet.getScene().getParent()));
                        return null;
                    }
                };
            }
        }.start();
    }
    @FXML
    private void openSettingsEditor() {
        Client.setScene(SceneSettingsEditor.getScene().getParent());
    }
}