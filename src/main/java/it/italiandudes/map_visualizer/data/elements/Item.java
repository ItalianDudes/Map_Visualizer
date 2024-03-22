package it.italiandudes.map_visualizer.data.elements;

import it.italiandudes.map_visualizer.master.interfaces.ISavable;
import it.italiandudes.map_visualizer.master.interfaces.ISerializable;
import it.italiandudes.map_visualizer.master.javafx.components.Waypoint;
import it.italiandudes.map_visualizer.master.utils.DBManager;
import it.italiandudes.map_visualizer.data.enums.Category;
import it.italiandudes.map_visualizer.data.enums.Rarity;
import it.italiandudes.map_visualizer.data.enums.SerializerType;
import it.italiandudes.map_visualizer.utils.Defs;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class Item implements ISavable, ISerializable {

    // Attributes
    @Nullable private Integer itemID = null;
    @Nullable private String base64image;
    @Nullable private String imageExtension;
    @NotNull private String name;
    private int costCopper;
    @Nullable private String description;
    @NotNull private Rarity rarity;
    private double weight;
    @NotNull private Category category;
    private int quantity;
    @NotNull private final Waypoint waypoint;

    // Constructors
    public Item(@NotNull final Category category, @NotNull final Waypoint waypoint) {
        name = "";
        rarity = Rarity.COMMON;
        costCopper = 0;
        weight = 0;
        this.category = category;
        quantity = 0;
        this.waypoint = waypoint;
    }
    public Item(@NotNull final Item item) {
        this.itemID = item.itemID;
        this.base64image = item.base64image;
        this.imageExtension = item.imageExtension;
        this.name = item.name;
        this.costCopper = item.costCopper;
        this.description = item.description;
        this.rarity = item.rarity;
        this.weight = item.weight;
        this.category = item.category;
        this.quantity = item.quantity;
        this.waypoint = item.waypoint;
    }
    public Item(@Nullable final Integer itemID, @Nullable final String base64image, @Nullable final String imageExtension,
                @NotNull final String name, final int costCopper, @Nullable final String description, @Nullable final Rarity rarity,
                final double weight, @Nullable final Category category, final int quantity, @NotNull final Waypoint waypoint) {
        this.itemID = itemID;
        this.base64image = base64image;
        this.imageExtension = imageExtension;
        this.name = name;
        this.costCopper = Math.max(0, costCopper);
        this.description = description;
        if (rarity == null) this.rarity = Rarity.values()[0];
        else this.rarity = rarity;
        this.weight = Math.max(0, weight);
        if (category == null) this.category = Category.values()[0];
        else this.category = category;
        this.quantity = Math.max(0, quantity);
        this.waypoint = waypoint;
    }
    public Item(@Nullable final Integer itemID, @Nullable final Image image, @Nullable final String imageExtension,
                @NotNull final String name, int cc, int cs, int ce, int cg, int cp,
                @Nullable final String description, @Nullable final String rarity, @Nullable final Category category,
                final double weight, final int quantity, @NotNull final Waypoint waypoint) {
        this.itemID = itemID;
        this.name = name;
        if (cc < 0) cc = 0;
        if (cs < 0) cs = 0;
        if (ce < 0) ce = 0;
        if (cg < 0) cg = 0;
        if (cp < 0) cp = 0;
        this.costCopper = cc + cs*10 + ce*50 + cg*100 + cp*1000;
        this.description = description;
        if (rarity == null) this.rarity = Rarity.values()[0];
        else this.rarity = Rarity.values()[Rarity.colorNames.indexOf(rarity)];
        ByteArrayOutputStream imageByteStream = new ByteArrayOutputStream();
        if (imageExtension != null && image != null) {
            try {
                this.imageExtension = imageExtension;
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), imageExtension, imageByteStream);
                this.base64image = Base64.getEncoder().encodeToString(imageByteStream.toByteArray());
            } catch (IOException e) {
                this.imageExtension = null;
                this.base64image = null;
            }
        } else {
            this.imageExtension = null;
            this.base64image = null;
        }
        if (category == null) this.category = Category.values()[0];
        else this.category = category;
        if (weight < 0) this.weight = 0;
        else this.weight = weight;
        this.quantity = Math.max(0, quantity);
        this.waypoint = waypoint;
    }
    public Item(@NotNull final String name) throws SQLException {
        String query = "SELECT * FROM items WHERE name = ?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("The database connection is not initialized");
        ps.setString(1, name);
        Item retrievedItem = new Item(ps.executeQuery());
        ps.close();
        this.itemID = retrievedItem.itemID;
        this.base64image = retrievedItem.base64image;
        this.imageExtension = retrievedItem.imageExtension;
        this.name = retrievedItem.name;
        this.costCopper = Math.max(0, retrievedItem.costCopper);
        this.description = retrievedItem.description;
        this.rarity = retrievedItem.rarity;
        this.weight = Math.max(0, retrievedItem.weight);
        this.category = retrievedItem.category;
        this.quantity = retrievedItem.quantity;
        this.waypoint = retrievedItem.waypoint;
    }
    public Item(int itemID) throws SQLException {
        String query = "SELECT * FROM items WHERE id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("The database connection is not initialized");
        ps.setInt(1, itemID);
        Item retrievedItem = new Item(ps.executeQuery());
        ps.close();
        this.itemID = retrievedItem.itemID;
        this.base64image = retrievedItem.base64image;
        this.imageExtension = retrievedItem.imageExtension;
        this.name = retrievedItem.name;
        this.costCopper = Math.max(0, retrievedItem.costCopper);
        this.description = retrievedItem.description;
        this.rarity = retrievedItem.rarity;
        this.weight = Math.max(0, retrievedItem.weight);
        this.category = retrievedItem.category;
        this.quantity = retrievedItem.quantity;
        this.waypoint = retrievedItem.waypoint;
    }
    private Item(@NotNull final ResultSet resultSet) throws SQLException {
        this.itemID = resultSet.getInt("id");
        try {
            this.base64image = resultSet.getString("base64image");
            this.imageExtension = resultSet.getString("image_extension");
        } catch (SQLException e) {
            this.base64image = null;
            this.imageExtension = null;
        }
        this.name = resultSet.getString("name");
        try {
            this.costCopper = resultSet.getInt("cost_copper");
            if (this.costCopper < 0) this.costCopper = 0;
        } catch (SQLException e) {
            this.costCopper = 0;
        }
        try {
            this.description = resultSet.getString("description");
        } catch (SQLException e) {
            this.description = null;
        }
        this.rarity = Rarity.values()[resultSet.getInt("rarity")];
        this.weight = resultSet.getDouble("weight");
        if (this.weight < 0) this.weight = 0;
        this.category = Category.values()[resultSet.getInt("category")];
        this.quantity = resultSet.getInt("quantity");
        if (this.quantity < 0) this.quantity = 0;
        this.waypoint = new Waypoint(resultSet.getInt("waypoint_id"));
    }

    // Methods
    @SuppressWarnings("DuplicatedCode")
    public static boolean checkIfExist(@NotNull final String itemName) throws SQLException {
        String query = "SELECT id FROM items WHERE name=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("There's no connection with the database");
        ps.setString(1, itemName);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            result.getInt("id");
            ps.close();
            return true;
        } else {
            ps.close();
            return false;
        }
    }
    @Override @SuppressWarnings("DuplicatedCode")
    public JSONObject exportElementJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SERIALIZER_ID, SerializerType.ITEM.ordinal());
        jsonObject.put(DB_VERSION, Defs.DB_VERSION);
        jsonObject.put("base64image", base64image);
        jsonObject.put("imageExtension", imageExtension);
        jsonObject.put("name", name);
        jsonObject.put("costCopper", costCopper);
        jsonObject.put("description", description);
        jsonObject.put("rarity", Rarity.colorNames.indexOf(rarity.getTextedRarity()));
        jsonObject.put("weight", weight);
        jsonObject.put("category", category.getDatabaseValue());
        jsonObject.put("quantity", quantity);
        return jsonObject;
    }
    @Override @SuppressWarnings("DuplicatedCode")
    public String exportElement() {
        return Base64.getEncoder().encodeToString(exportElementJSON().toString().getBytes(StandardCharsets.UTF_8));
    }
    @Override @SuppressWarnings("DuplicatedCode")
    public void saveIntoDatabase(@Nullable final String oldName) throws SQLException {
        String itemCheckerQuery = "SELECT id FROM items WHERE name=?;";
        PreparedStatement ps = DBManager.preparedStatement(itemCheckerQuery);
        if (ps == null) throw new SQLException("The database connection doesn't exist");
        ps.setString(1, oldName);
        ResultSet result = ps.executeQuery();
        String query;
        int itemID;
        if (result.next()) { // Update
            waypoint.saveIntoDatabase(oldName);
            itemID = result.getInt("id");
            ps.close();
            query = "UPDATE items SET name=?, base64image=?, image_extension=?, cost_copper=?, description=?, rarity=?, weight=?, category=?, quantity=? WHERE id=?;";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database connection doesn't exist");
            ps.setString(1, getName());
            ps.setString(2, getBase64image());
            ps.setString(3, getImageExtension());
            ps.setDouble(4, getCostCopper());
            ps.setString(5, getDescription());
            ps.setInt(6, Rarity.colorNames.indexOf(getRarity().getTextedRarity()));
            ps.setDouble(7, getWeight());
            ps.setInt(8, getCategory().getDatabaseValue());
            ps.setInt(9, getQuantity());
            ps.setInt(10, itemID);
            ps.executeUpdate();
            ps.close();
        } else { // Insert
            waypoint.saveIntoDatabase(null);
            assert waypoint.getWaypointID() != null;
            ps.close();
            query = "INSERT INTO items (name, base64image, image_extension, cost_copper, description, rarity, weight, category, quantity, waypoint_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database connection doesn't exist");
            ps.setString(1, getName());
            ps.setString(2, getBase64image());
            ps.setString(3, getImageExtension());
            ps.setDouble(4, getCostCopper());
            ps.setString(5, getDescription());
            ps.setInt(6, Rarity.colorNames.indexOf(getRarity().getTextedRarity()));
            ps.setDouble(7, getWeight());
            ps.setInt(8, getCategory().getDatabaseValue());
            ps.setInt(9, getQuantity());
            ps.setInt(10, waypoint.getWaypointID());
            ps.executeUpdate();
            ps.close();
            query = "SELECT id FROM items WHERE name=?;";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database connection doesn't exist");
            ps.setString(1, getName());
            result = ps.executeQuery();
            if (result.next()) {
                setItemID(result.getInt("id"));
                ps.close();
            } else {
                ps.close();
                throw new SQLException("Something strange happened on item insert! Item insert but doesn't result on select");
            }
        }
    }
    @Nullable
    public Integer getItemID() {
        return itemID;
    }
    public void setItemID(final int itemID) {
        if (this.itemID == null) this.itemID = itemID;
    }
    @Nullable
    public String getBase64image() {
        return base64image;
    }
    public void setBase64image(@Nullable final String base64image) {
        this.base64image = base64image;
    }
    @Nullable
    public String getImageExtension() {
        return imageExtension;
    }
    public void setImageExtension(@Nullable final String imageExtension) {
        this.imageExtension = imageExtension;
    }
    @NotNull
    public String getName() {
        return name;
    }
    public void setName(@NotNull final String name) {
        this.name = name;
    }
    public int getCostCopper() {
        return costCopper;
    }
    public void setCostCopper(final int costCopper) {
        if (costCopper >= 0) this.costCopper = costCopper;
    }
    @Nullable
    public String getDescription() {
        return description;
    }
    public void setDescription(@Nullable final String description) {
        this.description = description;
    }
    @NotNull
    public Rarity getRarity() {
        return rarity;
    }
    public void setRarity(@NotNull final Rarity rarity) {
        this.rarity = rarity;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(final double weight) {
        if (weight >= 0) this.weight = weight;
    }
    @NotNull
    public Category getCategory() {
        return category;
    }
    public void setCategory(@Nullable final Category category) {
        if (category == null) this.category = Category.values()[0];
        else this.category = category;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(final int quantity) {
        this.quantity = Math.max(0, quantity);
    }
    @NotNull
    public Waypoint getWaypoint() {
        return waypoint;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;

        Item item = (Item) o;

        if (getCostCopper() != item.getCostCopper()) return false;
        if (Double.compare(getWeight(), item.getWeight()) != 0) return false;
        if (getQuantity() != item.getQuantity()) return false;
        if (getItemID() != null ? !getItemID().equals(item.getItemID()) : item.getItemID() != null) return false;
        if (getBase64image() != null ? !getBase64image().equals(item.getBase64image()) : item.getBase64image() != null)
            return false;
        if (getImageExtension() != null ? !getImageExtension().equals(item.getImageExtension()) : item.getImageExtension() != null)
            return false;
        if (!getName().equals(item.getName())) return false;
        if (getDescription() != null ? !getDescription().equals(item.getDescription()) : item.getDescription() != null)
            return false;
        if (getRarity() != item.getRarity()) return false;
        if (getCategory() != item.getCategory()) return false;
        return getWaypoint().equals(item.getWaypoint());
    }
    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getItemID() != null ? getItemID().hashCode() : 0;
        result = 31 * result + (getBase64image() != null ? getBase64image().hashCode() : 0);
        result = 31 * result + (getImageExtension() != null ? getImageExtension().hashCode() : 0);
        result = 31 * result + getName().hashCode();
        result = 31 * result + getCostCopper();
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + getRarity().hashCode();
        temp = Double.doubleToLongBits(getWeight());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getCategory().hashCode();
        result = 31 * result + getQuantity();
        result = 31 * result + getWaypoint().hashCode();
        return result;
    }
    @Override
    public String toString() {
        return getName();
    }
}
