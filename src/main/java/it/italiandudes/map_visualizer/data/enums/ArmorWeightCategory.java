package it.italiandudes.map_visualizer.data.enums;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public enum ArmorWeightCategory {
    LIGHT(0, "Leggero (+Mod Destrezza)"),
    MEDIUM(1, "Medio (+Mod Destrezza MAX 2)"),
    HEAVY(2, "Pesante (Nessun Modificatore)")

    ;

    // Attributes
    @NotNull
    public static final ArrayList<ArmorWeightCategory> ARMOR_WEIGHT_CATEGORIES = new ArrayList<>();
    static {
        Collections.addAll(ARMOR_WEIGHT_CATEGORIES, ArmorWeightCategory.values());
    }
    private final int databaseValue;
    private final String name;

    // Constructors
    ArmorWeightCategory(final int databaseValue, final String name) {
        this.databaseValue = databaseValue;
        this.name = name;
    }

    // Methods
    public int getDatabaseValue() {
        return databaseValue;
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return getName();
    }
}
