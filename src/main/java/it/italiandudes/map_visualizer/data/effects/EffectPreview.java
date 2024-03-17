package it.italiandudes.dnd_visualizer.data.effect;

import it.italiandudes.dnd_visualizer.data.enums.EffectKnowledge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class EffectPreview {

    // Attributes
    private final int id;
    @NotNull private final String name;
    @Nullable private final String duration;
    private final int intensity;
    @NotNull private final EffectKnowledge isTreatable;
    @NotNull private final EffectKnowledge isCurable;
    @NotNull private final EffectKnowledge isLethal;
    @NotNull private final String isActive;

    // Constructors
    public EffectPreview(final int id, @NotNull final String name, @Nullable final String duration,
                         final int intensity, @NotNull final EffectKnowledge isTreatable,
                         @NotNull final EffectKnowledge isCurable, @NotNull final EffectKnowledge isLethal,
                         final boolean isActive) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.intensity = intensity;
        this.isTreatable = isTreatable;
        this.isCurable = isCurable;
        this.isLethal = isLethal;
        if (isActive) {
            this.isActive = "Si";
        } else {
            this.isActive = "No";
        }
    }

    // Methods
    public int getId() {
        return id;
    }
    @NotNull
    public String getName() {
        return name;
    }
    @Nullable
    public String getDuration() {
        return duration;
    }
    public int getIntensity() {
        return intensity;
    }
    @NotNull
    public EffectKnowledge getIsTreatable() {
        return isTreatable;
    }
    @NotNull
    public EffectKnowledge getIsCurable() {
        return isCurable;
    }
    @NotNull
    public EffectKnowledge getIsLethal() {
        return isLethal;
    }
    @NotNull
    public String getIsActive() {
        return isActive;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EffectPreview)) return false;
        EffectPreview that = (EffectPreview) o;
        return getId() == that.getId() && getIntensity() == that.getIntensity() && Objects.equals(getName(), that.getName()) && Objects.equals(getDuration(), that.getDuration()) && getIsTreatable() == that.getIsTreatable() && getIsCurable() == that.getIsCurable() && getIsLethal() == that.getIsLethal() && Objects.equals(getIsActive(), that.getIsActive());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDuration(), getIntensity(), getIsTreatable(), getIsCurable(), getIsLethal(), getIsActive());
    }
    @Override
    public String toString() {
        return name;
    }
}
