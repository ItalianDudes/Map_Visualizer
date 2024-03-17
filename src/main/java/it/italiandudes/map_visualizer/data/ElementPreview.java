package it.italiandudes.map_visualizer.data;

import it.italiandudes.map_visualizer.data.enums.Category;
import it.italiandudes.map_visualizer.data.enums.Rarity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public final class ElementPreview {

    // Attributes
    private final int id;
    @NotNull private final String name;
    private final Category category;
    private final Rarity rarity;
    private final double weight;
    private final int costCopper;
    private final int quantity;

    // Constructors
    public ElementPreview(final int id, @NotNull final String name, final Category category, final Rarity rarity, final double weight, final int costCopper, final int quantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.costCopper = costCopper;
        this.rarity = rarity;
        this.weight = weight;
        this.quantity = quantity;
    }

    // Methods
    public int getId() {
        return id;
    }
    @NotNull
    public String getName() {
        return name;
    }
    public Category getCategory() {
        return category;
    }
    public Rarity getRarity() {
        return rarity;
    }
    public String getRarityColor() {
        int red = (int)(this.rarity.getColor().getRed()*255);
        int green = (int)(this.rarity.getColor().getGreen()*255);
        int blue = (int)(this.rarity.getColor().getBlue()*255);
        return String.format("#%02X%02X%02X", red, green, blue);
    }
    public double getWeight() {
        return weight;
    }
    public int getCostCopper() {
        return costCopper;
    }
    public int getQuantity() {
        return quantity;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementPreview)) return false;
        ElementPreview that = (ElementPreview) o;
        return getId() == that.getId() && Double.compare(getWeight(), that.getWeight()) == 0 && getCostCopper() == that.getCostCopper() && getQuantity() == that.getQuantity() && Objects.equals(getName(), that.getName()) && getCategory() == that.getCategory() && getRarity() == that.getRarity();
    }
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCategory(), getRarity(), getWeight(), getCostCopper(), getQuantity());
    }
    @Override
    public String toString() {
        return name;
    }
}
