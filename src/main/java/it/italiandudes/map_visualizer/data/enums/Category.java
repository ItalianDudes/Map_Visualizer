package it.italiandudes.map_visualizer.data.enums;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

public enum Category {
    ITEM(0, "Oggetto"),
    EQUIPMENT(1, "Equipaggiamento"),
    SPELL(2, "Incantesimo");

    // Attributes
    @NotNull public static final ArrayList<Category> categories = new ArrayList<>();
    static {
        categories.addAll(Arrays.asList(Category.values()));
    }
    private final int databaseValue;
    private final String name;

    // Constructors
    Category(final int databaseValue, final String name) {
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
