package it.italiandudes.map_visualizer.utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Scanner;

@SuppressWarnings("unused")
public final class JSONManager {

    @NotNull
    public static JSONObject readJSON(@NotNull final File file) throws IOException {
        return readJSON(new Scanner(file, Defs.DEFAULT_CHARSET));
    }
    @NotNull
    public static JSONObject readJSON(@NotNull final InputStream inputStream) throws IOException {
        return readJSON(new Scanner(inputStream, Defs.DEFAULT_CHARSET));
    }
    @NotNull
    private static JSONObject readJSON(@NotNull final Scanner fileReader) throws IOException {
        try {
            StringBuilder buffer = new StringBuilder();
            while (fileReader.hasNext()) {
                buffer.append(fileReader.nextLine()).append('\n');
            }
            fileReader.close();
            return new JSONObject(buffer.toString());
        } catch (JSONException e) {
            throw new IOException("Parse Exception", e);
        }
    }
    public static void writeJSON(@NotNull final JSONObject json, @NotNull final File destination) throws IOException {
        FileWriter writer = new FileWriter(destination);
        writer.append(json.toString(2));
        writer.close();
    }
}