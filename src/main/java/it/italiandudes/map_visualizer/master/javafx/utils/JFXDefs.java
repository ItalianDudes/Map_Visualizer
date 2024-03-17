package it.italiandudes.map_visualizer.master.javafx.utils;

import it.italiandudes.map_visualizer.utils.Defs;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;

@SuppressWarnings("unused")
public final class JFXDefs {

    //App Info
    public static final class AppInfo {
        public static final String NAME = "Map Visualizer";
        public static final Image LOGO = new Image(Defs.Resources.get(Resources.Image.IMAGE_LOGO).toString());
    }

    // System Info
    public static final class SystemGraphicInfo {
        public static final Rectangle2D SCREEN_RESOLUTION = Screen.getPrimary().getBounds();
        public static final double SCREEN_WIDTH = SCREEN_RESOLUTION.getWidth();
        public static final double SCREEN_HEIGHT = SCREEN_RESOLUTION.getHeight();
    }

    // Resource Locations
    public static final class Resources {

        // FXML Location
        public static final class FXML {
            private static final String FXML_DIR = "/fxml/";
            public static final String FXML_LOADING = FXML_DIR + "SceneLoading.fxml";
            public static final String FXML_MAIN_MENU = FXML_DIR + "SceneMainMenu.fxml";
            public static final String FXML_SETTINGS_EDITOR = FXML_DIR + "SceneSettingsEditor.fxml";
            public static final String FXML_MAP_SHEET = FXML_DIR + "SceneMapSheet.fxml";

            // Sheet Tabs
            public static final class Tabs {
                private static final String TAB_DIR = FXML_DIR + "tabs/";
                public static final String FXML_TAB_MAP = TAB_DIR + "SceneTabMap.fxml";
                public static final String FXML_TAB_ELEMENTS = TAB_DIR + "SceneTabElements.fxml";
                public static final String FXML_TAB_SETTINGS = TAB_DIR + "SceneTabSettings.fxml";
            }

            public static final class Elements {
                private static final String FXML_ELEMENTS_DIR = FXML_DIR + "elements/";
                public static final String FXML_ELEMENT_ITEM = FXML_ELEMENTS_DIR + "SceneElementItem.fxml";
                public static final String FXML_ELEMENT_SPELL = FXML_ELEMENTS_DIR + "SceneElementSpell.fxml";
                public static final String FXML_ELEMENT_ARMOR = FXML_ELEMENTS_DIR + "SceneElementArmor.fxml";
                public static final String FXML_ELEMENT_WEAPON = FXML_ELEMENTS_DIR + "SceneElementWeapon.fxml";
                public static final String FXML_ELEMENT_ADDON = FXML_ELEMENTS_DIR + "SceneElementAddon.fxml";
            }
        }

        // GIF Location
        public static final class GIF {
            private static final String GIF_DIR = "/gif/";
            public static final String GIF_LOADING = GIF_DIR+"loading.gif";
        }

        // CSS Location
        public static final class CSS {
            private static final String CSS_DIR = "/css/";
            public static final String CSS_LIGHT_THEME = CSS_DIR + "light_theme.css";
            public static final String CSS_DARK_THEME = CSS_DIR + "dark_theme.css";
        }

        // Image Location
        public static final class Image {
            private static final String IMAGE_DIR = "/image/";
            public static final String IMAGE_LOGO = IMAGE_DIR+"app-logo.png";
            public static final String IMAGE_FILE_EXPLORER = IMAGE_DIR+"file-explorer.png";
        }
    }

}
