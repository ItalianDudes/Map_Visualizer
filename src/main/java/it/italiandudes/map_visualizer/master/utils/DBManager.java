package it.italiandudes.map_visualizer.master.utils;

import it.italiandudes.idl.common.Logger;
import it.italiandudes.map_visualizer.master.javafx.Client;
import it.italiandudes.map_visualizer.master.javafx.alerts.ErrorAlert;
import it.italiandudes.map_visualizer.master.javafx.scenes.SceneLoading;
import it.italiandudes.map_visualizer.master.javafx.scenes.SceneMainMenu;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlDialectInspection"})
public final class DBManager {

    // Attributes
    private static Connection dbConnection = null;
    private static final String DB_PREFIX = "jdbc:sqlite:";

    // Generic SQLite Connection Initializer
    private static void setConnection(@NotNull final String DB_ABSOLUTE_PATH) throws SQLException {
        dbConnection = DriverManager.getConnection(DB_PREFIX + DB_ABSOLUTE_PATH);
        dbConnection.setAutoCommit(true);
        Statement st = dbConnection.createStatement();
        st.execute("PRAGMA foreign_keys = ON;");
        st.close();
    }

    // Methods
    public static void connectToDB(@NotNull final File DB_PATH) throws IOException, SQLException {
        if (!DB_PATH.exists() || DB_PATH.isDirectory()) throw new IOException("This db doesn't exist");
        setConnection(DB_PATH.getAbsolutePath());
    }
    public static void closeConnection() {
        if (dbConnection != null) {
            try {
                dbConnection.close();
            }catch (Exception ignored){}
        }
    }
    public static PreparedStatement preparedStatement(@NotNull final String query) throws SQLException {
        if (dbConnection != null) {
            //noinspection SqlSourceToSinkFlow
            return dbConnection.prepareStatement(query);
        }
        return null;
    }

    // DB Creator
    public static void createSheet(@NotNull final String DB_PATH) throws SQLException {
        setConnection(DB_PATH);
        Scanner reader = new Scanner(Defs.Resources.getAsStream(Defs.Resources.SQL.SQL_MAP_DATA), Defs.DEFAULT_CHARSET);
        StringBuilder queryReader = new StringBuilder();
        String query;
        String buffer;

        while (reader.hasNext()) {
            buffer = reader.nextLine();
            queryReader.append(buffer);
            if (buffer.endsWith(";")) {
                query = queryReader.toString();
                PreparedStatement ps = dbConnection.prepareStatement(query);
                ps.execute();
                ps.close();
                queryReader = new StringBuilder();
            } else {
                queryReader.append('\n');
            }
        }
        reader.close();
        SheetDataHandler.writeKeyParameter(KeyParameters.DB_VERSION, Defs.DB_VERSION);
    }

    // Default DB Error Message
    public static void showDatabaseErrorMessage(@NotNull final Throwable t) {
        Logger.log(t);
        Platform.runLater(() -> {
            new ErrorAlert("ERRORE", "Errore di Database", "Si e' verificato un errore durante la comunicazione con il database, ritorno al menu principale.");
            Client.setScene(SceneLoading.getScene().getParent());
            DBManager.closeConnection();
            Client.setScene(SceneMainMenu.getScene().getParent());
        });
    }
}
