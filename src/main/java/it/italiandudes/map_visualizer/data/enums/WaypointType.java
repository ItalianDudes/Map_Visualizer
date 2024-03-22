package it.italiandudes.map_visualizer.data.enums;

import it.italiandudes.map_visualizer.utils.Defs.Resources.SVG;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

public enum WaypointType {
    ELEMENT_ITEM(SVG.Elements.ELEMENT_ITEM, Color.YELLOW),
    ELEMENT_SPELL(SVG.Elements.ELEMENT_SPELL, Color.YELLOW),
    ELEMENT_ARMOR(SVG.Elements.ELEMENT_ARMOR, Color.YELLOW),
    ELEMENT_ADDON(SVG.Elements.ELEMENT_ADDON, Color.YELLOW),
    ELEMENT_WEAPON(SVG.Elements.ELEMENT_WEAPON, Color.YELLOW),
    ENTITY_PLAYER(SVG.Entities.ENTITY_PLAYER, Color.LIGHTGRAY),
    ENTITY_NPC(SVG.Entities.ENTITY_NPC, Color.LIGHTGRAY),
    ENTITY_ENEMY(SVG.Entities.ENTITY_ENEMY, Color.RED),
    ENTITY_STRONG_ENEMY(SVG.Entities.ENTITY_STRONG_ENEMY, Color.RED),
    ENTITY_BOSS(SVG.Entities.ENTITY_BOSS, Color.RED),
    OBJECTIVE_MISSION_PRIMARY(SVG.Objectives.OBJECTIVE_MISSION, Color.YELLOW),
    POINT_OF_INTEREST_MARKET(SVG.PointsOfInterest.POI_MARKET, Color.GREEN),
    POINT_OF_INTEREST_TAVERN(SVG.PointsOfInterest.POI_TAVERN, Color.GREEN),
    POINT_OF_INTEREST_OFFICE(SVG.PointsOfInterest.POI_OFFICE, Color.GREEN);

    // Attributes
    @NotNull public final String SVG_PATH;
    @NotNull public final Color COLOR;

    WaypointType(@NotNull final String SVG_PATH, @NotNull final Color COLOR) {
        this.SVG_PATH = SVG_PATH;
        this.COLOR = COLOR;
    }
}
