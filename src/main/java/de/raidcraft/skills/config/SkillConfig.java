package de.raidcraft.skills.config;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.util.DataMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class SkillConfig extends YamlConfiguration implements SkillData, LevelData {

    public static final String CONFIG_NAME = "skills.yml";

    private final SkillsPlugin plugin;
    private final ConfigurationSection config;
    private final File file;
    private final SkillInformation information;
    private THeroSkill skill;

    public SkillConfig(SkillsPlugin plugin, Hero hero, SkillInformation info) {

        this(plugin, hero, info, null);
    }

    public SkillConfig(SkillsPlugin plugin, Hero hero, SkillInformation info, ProfessionConfig config) {

        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), CONFIG_NAME);
        this.information = info;
        String name = info.name();
        this.config = config.getConfigurationSection("skills." + name);
        // load the global skill config - values in it are overriden by the profession config
        try {
            if (!file.exists()) file.createNewFile();
            // load the actual file
            load(file);
            // lets check if we need to create defaults
            ConfigurationSection section = getConfigurationSection(name);
            if (section == null) {
                // yes we do so lets parse the defaults and go
                createSection(name, ConfigUtil.parseSkillDefaults(info.defaults()));
                getConfigurationSection(name).set("name", name);
                getConfigurationSection(name).set("description", info.desc());
                getConfigurationSection(name).set("usage", new ArrayList<String>());
                getConfigurationSection(name).set("strong-parents", new ArrayList<String>());
                getConfigurationSection(name).set("weak-parents", new ArrayList<String>());
                save();
            }
            // lets try the database now and create a new entry if none exists
            skill = Ebean.find(THeroSkill.class).where().eq("hero_id", hero.getId()).eq("name", name).findUnique();
            if (skill == null) {
                skill = new THeroSkill();
                skill.setActive(false);
                skill.setMastered(false);
                skill.setExp(0);
                skill.setLevel(0);
                skill.setHero(Ebean.find(THero.class, hero.getId()));
                skill.setProfession(Ebean.find(THeroProfession.class, config.getId()));
                Ebean.save(skill);
            }
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    public void save() {

        try {
            save(file);
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public SkillInformation getSkillInformation() {

        return information;
    }

    private <V> V getValue(String key, Class<V> vClass) {

        if (config != null) {
            if (config.isSet(key)) {
                return vClass.cast(config.get(key));
            }
        }
        if (!isSet(key)) {
            if (vClass == double.class) set(key, 0.0);
            if (vClass == int.class) set(key, 0);
            if (vClass == boolean.class) set(key, false);
            if (vClass == String.class) set(key, "default");
            save();
        }
        return vClass.cast(get(key));
    }

    private double getOverrideDouble(String key) {

        return getValue(key, double.class);
    }

    private int getOverrideInt(String key) {

        return getValue(key, int.class);
    }

    private boolean getOverrideBool(String key) {

        return getValue(key, boolean.class);
    }

    private String getOverrideString(String key) {

        return getValue(key, String.class);
    }

    @Override
    public int getId() {

        return skill.getId();
    }

    @Override
    public String getFriendlyName() {

        return getOverrideString("name");
    }

    @Override
    public String getDescription() {

        return getOverrideString("description");
    }

    @Override
    public String[] getUsage() {

        List<String> usage = getStringList("usage");
        return usage.toArray(new String[usage.size()]);
    }

    @Override
    public DataMap getData() {

        return new DataMap(config.getConfigurationSection("custom"));
    }

    @Override
    public String getProfession() {

        return skill.getProfession().getName();
    }

    @Override
    public int getManaCost() {

        return getOverrideInt("mana-cost");
    }

    @Override
    public double getManaLevelModifier() {

        return getOverrideDouble("mana-level-modifier");
    }

    @Override
    public int getStaminaCost() {

        return getOverrideInt("stamina-cost");
    }

    @Override
    public double getStaminaLevelModifier() {

        return getOverrideDouble("stamina-level-modifier");
    }

    @Override
    public int getHealthCost() {

        return getOverrideInt("health-cost");
    }

    @Override
    public double getHealthLevelModifier() {

        return getOverrideDouble("health-level-modifier");
    }

    @Override
    public int getRequiredLevel() {

        return getOverrideInt("level");
    }

    @Override
    public int getDamage() {

        return getOverrideInt("damage");
    }

    @Override
    public double getDamageLevelModifier() {

        return getOverrideDouble("damage-level-modifier");
    }

    @Override
    public double getCastTime() {

        return getOverrideDouble("cast-time");
    }

    @Override
    public double getCastTimeLevelModifier() {

        return getOverrideDouble("cast-time-modifier");
    }

    @Override
    public double getDuration() {

        return getOverrideDouble("duration");
    }

    @Override
    public double getDurationLevelModifier() {

        return getOverrideDouble("duration-level-modifier");
    }

    @Override
    public int getLevel() {

        return skill.getLevel();
    }

    @Override
    public int getExp() {

        return skill.getExp();
    }

    @Override
    public int getMaxLevel() {

        return getOverrideInt("max-level");
    }
}
