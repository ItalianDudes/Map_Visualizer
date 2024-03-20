package it.italiandudes.map_visualizer.master.javafx.components.waypoints;

import it.italiandudes.map_visualizer.utils.SVGReader;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.jetbrains.annotations.NotNull;

public final class Waypoint extends SVGPath {

    // Attributes
    @NotNull private String name;
    @NotNull private final Point2D center;

    // Constructors
    public Waypoint(@NotNull String name, @NotNull final String path, @NotNull final Point2D center) {
        super();
        this.name = name;
        this.center = center;
        setContent(SVGReader.readSVGFileFromJar(path));
        setLayoutX(center.getX());
        setLayoutY(center.getY());
    }

    // Methods
    @NotNull
    public String getName() {
        return name;
    }
    public void setName(@NotNull String name) {
        this.name = name;
    }
    @NotNull
    public Point2D getCenter() {
        return center;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;

        Waypoint waypoint = (Waypoint) o;

        if (!getName().equals(waypoint.getName())) return false;
        return getCenter().equals(waypoint.getCenter());
    }
    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getCenter().hashCode();
        return result;
    }
    @Override @NotNull
    public String toString() {
        return name;
    }
}
