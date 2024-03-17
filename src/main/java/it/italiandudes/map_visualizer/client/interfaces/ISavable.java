package it.italiandudes.map_visualizer.client.interfaces;

import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

@SuppressWarnings("unused")
public interface ISavable {
    void saveIntoDatabase(@Nullable final String oldName) throws SQLException;
}
