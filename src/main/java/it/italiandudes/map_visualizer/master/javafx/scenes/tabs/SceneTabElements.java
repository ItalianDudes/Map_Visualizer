package it.italiandudes.map_visualizer.master.javafx.scenes.tabs;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.master.javafx.controllers.ControllerSceneMapSheet;
import it.italiandudes.map_visualizer.master.javafx.utils.JFXDefs;
import it.italiandudes.map_visualizer.master.javafx.components.SceneController;
import it.italiandudes.map_visualizer.master.javafx.controllers.tabs.ControllerSceneTabElements;
import it.italiandudes.map_visualizer.master.javafx.utils.ThemeHandler;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class SceneTabElements {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final ControllerSceneMapSheet controllerSceneMapSheet) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Tabs.FXML_TAB_ELEMENTS));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneTabElements controller = loader.getController();
            controller.setControllerSceneMapSheet(controllerSceneMapSheet);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
}
