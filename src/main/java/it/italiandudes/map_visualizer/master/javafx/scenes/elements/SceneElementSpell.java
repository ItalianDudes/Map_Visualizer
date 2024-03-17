package it.italiandudes.map_visualizer.master.javafx.scenes.elements;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.master.javafx.utils.JFXDefs;
import it.italiandudes.map_visualizer.master.javafx.components.SceneController;
import it.italiandudes.map_visualizer.master.javafx.controllers.elements.ControllerSceneElementSpell;
import it.italiandudes.map_visualizer.master.javafx.utils.ThemeHandler;
import it.italiandudes.map_visualizer.data.elements.Spell;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

public final class SceneElementSpell {

    // Scene Generator
    @NotNull
    public static SceneController getScene(@NotNull final Spell spell) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_SPELL));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementSpell controller = loader.getController();
            controller.setSpell(spell);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
    @NotNull
    public static SceneController getScene(@NotNull final JSONObject spellStructure) {
        try {
            FXMLLoader loader = new FXMLLoader(Defs.Resources.get(JFXDefs.Resources.FXML.Elements.FXML_ELEMENT_SPELL));
            Parent root = loader.load();
            ThemeHandler.loadConfigTheme(root);
            ControllerSceneElementSpell controller = loader.getController();
            controller.setSpellStructure(spellStructure);
            controller.configurationComplete();
            return new SceneController(root, controller);
        } catch (IOException e) {
            Logger.log(e);
            System.exit(-1);
            return null;
        }
    }
}
