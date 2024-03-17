package it.italiandudes.map_visualizer.client.javafx.scenes.elements;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.client.javafx.JFXDefs;
import it.italiandudes.map_visualizer.client.javafx.components.SceneController;
import it.italiandudes.map_visualizer.client.javafx.controllers.elements.ControllerSceneElementItem;
import it.italiandudes.map_visualizer.client.javafx.utils.ThemeHandler;
import it.italiandudes.map_visualizer.data.elements.Item;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

public final class SceneElementItem {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final Item item) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_ITEM));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementItem controller = loader.getController();
            controller.setItem(item);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
    @NotNull
    public static SceneController getScene(@NotNull final JSONObject itemStructure) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_ITEM));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementItem controller = loader.getController();
            controller.setItemStructure(itemStructure);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
}
