package de.raidcraft.skills.config;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.DataMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author Silthus
 */
public class SkillConfig extends YamlConfiguration implements SkillData {

    public static final String CONFIG_NAME = "skills.yml";

    private final SkillsPlugin plugin;
    private final Hero hero;

    public SkillConfig(SkillsPlugin plugin, Hero hero, String skillName) {

        this(plugin, hero, skillName, null);
    }

    public SkillConfig(SkillsPlugin plugin, Hero hero, String skillName, ProfessionConfig config) {

        this.plugin = plugin;
        this.hero = hero;
        // load the global skill config - values in it are overriden by the profession config
        File file = new File(plugin.getDataFolder(), CONFIG_NAME);
        try {
            if (!file.exists()) file.createNewFile();
            // load the actual file
            load(file);
            // lets check if we need to create defaults
            ConfigurationSection section = getConfigurationSection(skillName);
            if (section == null) {
                // we need to create defaults

            }
        } catch (IOException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().warning(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getFriendlyName() {
        //TODO: implement
    }

    @Override
    public String getDescription() {
        //TODO: implement
    }

    @Override
    public String[] getUsage() {
        //TODO: implement
    }

    @Override
    public double getCost() {
        //TODO: implement
    }

    @Override
    public Profession getProfession() {
        //TODO: implement
    }

    @Override
    public DataMap getData() {
        //TODO: implement
    }

    @Override
    public int getManaCost() {
        //TODO: implement
    }

    @Override
    public double getManaLevelModifier() {
        //TODO: implement
    }

    @Override
    public int getStaminaCost() {
        //TODO: implement
    }

    @Override
    public double getStaminaLevelModifier() {
        //TODO: implement
    }

    @Override
    public int getHealthCost() {
        //TODO: implement
    }

    @Override
    public double getHealthLevelModifier() {
        //TODO: implement
    }

    @Override
    public int getRequiredLevel() {
        //TODO: implement
    }

    @Override
    public int getDamage() {
        //TODO: implement
    }

    @Override
    public double getDamageLevelModifier() {
        //TODO: implement
    }

    @Override
    public double getCastTime() {
        //TODO: implement
    }

    @Override
    public double getCastTimeLevelModifier() {
        //TODO: implement
    }

    @Override
    public double getDuration() {
        //TODO: implement
    }

    @Override
    public double getDurationLevelModifier() {
        //TODO: implement
    }
}
