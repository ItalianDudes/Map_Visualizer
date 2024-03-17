package it.italiandudes.map_visualizer.data.elements;

import it.italiandudes.map_visualizer.master.interfaces.ISavable;
import it.italiandudes.map_visualizer.master.utils.DBManager;
import it.italiandudes.map_visualizer.data.enums.EquipmentType;
import it.italiandudes.map_visualizer.data.enums.Rarity;
import it.italiandudes.map_visualizer.data.enums.SerializerType;
import it.italiandudes.map_visualizer.utils.Defs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

@SuppressWarnings("unused")
public final class Weapon extends Equipment implements ISavable {

    // Attributes
    @Nullable private Integer weaponID;
    @Nullable private String weaponCategory;
    @Nullable private String properties;

    // Constructors
    public Weapon() {
        super(EquipmentType.WEAPON);
    }
    public Weapon(@NotNull final Item item, final int equipmentID, final int weaponID, @Nullable final String weaponCategory,
                  @Nullable final String properties, final int lifeEffect, final double lifeEffectPerc, final int loadEffect,
                  final double loadEffectPerc, final int caEffect, @Nullable final String otherEffects, final boolean isEquipped) {
        super(item, EquipmentType.WEAPON, lifeEffect, lifeEffectPerc, caEffect, loadEffect, loadEffectPerc, otherEffects, isEquipped);
        this.weaponCategory = weaponCategory;
        this.properties = properties;
        this.setEquipmentID(equipmentID);
        this.weaponID = weaponID;
    }
    public Weapon(@NotNull final Item item, @Nullable final String weaponCategory, @Nullable final String properties,
                  final int lifeEffect, final double lifeEffectPerc, final int loadEffect, final double loadEffectPerc,
                  final int caEffect, @Nullable final String otherEffects, final boolean isEquipped) {
        super(item, EquipmentType.WEAPON, lifeEffect, lifeEffectPerc, caEffect, loadEffect, loadEffectPerc, otherEffects, isEquipped);
        this.weaponCategory = weaponCategory;
        this.properties = properties;
    }
    public Weapon(@NotNull final String weaponName) throws SQLException {
        super(weaponName);
        String query = "SELECT * FROM weapons WHERE equipment_id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("The database is not connected");
        assert getEquipmentID() != null;
        ps.setInt(1, getEquipmentID());
        ResultSet result = ps.executeQuery();
        if (result.next()) {
            this.weaponID = result.getInt("id");
            this.weaponCategory = result.getString("category");
            this.properties = result.getString("properties");
            ps.close();
        } else {
            ps.close();
            throw new SQLException("Exist the equipment, but not the weapon");
        }
    }
    public Weapon(@NotNull final JSONObject weaponStructure) throws JSONException {
        super(weaponStructure);
        try {
            this.weaponCategory = weaponStructure.getString("weaponCategory");
        } catch (JSONException e) {
            this.weaponCategory = null;
        }
        try {
            this.properties = weaponStructure.getString("properties");
        } catch (JSONException e) {
            this.properties = null;
        }
    }

    // Methods
    @Override @SuppressWarnings("DuplicatedCode")
    public JSONObject exportElementJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SERIALIZER_ID, SerializerType.WEAPON.ordinal());
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
        jsonObject.put("type", getType().getDatabaseValue());
        jsonObject.put("lifeEffect", getLifeEffect());
        jsonObject.put("lifePercentageEffect", getLifePercentageEffect());
        jsonObject.put("caEffect", getCaEffect());
        jsonObject.put("loadEffect", getLoadEffect());
        jsonObject.put("loadPercentageEffect", getLoadPercentageEffect());
        jsonObject.put("otherEffects", getOtherEffects());
        jsonObject.put("weaponCategory", weaponCategory);
        jsonObject.put("properties", properties);
        return jsonObject;
    }
    @Override @SuppressWarnings("DuplicatedCode")
    public String exportElement() {
        return Base64.getEncoder().encodeToString(exportElementJSON().toString().getBytes(StandardCharsets.UTF_8));
    }
    @Override
    public void saveIntoDatabase(@Nullable final String oldName) throws SQLException {
        super.saveIntoDatabase(oldName);
        Integer equipmentID = getEquipmentID();
        assert equipmentID != null;
        if (weaponID == null) { // Insert
            String query = "INSERT INTO weapons (equipment_id, category, properties) VALUES (?, ?, ?);";
            PreparedStatement ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database is not connected");
            ps.setInt(1, equipmentID);
            ps.setString(2, getWeaponCategory());
            ps.setString(3, getProperties());
            ps.executeUpdate();
            ps.close();
            query = "SELECT id FROM weapons WHERE equipment_id = ?;";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database is not connected");
            ps.setInt(1, equipmentID);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                setWeaponID(resultSet.getInt("id"));
                ps.close();
            } else {
                ps.close();
                throw new SQLException("Something strange happened on weapon insert! Weapon insert but doesn't result on select");
            }
        } else { // Update
            String query = "UPDATE weapons SET category=?, properties=? WHERE id=?;";
            PreparedStatement ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database is not connected");
            ps.setString(1, getWeaponCategory());
            ps.setString(2, getProperties());
            ps.setInt(3, getWeaponID());
            ps.executeUpdate();
            ps.close();
        }
    }
    @Nullable
    public Integer getWeaponID() {
        return weaponID;
    }
    public void setWeaponID(final int weaponID) {
        if (this.weaponID == null) this.weaponID = weaponID;
    }
    @Nullable
    public String getWeaponCategory() {
        return weaponCategory;
    }
    public void setWeaponCategory(@Nullable final String weaponCategory) {
        this.weaponCategory = weaponCategory;
    }
    @Nullable
    public String getProperties() {
        return properties;
    }
    public void setProperties(@Nullable final String properties) {
        this.properties = properties;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weapon)) return false;
        if (!super.equals(o)) return false;

        Weapon weapon = (Weapon) o;

        if (getWeaponID() != null ? !getWeaponID().equals(weapon.getWeaponID()) : weapon.getWeaponID() != null)
            return false;
        if (getWeaponCategory() != null ? !getWeaponCategory().equals(weapon.getWeaponCategory()) : weapon.getWeaponCategory() != null)
            return false;
        return getProperties() != null ? getProperties().equals(weapon.getProperties()) : weapon.getProperties() == null;
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getWeaponID() != null ? getWeaponID().hashCode() : 0);
        result = 31 * result + (getWeaponCategory() != null ? getWeaponCategory().hashCode() : 0);
        result = 31 * result + (getProperties() != null ? getProperties().hashCode() : 0);
        return result;
    }
}
