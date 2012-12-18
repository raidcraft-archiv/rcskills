package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.skills.util.ConfigUtil;
import de.raidcraft.util.DataMap;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public final class SkillFactory extends ConfigurationBase implements SkillProperties {

    private final SkillsPlugin plugin;
    private final Class<? extends Skill> sClass;
    private final SkillInformation information;
    private boolean createDefaults = false;

    protected SkillFactory(SkillsPlugin plugin, Class<? extends Skill> sClass, File configDir) {

        super(plugin, new File(configDir, sClass.getAnnotation(SkillInformation.class).name().toLowerCase() + ".yml"));
        this.plugin = plugin;
        this.sClass = sClass;
        this.information = sClass.getAnnotation(SkillInformation.class);
        this.createDefaults = !getFile().exists();
    }

    @Override
    public void load() {

        super.load();
        if (createDefaults) {
            createDefaults();
            createDefaults = false;
        }
        plugin.getLogger().info("Skill loaded: " + information.name().toLowerCase());
    }

    private void createDefaults() {

        String name = information.name().toLowerCase();

        if (createDefaults) {
            // yes we do so lets parse the defaults and go
            set("name", name);
            set("description", information.desc());
            set("usage", new ArrayList<String>());
            set("strong-parents", new ArrayList<String>());
            set("weak-parents", new ArrayList<String>());
            createSection("custom", ConfigUtil.parseSkillDefaults(information.defaults()));
            save();
        }
    }

    protected Skill create(Hero hero, Profession profession, ProfessionFactory factory) throws UnknownSkillException {

        // set the config that overrides the default skill parameters with the profession config
        setOverrideConfig(factory.getConfigurationSection("skills." + information.name().toLowerCase()));

        if (getOverrideConfig() == null) {
            setOverrideConfig(factory.createSection("skills." + information.name().toLowerCase()));
        }
        // lets load the database
        THeroSkill database = loadDatabase(hero, factory);

        // its reflection time yay!
        try {
            Constructor<? extends Skill> constructor =
                    sClass.getConstructor(Hero.class, SkillProperties.class, Profession.class, THeroSkill.class);
            constructor.setAccessible(true);
            return constructor.newInstance(hero, this, profession, database);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownSkillException("Error when loading skill for class: " + sClass.getCanonicalName());
    }

    private THeroSkill loadDatabase(Hero hero, ProfessionFactory factory) {

        THeroSkill database = Ebean.find(THeroSkill.class).where()
                .eq("hero_id", hero.getId())
                .eq("name", information.name().toLowerCase()).findUnique();
        if (database == null) {
            database = new THeroSkill();
            database.setName(getName());
            database.setUnlocked(false);
            database.setExp(0);
            database.setLevel(0);
            database.setHero(Ebean.find(THero.class, hero.getId()));
            database.setProfession(Ebean.find(THeroProfession.class).where()
                    .eq("name", factory.getName())
                    .eq("hero_id", hero.getId()).findUnique());
        }
        return database;
    }

    @Override
    public String getName() {

        return information.name().toLowerCase();
    }

    @Override
    public SkillInformation getInformation() {

        return information;
    }

    @Override
    public String getFriendlyName() {

        return getOverride("name", information.name());
    }

    @Override
    public String getDescription() {

        return getOverride("description", information.desc());
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
    public ItemStack[] getReagents() {

        ConfigurationSection section = getOverrideSection("reagents");
        Set<String> keys = section.getKeys(false);
        ItemStack[] reagents = new ItemStack[keys.size()];
        int i = 0;
        for (String key : keys) {
            Material material;
            try {
                material = Material.getMaterial(Integer.parseInt(key));
            } catch (NumberFormatException e) {
                material = Material.getMaterial(key);
            }
            if (material == null) {
                plugin.getLogger().warning("Item " + key + " is non existant in bukkit! Skill: " + getName());
            }
            reagents[i] = new ItemStack(material, section.getInt(key));
        }
        return reagents;
    }

    @Override
    public DataMap getData() {

        return new DataMap(getOverrideSection("custom"));
    }

    @Override
    public int getManaCost() {

        return getOverride("mana.base-cost", 0);
    }

    @Override
    public double getManaLevelModifier() {

        return getOverride("mana.level-modifier", 0.0);
    }

    @Override
    public int getStaminaCost() {

        return getOverride("stamina.base-cost", 0);
    }

    @Override
    public double getStaminaLevelModifier() {

        return getOverride("stamina.level-modifier", 0.0);
    }

    @Override
    public int getHealthCost() {

        return getOverride("health.base-cost", 0);
    }

    @Override
    public double getHealthLevelModifier() {

        return getOverride("health.level-modifier", 0.0);
    }

    @Override
    public int getRequiredLevel() {

        return getOverride("level", 1);
    }

    @Override
    public int getDamage() {

        return getOverride("damage.base", 0);
    }

    @Override
    public double getDamageLevelModifier() {

        return getOverride("damage.level-modifier", 0.0);
    }

    @Override
    public int getCastTime() {

        return getOverride("casttime.base", 0);
    }

    @Override
    public double getCastTimeLevelModifier() {

        return getOverride("casttime.level-modifier", 0.0);
    }

    @Override
    public int getMaxLevel() {

        return getOverride("max-level", 10);
    }

    @Override
    public double getSkillLevelDamageModifier() {

        return getOverride("damage.skill-level-modifier", 0.0);
    }

    @Override
    public double getSkillLevelManaCostModifier() {

        return getOverride("mana.skill-level-modifier", 0.0);
    }

    @Override
    public double getSkillLevelStaminaCostModifier() {

        return getOverride("stamina.skill-level-modifier", 0.0);
    }

    @Override
    public double getSkillLevelHealthCostModifier() {

        return getOverride("health.skill-level-modifier", 0.0);
    }

    @Override
    public double getSkillLevelCastTimeModifier() {

        return getOverride("casttime.skill-level-modifier", 0.0);
    }

    @Override
    public double getProfLevelDamageModifier() {

        return getOverride("damage.prof-level-modifier", 0.0);
    }

    @Override
    public double getProfLevelManaCostModifier() {

        return getOverride("mana.prof-level-modifier", 0.0);
    }

    @Override
    public double getProfLevelStaminaCostModifier() {

        return getOverride("stamina.prof-level-modifier", 0.0);
    }

    @Override
    public double getProfLevelHealthCostModifier() {

        return getOverride("health.prof-level-modifier", 0.0);
    }

    @Override
    public double getProfLevelCastTimeModifier() {

        return getOverride("casttime.prof-level-modifier", 0.0);
    }
}
