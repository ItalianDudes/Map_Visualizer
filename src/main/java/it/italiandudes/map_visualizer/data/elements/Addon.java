package it.italiandudes.map_visualizer.data.elements;

import it.italiandudes.map_visualizer.master.interfaces.ISavable;
import it.italiandudes.map_visualizer.master.interfaces.ISerializable;
import it.italiandudes.map_visualizer.master.javafx.components.Waypoint;
import it.italiandudes.map_visualizer.master.utils.DBManager;
import it.italiandudes.map_visualizer.data.enums.AddonSlot;
import it.italiandudes.map_visualizer.data.enums.EquipmentType;
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

@SuppressWarnings("unused")
public class Addon extends Equipment implements ISavable, ISerializable {

    // Attributes
    @Nullable private Integer addonID;
    @NotNull private AddonSlot slot;

    public Addon(@NotNull final AddonSlot slot, @NotNull final Waypoint waypoint) {
        super(EquipmentType.ADDON, waypoint);
        this.slot = slot;
    }
    public Addon(@NotNull final Item item, final int equipmentID, final int addonID, @NotNull final AddonSlot slot,
                 final int lifeEffect, final double lifeEffectPerc, final int loadEffect, final double loadEffectPerc,
                 final int caEffect, @Nullable final String otherEffects, final boolean isEquipped) {
        super(item, EquipmentType.ADDON, lifeEffect, lifeEffectPerc, caEffect, loadEffect, loadEffectPerc, otherEffects, isEquipped);
        this.slot = slot;
        this.setEquipmentID(equipmentID);
        this.addonID = addonID;
    }
    public Addon(@NotNull final Item item, @NotNull final AddonSlot slot, final int lifeEffect, final double lifeEffectPerc,
                 final int loadEffect, final double loadEffectPerc, final int caEffect, @Nullable final String otherEffects,
                 final boolean isEquipped) {
        super(item, EquipmentType.ADDON, lifeEffect, lifeEffectPerc, caEffect, loadEffect, loadEffectPerc, otherEffects, isEquipped);
        this.slot = slot;
    }
    public Addon(@NotNull final String addonName) throws SQLException {
        super(addonName);
        String query = "SELECT * FROM addons WHERE equipment_id=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("The database is not connected");
        assert getEquipmentID()!=null;
        ps.setInt(1, getEquipmentID());
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            this.addonID = resultSet.getInt("id");
            this.slot = AddonSlot.values()[resultSet.getInt("slot")+1];
            ps.close();
        } else {
            ps.close();
            throw new SQLException("Exist the equipment, but not the addon");
        }
    }

    // Methods
    @Override @SuppressWarnings("DuplicatedCode")
    public JSONObject exportElementJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SERIALIZER_ID, SerializerType.ADDON.ordinal());
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
        jsonObject.put("slot", slot.getDatabaseValue());
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
        if (addonID == null) { // Insert
            String query = "INSERT INTO addons (equipment_id, slot) VALUES (?, ?);";
            PreparedStatement ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database is not connected");
            ps.setInt(1, equipmentID);
            ps.setInt(2, slot.getDatabaseValue());
            ps.executeUpdate();
            ps.close();
            query = "SELECT id FROM addons WHERE equipment_id=?;";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database is not connected");
            ps.setInt(1, equipmentID);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                setAddonID(resultSet.getInt("id"));
                ps.close();
            } else {
                ps.close();
                throw new SQLException("Something strange happened on addon insert! Addon insert but doesn't result on select");
            }
        } else { // Update
            String query = "UPDATE addons SET slot=? WHERE id=?;";
            PreparedStatement ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database is not connected");
            ps.setInt(1, slot.getDatabaseValue());
            ps.setInt(2, addonID);
            ps.executeUpdate();
            ps.close();
        }
    }
    @Nullable
    public Integer getAddonID() {
        return addonID;
    }

    public void setAddonID(final int addonID) {
        if (this.addonID == null) this.addonID = addonID;
    }
    @NotNull
    public AddonSlot getSlot() {
        return slot;
    }
    public void setSlot(@NotNull final AddonSlot slot) {
        this.slot = slot;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Addon)) return false;
        if (!super.equals(o)) return false;

        Addon addon = (Addon) o;

        if (isEquipped() != addon.isEquipped()) return false;
        if (getAddonID() != null ? !getAddonID().equals(addon.getAddonID()) : addon.getAddonID() != null) return false;
        return getSlot() == addon.getSlot();
    }
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getAddonID() != null ? getAddonID().hashCode() : 0);
        result = 31 * result + getSlot().hashCode();
        result = 31 * result + (isEquipped() ? 1 : 0);
        return result;
    }
}
