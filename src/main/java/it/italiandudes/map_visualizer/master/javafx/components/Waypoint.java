package it.italiandudes.map_visualizer.master.javafx.components;

import it.italiandudes.map_visualizer.data.enums.WaypointType;
import it.italiandudes.map_visualizer.master.interfaces.ISavable;
import it.italiandudes.map_visualizer.master.utils.DBManager;
import it.italiandudes.map_visualizer.utils.SVGReader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class Waypoint extends StackPane implements ISavable {

    // Attributes
    @Nullable private Integer waypointID = null;
    @NotNull private String name;
    @NotNull private Point2D center;
    @NotNull private final WaypointType type;
    private boolean isZoom;

    // Constructors
    public Waypoint(final int id) throws SQLException {
        String query = "SELECT * FROM waypoints WHERE id=?;";
        try (PreparedStatement ps = DBManager.preparedStatement(query)) {
            if (ps == null) throw new SQLException("The prepared statement is null");
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                this.waypointID = id;
                this.name = result.getString("name");
                this.center = new Point2D(result.getDouble("center_x"), result.getDouble("center_y"));
                this.type = WaypointType.values()[result.getInt("type")];
            } else throw new SQLException("WaypointID not found");
        }
    }
    public Waypoint(@NotNull final WaypointType type, @NotNull Point2D center) {
        this("", type, center);
    }
    public Waypoint(@NotNull String name, @NotNull final WaypointType type, @NotNull Point2D center) {
        super();
        this.name = name;
        this.center = center;
        this.type = type;
        SVGPath icon = new SVGPath();
        icon.setContent(SVGReader.readSVGFileFromJar(type.SVG_PATH));
        Pane pane = new Pane();
        pane.setShape(icon);
        pane.getStyleClass().add("waypoint-icon");
        getChildren().add(pane);
        setAlignment(Pos.CENTER);
        setZoom(false);
        setBackground(new Background(new BackgroundFill(type.COLOR, null, null)));
        getStyleClass().add("waypoint");
        setLayoutX(center.getX());
        setLayoutY(center.getY());
    }

    // Waypoint Checker
    public static boolean checkIfExist(@NotNull String name, @NotNull WaypointType type) throws SQLException {
        String query = "SELECT * FROM waypoints WHERE name=? AND type=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("Prepared Statement is null");
        ps.setString(1, name);
        ps.setInt(2, type.ordinal());
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            ps.close();
            return true;
        } else {
            ps.close();
            return false;
        }
    }

    // Methods
    @Override
    public void saveIntoDatabase(@Nullable String oldName) throws SQLException {
        ResultSet result = null;
        PreparedStatement ps = null;
        if (oldName != null) {
            String itemCheckerQuery = "SELECT id FROM waypoints WHERE name=? AND type=?;";
            ps = DBManager.preparedStatement(itemCheckerQuery);
            if (ps == null) throw new SQLException("The database connection doesn't exist");
            ps.setString(1, oldName);
            ps.setInt(2, type.ordinal());
            result = ps.executeQuery();
        }
        String query;
        int itemID;
        if (result != null && result.next()) { // Update
            itemID = result.getInt("id");
            ps.close();
            query = "UPDATE waypoints SET name=?, center_x=?, center_y=? WHERE id=?;";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database connection doesn't exist");
            ps.setString(1, getName());
            ps.setDouble(2, center.getX());
            ps.setDouble(3, center.getY());
            ps.setInt(4, itemID);
            ps.executeUpdate();
            ps.close();
        } else { // Insert
            query = "INSERT INTO waypoints (name, type, center_x, center_y) VALUES (?, ?, ?, ?);";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database connection doesn't exist");
            ps.setString(1, name);
            ps.setInt(2, type.ordinal());
            ps.setDouble(3, center.getX());
            ps.setDouble(4, center.getY());
            ps.executeUpdate();
            ps.close();
            query = "SELECT id FROM waypoints WHERE name=? AND type=?;";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database connection doesn't exist");
            ps.setString(1, getName());
            ps.setInt(2, type.ordinal());
            result = ps.executeQuery();
            if (result.next()) {
                setWaypointID(result.getInt("id"));
                ps.close();
            } else {
                ps.close();
                throw new SQLException("Something strange happened on waypoint insert! Waypoint insert but doesn't result on select");
            }
        }
    }
    @Nullable
    public Integer getWaypointID() {
        return waypointID;
    }
    public void setWaypointID(@NotNull Integer waypointID) {
        if (this.waypointID != null) return;
        this.waypointID = waypointID;
    }
    @NotNull
    public WaypointType getType() {
        return type;
    }
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
    public void setCenter(@NotNull Point2D center) {
        this.center = center;
        setLayoutX(center.getX());
        setLayoutY(center.getY());
    }
    public boolean getZoom() {
        return isZoom;
    }
    public void setZoom(boolean zoom) {
        this.isZoom = zoom;
        if (zoom) {
            setScaleX(1.0);
            setScaleY(1.0);
        } else {
            setScaleX(0.5);
            setScaleY(0.5);
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;

        Waypoint waypoint = (Waypoint) o;

        if (getZoom() != waypoint.getZoom()) return false;
        if (getWaypointID() != null ? !getWaypointID().equals(waypoint.getWaypointID()) : waypoint.getWaypointID() != null)
            return false;
        if (!getName().equals(waypoint.getName())) return false;
        if (!getCenter().equals(waypoint.getCenter())) return false;
        return getType() == waypoint.getType();
    }
    @Override
    public int hashCode() {
        int result = getWaypointID() != null ? getWaypointID().hashCode() : 0;
        result = 31 * result + getName().hashCode();
        result = 31 * result + getCenter().hashCode();
        result = 31 * result + getType().hashCode();
        result = 31 * result + (getZoom() ? 1 : 0);
        return result;
    }
    @Override @NotNull
    public String toString() {
        return name;
    }
}
