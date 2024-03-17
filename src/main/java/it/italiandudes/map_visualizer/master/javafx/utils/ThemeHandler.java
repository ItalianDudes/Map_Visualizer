package it.italiandudes.map_visualizer.master.javafx.utils;

import it.italiandudes.map_visualizer.master.utils.Settings;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.scene.Parent;
import org.jetbrains.annotations.NotNull;

public final class ThemeHandler {

    // Config Theme
    private static String configTheme = null;

    // Methods
    public static void setConfigTheme() {
        if (Settings.getSettings().getBoolean(Defs.SettingsKeys.ENABLE_DARK_MODE)) {
            configTheme = Defs.Resources.get(JFXDefs.Resources.CSS.CSS_DARK_THEME).toExternalForm();
        } else {
            configTheme = Defs.Resources.get(JFXDefs.Resources.CSS.CSS_LIGHT_THEME).toExternalForm();
        }
    }

    // Config Theme
    public static void loadConfigTheme(@NotNull final Parent scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(configTheme);
    }

    // Light Theme
    public static void loadLightTheme(@NotNull final Parent scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Defs.Resources.get(JFXDefs.Resources.CSS.CSS_LIGHT_THEME).toExternalForm());
    }

    // Dark Theme
    public static void loadDarkTheme(@NotNull final Parent scene) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(Defs.Resources.get(JFXDefs.Resources.CSS.CSS_DARK_THEME).toExternalForm());
    }
}