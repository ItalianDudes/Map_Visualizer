package it.italiandudes.map_visualizer.master.javafx.scenes.elements;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.master.javafx.utils.JFXDefs;
import it.italiandudes.map_visualizer.master.javafx.components.SceneController;
import it.italiandudes.map_visualizer.master.javafx.controllers.elements.ControllerSceneElementAddon;
import it.italiandudes.map_visualizer.master.javafx.utils.ThemeHandler;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

public final class SceneElementAddon {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@Nullable final String addonName) throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_ADDON));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementAddon controller = loader.getController();
            if (addonName != null) controller.setAddon(addonName);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
    @NotNull
    public static SceneController getScene(@NotNull final JSONObject addonStructure) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_ADDON));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementAddon controller = loader.getController();
            controller.setAddonStructure(addonStructure);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
}
