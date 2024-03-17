package it.italiandudes.map_visualizer.utils;

import it.italiandudes.map_visualizer.Map_Visualizer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

public final class Defs {

    // App File Name
    public static final String APP_FILE_NAME = "Map_Visualizer";

    // Charset
    public static final String DEFAULT_CHARSET = "UTF-8";

    // Jar App Position
    public static final String JAR_POSITION;
    static {
        try {
            JAR_POSITION = new File(Map_Visualizer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // JSON Settings
    public static final class SettingsKeys {
        public static final String ENABLE_DARK_MODE = "enableDarkMode";
    }

    // Resources Location
    public static final class Resources {

        //Resource Getters
        public static URL get(@NotNull final String resourceConst) {
            return Objects.requireNonNull(Map_Visualizer.class.getResource(resourceConst));
        }
        public static InputStream getAsStream(@NotNull final String resourceConst) {
            return Objects.requireNonNull(Map_Visualizer.class.getResourceAsStream(resourceConst));
        }

        // JSON
        public static final class JSON {
            public static final String JSON_SETTINGS = "settings.json";
            public static final String DEFAULT_JSON_SETTINGS = "/json/" + JSON_SETTINGS;
        }

        // Images
        public static final class Image {
            private static final String IMAGE_DIR = "/image/";
            public static final String IMAGE_DARK_MODE = IMAGE_DIR + "dark_mode.png";
            public static final String IMAGE_LIGHT_MODE = IMAGE_DIR + "light_mode.png";
        }
    }
}
