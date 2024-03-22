package it.italiandudes.map_visualizer.data.utils;

import org.jetbrains.annotations.NotNull;

public final class UpdatableString {

    // Attributes
    @NotNull private String string;

    // Constructors
    public UpdatableString() {
        this("");
    }
    public UpdatableString(@NotNull String initialValue) {
        this.string = initialValue;
    }

    // Methods
    @NotNull
    public String getString() {
        return string;
    }
    public void setString(@NotNull String string) {
        this.string = string;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpdatableString)) return false;

        UpdatableString that = (UpdatableString) o;

        return getString().equals(that.getString());
    }
    @Override
    public int hashCode() {
        return getString().hashCode();
    }
    @Override
    public String toString() {
        return string;
    }
}
