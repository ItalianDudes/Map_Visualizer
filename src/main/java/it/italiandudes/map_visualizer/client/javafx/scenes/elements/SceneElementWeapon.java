package it.italiandudes.map_visualizer.client.javafx.scenes.elements;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.client.javafx.JFXDefs;
import it.italiandudes.map_visualizer.client.javafx.components.SceneController;
import it.italiandudes.map_visualizer.client.javafx.controllers.elements.ControllerSceneElementWeapon;
import it.italiandudes.map_visualizer.client.javafx.utils.ThemeHandler;
import it.italiandudes.map_visualizer.data.elements.Weapon;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

public final class SceneElementWeapon {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final Weapon weapon) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_WEAPON));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementWeapon controller = loader.getController();
            controller.setWeapon(weapon);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
    @NotNull
    public static SceneController getScene(@NotNull final JSONObject weaponStructure) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_WEAPON));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementWeapon controller = loader.getController();
            controller.setWeaponStructure(weaponStructure);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
}
