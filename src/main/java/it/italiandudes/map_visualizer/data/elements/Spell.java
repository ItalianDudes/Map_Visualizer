package it.italiandudes.map_visualizer.data.elements;

import it.italiandudes.map_visualizer.master.interfaces.ISavable;
import it.italiandudes.map_visualizer.master.javafx.components.Waypoint;
import it.italiandudes.map_visualizer.master.utils.DBManager;
import it.italiandudes.map_visualizer.data.enums.Category;
import it.italiandudes.map_visualizer.data.enums.Rarity;
import it.italiandudes.map_visualizer.data.enums.SerializerType;
import it.italiandudes.map_visualizer.utils.Defs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Objects;

@SuppressWarnings("unused")
public final class Spell extends Item implements ISavable {

    // Attributes
    @Nullable private Integer spellID;
    private int level;
    @Nullable private String type;
    @Nullable private String castTime;
    @Nullable private String range;
    @Nullable private String components;
    @Nullable private String duration;

    // Constructors
    public Spell(@NotNull final Waypoint waypoint) {
        super(Category.SPELL, waypoint);
        level = 0;
    }
    public Spell(@NotNull final Item baseItem, @Nullable final Integer spellID,
                 final int level, @Nullable final String type, @Nullable final String castTime,
                 @Nullable final String range, @Nullable final String components,
                 @Nullable final String duration) {
        super(baseItem);
        this.spellID = spellID;
        if (level >= 0 && level <= 9) this.level = level;
        else this.level = 0;
        this.type = type;
        this.castTime = castTime;
        this.range = range;
        this.components = components;
        this.duration = duration;
    }
    public Spell(@NotNull final String spellName) throws SQLException {
        super(spellName);
        String query = "SELECT * FROM spells WHERE item_id = ?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("The database is not connected");
        Integer itemID = getItemID();
        assert itemID != null;
        ps.setInt(1, itemID);
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            this.spellID = result.getInt("id");
            this.level = result.getInt("level");
            this.castTime = result.getString("cast_time");
            this.range = result.getString("spell_range");
            this.duration = result.getString("duration");
            this.components = result.getString("components");
            this.type = result.getString("type");
            ps.close();
        } else {
            ps.close();
            throw new SQLException("Exist the item, but not the spell");
        }
    }
    public Spell(@NotNull final ResultSet resultSet) throws SQLException {
        super(resultSet.getInt("item_id"));
        this.spellID = resultSet.getInt("id");
        try {
            this.level = resultSet.getInt("level");
            if (this.level < 0 || this.level > 9) this.level = 0;
        } catch (SQLException e) {
            this.level = 0;
        }
        try {
            this.type = resultSet.getString("type");
        } catch (SQLException e) {
            this.type = null;
        }
        try {
            this.castTime = resultSet.getString("cast_time");
        } catch (SQLException e) {
            this.castTime = null;
        }
        try {
            this.range = resultSet.getString("spell_range");
        } catch (SQLException e) {
            this.range = null;
        }
        try {
            this.components = resultSet.getString("components");
        } catch (SQLException e) {
            this.components = null;
        }
        try {
            this.duration = resultSet.getString("duration");
        } catch (SQLException e) {
            this.duration = null;
        }
    }

    // Methods
    @Override @SuppressWarnings("DuplicatedCode")
    public JSONObject exportElementJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SERIALIZER_ID, SerializerType.SPELL.ordinal());
        jsonObject.put(DB_VERSION, Defs.DB_VERSION);
        jsonObject.put("base64image", getBase64image());
        jsonObject.put("imageExtension", getImageExtension());
        jsonObject.put("name", getName());
        jsonObject.put("costCopper", getCostCopper());
        jsonObject.put("description", getDescription());
        jsonObject.put("rarity", Rarity.colorNames.indexOf(getRarity().getTextedRarity()));
        jsonObject.put("weight", getWeight());
        jsonObject.put("category", getCategory().getDatabaseValue());
        jsonObject.put("quantity", getQuantity());
        jsonObject.put("level", level);
        jsonObject.put("type", type);
        jsonObject.put("castTime", castTime);
        jsonObject.put("range", range);
        jsonObject.put("components", components);
        jsonObject.put("duration", duration);
        return jsonObject;
    }
    @Override @SuppressWarnings("DuplicatedCode")
    public String exportElement() {
        return Base64.getEncoder().encodeToString(exportElementJSON().toString().getBytes(StandardCharsets.UTF_8));
    }
    @Override @SuppressWarnings("DuplicatedCode")
    public void saveIntoDatabase(@Nullable final String oldName) throws SQLException {
        super.saveIntoDatabase(oldName);
        Integer itemID = getItemID();
        assert itemID != null;
        if (spellID == null) { // Insert
            String query = "INSERT INTO spells (item_id, level, type, cast_time, spell_range, components, duration) VALUES (?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database is not connected");
            ps.setInt(1, itemID);
            ps.setInt(2, getLevel());
            ps.setString(3, getType());
            ps.setString(4, getCastTime());
            ps.setString(5, getRange());
            ps.setString(6, getComponents());
            ps.setString(7, getDuration());
            ps.executeUpdate();
            ps.close();
            query = "SELECT id FROM spells WHERE item_id = ?;";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database is not connected");
            ps.setInt(1, itemID);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                setSpellID(resultSet.getInt("id"));
                ps.close();
            } else {
                ps.close();
                throw new SQLException("Something strange happened on spell insert! Spell insert but doesn't result on select");
            }
        } else { // Update
            String query = "UPDATE spells SET item_id=?, level=?, type=?, cast_time=?, spell_range=?, components=?, duration=? WHERE id=?;";
            PreparedStatement ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database is not connected");
            ps.setInt(1, itemID);
            ps.setInt(2, getLevel());
            ps.setString(3, getType());
            ps.setString(4, getCastTime());
            ps.setString(5, getRange());
            ps.setString(6, getComponents());
            ps.setString(7, getDuration());
            ps.setInt(8, getSpellID());
            ps.executeUpdate();
            ps.close();
        }
    }
    @Nullable
    public Integer getSpellID() {
        return spellID;
    }
    public void setSpellID(final int spellID) {
        if (this.spellID == null) this.spellID = spellID;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(final int level) {
        if (level >= 0 && level <= 9) this.level = level;
    }
    @Nullable
    public String getType() {
        return type;
    }
    public void setType(@Nullable final String type) {
        this.type = type;
    }
    @Nullable
    public String getCastTime() {
        return castTime;
    }
    public void setCastTime(@Nullable final String castTime) {
        this.castTime = castTime;
    }
    @Nullable
    public String getRange() {
        return range;
    }
    public void setRange(@Nullable final String range) {
        this.range = range;
    }
    @Nullable
    public String getComponents() {
        return components;
    }
    public void setComponents(@Nullable final String components) {
        this.components = components;
    }
    @Nullable
    public String getDuration() {
        return duration;
    }
    public void setDuration(@Nullable final String duration) {
        this.duration = duration;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Spell)) return false;
        if (!super.equals(o)) return false;
        Spell spell = (Spell) o;
        return getLevel() == spell.getLevel() && Objects.equals(getSpellID(), spell.getSpellID()) && Objects.equals(getType(), spell.getType()) && Objects.equals(getCastTime(), spell.getCastTime()) && Objects.equals(getRange(), spell.getRange()) && Objects.equals(getComponents(), spell.getComponents()) && Objects.equals(getDuration(), spell.getDuration());
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSpellID(), getLevel(), getType(), getCastTime(), getRange(), getComponents(), getDuration());
    }
}
