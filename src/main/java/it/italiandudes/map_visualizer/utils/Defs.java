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

    // DB Versions
    public static final String DB_VERSION = "1.0";

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

        // Element Extension
        public static final String ELEMENT_EXTENSION = "dnd5e.element";

        // Map Extension
        public static final String MAP_EXTENSION = "dnd5e.map";

        // JSON
        public static final class JSON {
            public static final String JSON_SETTINGS = "settings.json";
            public static final String DEFAULT_JSON_SETTINGS = "/json/" + JSON_SETTINGS;
        }

        // SQL
        public static final class SQL {
            private static final String SQL_DIR = "/sql/";
            public static final String SQL_MAP_DATA = SQL_DIR + "map_data.sql";
            public static String[] SUPPORTED_IMAGE_EXTENSIONS = {"*.png", "*.jpg", "*.jpeg"};
        }

        // SVG
        public static final class SVG {
            private static final String SVG_DIR = "/svg/";
            public static final String SVG_ADDON = SVG_DIR + "addon.svg";
            public static final String SVG_ARMOR = SVG_DIR + "armor.svg";
            public static final String SVG_ITEM = SVG_DIR + "item.svg";
            public static final String SVG_SPELL = SVG_DIR + "spell.svg";
            public static final String SVG_WEAPON = SVG_DIR + "weapon.svg";
        }

        // Images
        public static final class Image {
            private static final String IMAGE_DIR = "/image/";
            public static final String IMAGE_DARK_MODE = IMAGE_DIR + "dark_mode.png";
            public static final String IMAGE_LIGHT_MODE = IMAGE_DIR + "light_mode.png";
        }
    }
}
