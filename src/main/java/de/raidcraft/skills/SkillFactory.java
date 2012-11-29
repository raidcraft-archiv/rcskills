package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.config.ConfigUtil;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.util.DataMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public final class SkillFactory extends YamlConfiguration implements SkillProperties {

    public static final String CONFIG_NAME = "skills.yml";

    private final SkillsPlugin plugin;
    private final Class<? extends Skill> sClass;
    private final SkillInformation information;
    private final File file;

    private ConfigurationSection professionConfig;

    /**
     * This only creates a fake skill and creates the defaults for it.
     *
     * @param plugin
     */
    protected SkillFactory(SkillsPlugin plugin, Class<? extends Skill> sClass) {

        this.plugin = plugin;
        this.sClass = sClass;
        this.information = sClass.getAnnotation(SkillInformation.class);
        this.file = new File(plugin.getDataFolder(), CONFIG_NAME);
        // load the global skill config - values in it are overriden by the profession config
        loadFile();
        plugin.getLogger().info("Skill loaded: " + information.name());
    }

    protected Skill create(Hero hero, ProfessionFactory factory) throws UnknownSkillException {

        // set the config that overrides the default skill parameters with the profession config
        this.professionConfig = factory.getConfigurationSection("skills." + information.name());
        // lets load the database
        THeroSkill database = loadDatabase(hero, factory);

        // its reflection time yay!
        try {
            Constructor<? extends Skill> constructor = sClass.getConstructor(Hero.class, SkillProperties.class, THeroSkill.class);
            constructor.setAccessible(true);
            return constructor.newInstance(hero, factory, database);
        } catch (NoSuchMethodException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (InstantiationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownSkillException("Error when loading skill for class: " + sClass.getCanonicalName());
    }

    private THeroSkill loadDatabase(Hero hero, ProfessionFactory factory) {

        THeroSkill database = Ebean.find(THeroSkill.class).where().eq("hero_id", hero.getId()).eq("name", information.name()).findUnique();
        if (database == null) {
            database = new THeroSkill();
            database.setUnlocked(false);
            database.setExp(0);
            database.setLevel(0);
            database.setHero(Ebean.find(THero.class, hero.getId()));
            database.setProfession(Ebean.find(THeroProfession.class).where().eq("name", factory.getName()).findUnique());
            Ebean.save(database);
        }
        return database;
    }

    private void loadFile() {

        try {
            String name = information.name();
            if (!file.exists()) file.createNewFile();
            // load the actual file
            load(file);
            // lets check if we need to create defaults
            ConfigurationSection section = getConfigurationSection(name);
            if (section == null) {
                createSection(name);
                section = getConfigurationSection(name);
                // yes we do so lets parse the defaults and go
                section.set("name", name);
                section.set("description", information.desc());
                section.set("usage", new ArrayList<String>());
                section.set("strong-parents", new ArrayList<String>());
                section.set("weak-parents", new ArrayList<String>());
                section.createSection("custom", ConfigUtil.parseSkillDefaults(information.defaults()));
                save();
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
    public SkillInformation getInformation() {

        return information;
    }

    private <V> V getValue(String key, Class<V> vClass, V def) {

        if (professionConfig != null) {
            if (professionConfig.isSet(key)) {
                return vClass.cast(professionConfig.get(key));
            }
        }
        if (!isSet(key)) {
            if (vClass == double.class) set(key, def);
            if (vClass == int.class) set(key, def);
            if (vClass == boolean.class) set(key, def);
            if (vClass == String.class) set(key, def);
            save();
        }
        return vClass.cast(get(key, def));
    }

    private double getOverrideDouble(String key, double def) {

        return getValue(key, double.class, def);
    }

    private int getOverrideInt(String key, int def) {

        return getValue(key, int.class, def);
    }

    private boolean getOverrideBool(String key, boolean def) {

        return getValue(key, boolean.class, def);
    }

    private String getOverrideString(String key, String def) {

        return getValue(key, String.class, def);
    }

    @Override
    public String getFriendlyName() {

        return getOverrideString("name", information.name());
    }

    @Override
    public String getDescription() {

        return getOverrideString("description", information.desc());
    }

    @Override
    public String[] getUsage() {

        List<String> usage = getStringList("usage");
        return usage.toArray(new String[usage.size()]);
    }

    @Override
    public Skill.Type[] getSkillTypes() {

        return information.types();
    }

    @Override
    public DataMap getData() {

        return new DataMap(professionConfig.getConfigurationSection("custom"));
    }

    @Override
    public int getManaCost() {

        return getOverrideInt("mana-cost", 0);
    }

    @Override
    public double getManaLevelModifier() {

        return getOverrideDouble("mana-level-modifier", 0);
    }

    @Override
    public int getStaminaCost() {

        return getOverrideInt("stamina-cost", 0);
    }

    @Override
    public double getStaminaLevelModifier() {

        return getOverrideDouble("stamina-level-modifier", 0);
    }

    @Override
    public int getHealthCost() {

        return getOverrideInt("health-cost", 0);
    }

    @Override
    public double getHealthLevelModifier() {

        return getOverrideDouble("health-level-modifier", 0);
    }

    @Override
    public int getRequiredLevel() {

        return getOverrideInt("level", 1);
    }

    @Override
    public int getDamage() {

        return getOverrideInt("damage", 0);
    }

    @Override
    public double getDamageLevelModifier() {

        return getOverrideDouble("damage-level-modifier", 0);
    }

    @Override
    public double getCastTime() {

        return getOverrideDouble("cast-time", 0);
    }

    @Override
    public double getCastTimeLevelModifier() {

        return getOverrideDouble("cast-time-modifier", 0);
    }

    @Override
    public double getDuration() {

        return getOverrideDouble("duration", 0);
    }

    @Override
    public double getDurationLevelModifier() {

        return getOverrideDouble("duration-level-modifier", 0);
    }

    @Override
    public int getMaxLevel() {

        return getOverrideInt("max-level", 10);
    }

    @Override
    public double getSkillLevelDamageModifier() {

        return getOverrideDouble("skill-level-damage-modifier", 0);
    }

    @Override
    public double getSkillLevelManaCostModifier() {

        return getOverrideDouble("skill-level-mana-modifier", 0);
    }

    @Override
    public double getSkillLevelStaminaCostModifier() {

        return getOverrideDouble("skill-level-stamina-modifier", 0);
    }

    @Override
    public double getSkillLevelHealthCostModifier() {

        return getOverrideDouble("skill-level-health-modifier", 0);
    }

    @Override
    public double getSkillLevelCastTimeModifier() {

        return getOverrideDouble("skill-level-casttime-modifier", 0);
    }

    @Override
    public double getSkillLevelDurationModifier() {

        return getOverrideDouble("skill-level-duration-modifier", 0);
    }

    @Override
    public double getProfLevelDamageModifier() {

        return getOverrideDouble("prof-level-damage-modifier", 0);
    }

    @Override
    public double getProfLevelManaCostModifier() {

        return getOverrideDouble("prof-level-mana-modifier", 0);
    }

    @Override
    public double getProfLevelStaminaCostModifier() {

        return getOverrideDouble("prof-level-stamina-modifier", 0);
    }

    @Override
    public double getProfLevelHealthCostModifier() {

        return getOverrideDouble("prof-level-health-modifier", 0);
    }

    @Override
    public double getProfLevelCastTimeModifier() {

        return getOverrideDouble("prof-level-casttime-modifier", 0);
    }

    @Override
    public double getProfLevelDurationModifier() {

        return getOverrideDouble("prof-level-duration-modifier", 0);
    }
}
