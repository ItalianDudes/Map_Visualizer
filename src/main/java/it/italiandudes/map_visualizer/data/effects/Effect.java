package it.italiandudes.dnd_visualizer.data.effect;

import it.italiandudes.dnd_visualizer.data.enums.EffectKnowledge;
import it.italiandudes.dnd_visualizer.db.DBManager;
import it.italiandudes.dnd_visualizer.interfaces.ISavable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@SuppressWarnings("unused")
public class Effect implements ISavable {

    // Attributes
    private Integer id = null;
    @NotNull private String name;
    @Nullable private String duration;
    private int intensity;
    @NotNull private EffectKnowledge isTreatable;
    @NotNull private EffectKnowledge isCurable;
    @NotNull private EffectKnowledge isLethal;
    private int lifeEffect;
    private double lifePercentageEffect;
    private int caEffect;
    private int loadEffect;
    private double loadPercentageEffect;
    @Nullable private String otherEffects;
    @Nullable private String description;
    private boolean isActive;

    // Constructors
    public Effect(@Nullable final Integer id, @NotNull final String name, @Nullable final String duration,
                  final int intensity, @NotNull final EffectKnowledge isTreatable,
                  @NotNull final EffectKnowledge isCurable, @NotNull final EffectKnowledge isLethal,
                  final int lifeEffect, final double lifePercentageEffect, final int caEffect,
                  final int loadEffect, final double loadPercentageEffect, @Nullable final String otherEffects,
                  @Nullable final String description, final boolean isActive) {
        this.name = name;
        this.duration = duration;
        this.intensity = intensity;
        this.isTreatable = isTreatable;
        this.isCurable = isCurable;
        this.isLethal = isLethal;
        this.lifeEffect = lifeEffect;
        this.lifePercentageEffect = lifePercentageEffect;
        this.caEffect = caEffect;
        this.loadEffect = loadEffect;
        this.loadPercentageEffect = loadPercentageEffect;
        this.otherEffects = otherEffects;
        this.description = description;
        this.isActive = isActive;
    }
    public Effect(@NotNull final String name) throws SQLException {
        String query = "SELECT * FROM effects WHERE name=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("The database connection is not initialized");
        ps.setString(1, name);
        ResultSet result = ps.executeQuery();
        this.name = name;
        this.id = result.getInt("id");
        this.duration = result.getString("duration");
        this.intensity = result.getInt("intensity");
        this.isTreatable = EffectKnowledge.values()[result.getInt("is_treatable")+1];
        this.isCurable = EffectKnowledge.values()[result.getInt("is_curable")+1];
        this.isLethal = EffectKnowledge.values()[result.getInt("is_lethal")+1];
        this.lifeEffect = result.getInt("life_effect");
        this.lifePercentageEffect = result.getDouble("life_percentage_effect");
        this.caEffect = result.getInt("ca_effect");
        this.loadEffect = result.getInt("load_effect");
        this.loadPercentageEffect = result.getDouble("load_percentage_effect");
        this.otherEffects = result.getString("other_effects");
        this.description = result.getString("description");
        this.isActive = result.getInt("is_active")!=0;
        ps.close();
    }

