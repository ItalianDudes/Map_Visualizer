package it.italiandudes.map_visualizer.data.enums;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public enum ArmorSlot {
    NO_ARMOR(-1, ""),
    FULL_SET(0, "Set Completo"),
    HEAD(1, "Testa"),
    LEFT_SHOULDER(2, "Spalla SX"),
    RIGHT_SHOULDER(3, "Spalla DX"),
    LEFT_ARM(4, "Braccio SX"),
    RIGHT_ARM(5, "Braccio DX"),
    LEFT_FOREARM(6, "Avambraccio SX"),
    RIGHT_FOREARM(7, "Avambraccio DX"),
    LEFT_HAND(8, "Mano SX"),
    RIGHT_HAND(9, "Mano DX"),
    CHEST(10, "Petto"),
    ABDOMEN(11, "Addome"),
    BACK(12, "Schiena"),
    LEFT_LEG(13, "Gamba SX"),
    RIGHT_LEG(14, "Gamba DX"),
    LEFT_KNEE(15, "Ginocchio SX"),
    RIGHT_KNEE(16, "Ginocchio DX"),
    LEFT_FOOT(17, "Piede SX"),
    RIGHT_FOOT(18, "Piede DX")
    ;

    // Attributes
    @NotNull
    public static final ArrayList<ArmorSlot> ARMOR_SLOTS = new ArrayList<>();
    static {
        for (ArmorSlot slot : ArmorSlot.values()) {
            if (slot.databaseValue >= 0) ARMOR_SLOTS.add(slot);
        }
    }
    private final int databaseValue;
    private final String name;

    // Constructors
    ArmorSlot(final int databaseValue, final String name) {
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
