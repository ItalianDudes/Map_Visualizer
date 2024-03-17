package it.italiandudes.map_visualizer.data.enums;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public enum AddonSlot {
    NO_ADDON(-1, ""),
    NECKLACE(0, "Collana"),
    MANTLE(1, "Mantello"),
    LEFT_BRACELET(2, "Bracciale SX"),
    RIGHT_BRACELET(3, "Bracciale DX"),
    LEFT_EARRING(4, "Orecchino SX"),
    RIGHT_EARRING(5, "Orecchino DX"),
    RING_1(6, "Anello 1"),
    RING_2(7, "Anello 2"),
    RING_3(8, "Anello 3"),
    RING_4(9, "Anello 4"),
    BACKPACK(10, "Zaino"),
    BELT(11, "Cintura"),
    BANDOLIER(12, "Bandoliera"),
    ;

    // Attributes
    @NotNull
    public static final ArrayList<AddonSlot> ADDON_SLOTS = new ArrayList<>();
    static {
        for (AddonSlot slot : AddonSlot.values()) {
            if (slot.databaseValue >= 0) ADDON_SLOTS.add(slot);
        }
    }
    private final int databaseValue;
    private final String name;

    // Constructors
    AddonSlot(final int databaseValue, final String name) {
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