    // Methods
    @SuppressWarnings("DuplicatedCode")
    public static boolean checkIfExist(@NotNull final String name) throws SQLException {
        String query = "SELECT id FROM effects WHERE name=?;";
        PreparedStatement ps = DBManager.preparedStatement(query);
        if (ps == null) throw new SQLException("There's no connection with the database");
        ps.setString(1, name);
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
    @Override
    public void saveIntoDatabase(@Nullable final String oldName) throws SQLException {
        String noteCheckerQuery = "SELECT id FROM effects WHERE name=?;";
        PreparedStatement ps = DBManager.preparedStatement(noteCheckerQuery);
        if (ps == null) throw new SQLException("The database connection doesn't exist");
        ps.setString(1, oldName);
        ResultSet result = ps.executeQuery();
        String query;
        int effectID;
        if (result.next()) { // Update
            effectID = result.getInt("id");
            ps.close();
            query = "UPDATE effects SET name=?, duration=?, intensity=?, is_treatable=?, is_curable=?, is_lethal=?, life_effect=?, life_percentage_effect=?, ca_effect=?, load_effect=?, load_percentage_effect=?, other_effects=?, description=?, is_active=? WHERE id=?;";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database connection doesn't exist");
            ps.setString(1, getName());
            ps.setString(2, getDuration());
            ps.setInt(3, getIntensity());
            ps.setInt(4, isTreatable.getDatabaseValue());
            ps.setInt(5, isCurable.getDatabaseValue());
            ps.setInt(6, isLethal.getDatabaseValue());
            ps.setInt(7, getLifeEffect());
            ps.setDouble(8, getLifePercentageEffect());
            ps.setInt(9, getCaEffect());
            ps.setInt(10, getLoadEffect());
            ps.setDouble(11, getLoadPercentageEffect());
            ps.setString(12, getOtherEffects());
            ps.setString(13, getDescription());
            ps.setInt(14, (isActive()?1:0));
            ps.setInt(15, effectID);
            ps.executeUpdate();
            ps.close();
        } else { // Insert
            ps.close();
            query = "INSERT INTO effects (name, duration, intensity, is_treatable, is_curable, is_lethal, life_effect, life_percentage_effect, ca_effect, load_effect, load_percentage_effect, other_effects, description, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database connection doesn't exist");
            ps.setString(1, getName());
            ps.setString(2, getDuration());
            ps.setInt(3, getIntensity());
            ps.setInt(4, isTreatable.getDatabaseValue());
            ps.setInt(5, isCurable.getDatabaseValue());
            ps.setInt(6, isLethal.getDatabaseValue());
            ps.setInt(7, getLifeEffect());
            ps.setDouble(8, getLifePercentageEffect());
            ps.setInt(9, getCaEffect());
            ps.setInt(10, getLoadEffect());
            ps.setDouble(11, getLoadPercentageEffect());
            ps.setString(12, getOtherEffects());
            ps.setString(13, getDescription());
            ps.setInt(14, (isActive()?1:0));
            ps.executeUpdate();
            ps.close();
            query = "SELECT id FROM effects WHERE name=?;";
            ps = DBManager.preparedStatement(query);
            if (ps == null) throw new SQLException("The database connection doesn't exist");
            ps.setString(1, getName());
            result = ps.executeQuery();
            if (result.next()) {
                setId(result.getInt("id"));
                ps.close();
            } else {
                ps.close();
                throw new SQLException("Something strange happened on effect insert! Note insert but doesn't result on select");
            }
        }
    }
    public Integer getId() {
        return id;
    }
    public void setId(@NotNull final Integer id) {
        if (this.id != null) return;
        this.id = id;
    }
    @NotNull
    public String getName() {
        return name;
    }
    public void setName(@NotNull final String name) {
        this.name = name;
    }
    @Nullable
    public String getDuration() {
        return duration;
    }
    public void setDuration(@Nullable final String duration) {
        this.duration = duration;
    }
    public int getIntensity() {
        return intensity;
    }
    public void setIntensity(final int intensity) {
        this.intensity = intensity;
    }
    @NotNull
    public EffectKnowledge getIsTreatable() {
        return isTreatable;
    }
    public void setIsTreatable(@NotNull final EffectKnowledge isTreatable) {
        this.isTreatable = isTreatable;
    }
    @NotNull
    public EffectKnowledge getIsCurable() {
        return isCurable;
    }
    public void setIsCurable(@NotNull final EffectKnowledge isCurable) {
        this.isCurable = isCurable;
    }
    @NotNull
    public EffectKnowledge getIsLethal() {
        return isLethal;
    }
    public void setIsLethal(@NotNull final EffectKnowledge isLethal) {
        this.isLethal = isLethal;
    }

    public int getLifeEffect() {
        return lifeEffect;
    }
    public void setLifeEffect(final int lifeEffect) {
        this.lifeEffect = lifeEffect;
    }
    public double getLifePercentageEffect() {
        return lifePercentageEffect;
    }
    public void setLifePercentageEffect(final double lifePercentageEffect) {
        this.lifePercentageEffect = lifePercentageEffect;
    }
    public int getCaEffect() {
        return caEffect;
    }
    public void setCaEffect(final int caEffect) {
        this.caEffect = caEffect;
    }
    public int getLoadEffect() {
        return loadEffect;
    }
    public void setLoadEffect(final int loadEffect) {
        this.loadEffect = loadEffect;
    }
    public double getLoadPercentageEffect() {
        return loadPercentageEffect;
    }
    public void setLoadPercentageEffect(final double loadPercentageEffect) {
        this.loadPercentageEffect = loadPercentageEffect;
    }
    @Nullable
    public String getOtherEffects() {
        return otherEffects;
    }
    public void setOtherEffects(@Nullable final String otherEffects) {
        this.otherEffects = otherEffects;
    }
    @Nullable
    public String getDescription() {
        return description;
    }
    public void setDescription(@Nullable final String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(final boolean active) {
        isActive = active;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Effect)) return false;
        Effect effect = (Effect) o;
        return getIntensity() == effect.getIntensity() && getLifeEffect() == effect.getLifeEffect() && Double.compare(getLifePercentageEffect(), effect.getLifePercentageEffect()) == 0 && getCaEffect() == effect.getCaEffect() && getLoadEffect() == effect.getLoadEffect() && Double.compare(getLoadPercentageEffect(), effect.getLoadPercentageEffect()) == 0 && isActive() == effect.isActive() && Objects.equals(getId(), effect.getId()) && Objects.equals(getName(), effect.getName()) && Objects.equals(getDuration(), effect.getDuration()) && getIsTreatable() == effect.getIsTreatable() && getIsCurable() == effect.getIsCurable() && getIsLethal() == effect.getIsLethal() && Objects.equals(getOtherEffects(), effect.getOtherEffects()) && Objects.equals(getDescription(), effect.getDescription());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDuration(), getIntensity(), getIsTreatable(), getIsCurable(), getIsLethal(), getLifeEffect(), getLifePercentageEffect(), getCaEffect(), getLoadEffect(), getLoadPercentageEffect(), getOtherEffects(), getDescription(), isActive());
    }
    @Override
    public String toString() {
        return name;
    }
}
