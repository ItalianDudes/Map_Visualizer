package it.italiandudes.map_visualizer.utils;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public final class SVGReader {

    @NotNull
    public static String readSVGFileFromJar(@NotNull final String path) {
        Scanner scanner = new Scanner(Defs.Resources.getAsStream(path));
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNext()) {
            builder.append(scanner.nextLine());
        }
        scanner.close();
        return builder.toString();
    }
}
