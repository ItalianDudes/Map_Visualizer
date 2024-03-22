package it.italiandudes.map_visualizer.master.javafx.scenes.elements;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.master.javafx.components.Waypoint;
import it.italiandudes.map_visualizer.master.javafx.utils.JFXDefs;
import it.italiandudes.map_visualizer.master.javafx.components.SceneController;
import it.italiandudes.map_visualizer.master.javafx.controllers.elements.ControllerSceneElementWeapon;
import it.italiandudes.map_visualizer.master.javafx.utils.ThemeHandler;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.SQLException;

public final class SceneElementWeapon {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final Waypoint waypoint) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_WEAPON));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementWeapon controller = loader.getController();
            controller.setWaypoint(waypoint);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
    @NotNull
    public static SceneController getScene(@Nullable final String weaponName) throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_WEAPON));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementWeapon controller = loader.getController();
            if (weaponName != null) controller.setWeapon(weaponName);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
}
