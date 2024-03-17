package it.italiandudes.map_visualizer.master.javafx.scenes.elements;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.master.javafx.utils.JFXDefs;
import it.italiandudes.map_visualizer.master.javafx.components.SceneController;
import it.italiandudes.map_visualizer.master.javafx.controllers.elements.ControllerSceneElementArmor;
import it.italiandudes.map_visualizer.master.javafx.utils.ThemeHandler;
import it.italiandudes.map_visualizer.data.elements.Armor;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

public final class SceneElementArmor {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final Armor armor) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_ARMOR));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementArmor controller = loader.getController();
            controller.setArmor(armor);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
    @NotNull
    public static SceneController getScene(@NotNull final JSONObject armorStructure) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_ARMOR));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementArmor controller = loader.getController();
            controller.setArmorStructure(armorStructure);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
}
