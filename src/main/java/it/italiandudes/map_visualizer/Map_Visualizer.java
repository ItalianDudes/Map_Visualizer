package it.italiandudes.map_visualizer;

import it.italiandudes.map_visualizer.client.javafx.Client;
import it.italiandudes.idl.common.InfoFlags;
import it.italiandudes.idl.common.Logger;

import java.io.IOException;

public final class Map_Visualizer {

    // Main Method
    public static void main(String[] args) {

        // Initializing the logger
        try {
            Logger.init();
        } catch (IOException e) {
            Logger.log("An error has occurred during Logger initialization, exit...");
            return;
        }

        // Configure the shutdown hooks
        Runtime.getRuntime().addShutdownHook(new Thread(Logger::close));

        // Start the client
        try {
            Client.start(args);
        } catch (NoClassDefFoundError e) {
            Logger.log("ERROR: TO RUN THIS JAR YOU NEED JAVA 8 WITH BUILT-IN JAVAFX!", new InfoFlags(true, true, true, true));
            Logger.log(e);
            System.exit(0);
        }
    }
}
