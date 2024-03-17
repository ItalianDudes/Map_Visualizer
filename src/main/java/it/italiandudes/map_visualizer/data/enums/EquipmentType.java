package it.italiandudes.map_visualizer.data.enums;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public enum EquipmentType {
    ARMOR(0, "Armatura"),
    WEAPON(1, "Arma"),
    ADDON(2, "Addon")
    ;

    // Attributes
    @NotNull
    public static final ArrayList<EquipmentType> types = new ArrayList<>();
    static {
        types.addAll(Arrays.asList(EquipmentType.values()));
    }
    private final int databaseValue;
    private final String name;

    // Constructors
    EquipmentType(final int databaseValue, final String name) {
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
