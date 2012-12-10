package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.Equipment;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.professions.SimpleProfession;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public final class ProfessionFactory extends DefaultConfiguration implements ProfessionProperties {

    private final SkillsPlugin plugin;
    private final String name;

    protected ProfessionFactory(SkillsPlugin plugin, File file) {

        super(plugin, file);
        this.plugin = plugin;
        this.name = file.getName().toLowerCase().replace("profession", "").replace(".yml", "").trim();
    }

    @Override
    public void load() {

        super.load();
        plugin.getLogger().info("Profession loaded: " + name);
    }

    protected Profession create(Hero hero) throws UnknownSkillException {

        return new SimpleProfession(hero, this, loadDatabase(hero, name));
    }

    private THeroProfession loadDatabase(Hero hero, String name) {

        // then load the hero stats from the database
        THeroProfession database = Ebean.find(THeroProfession.class).where()
                .eq("name", name)
                .eq("hero_id", hero.getId()).findUnique();
        if (database == null) {
            // create a new entry
            database = new THeroProfession();
            database.setName(getName());
            database.setHero(Ebean.find(THero.class, hero.getId()));
            database.setLevel(1);
            database.setExp(0);
            database.setMastered(false);
            database.setActive(false);
            Ebean.save(database);
        }
        return database;
    }

    public List<Skill> loadSkills(Hero hero, Profession profession) {

        List<Skill> skills = new ArrayList<>();
        // now load the skills - when a skill does not exist in the database we will insert it
        for (String skill : getConfigurationSection("skills").getKeys(false)) {
            try {
                skills.add(plugin.getSkillManager().getSkill(hero, this, profession, skill));
            } catch (UnknownSkillException e) {
                plugin.getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
        }
        return skills;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getTag() {

        return getString("tag");
    }

    @Override
    public String getFriendlyName() {

        return getString("name");
    }

    @Override
    public String getDescription() {

        return getString("description");
    }

    @Override
    public int getMaxLevel() {

        return getInt("max-level", 60);
    }

    @Override
    public int getBaseHealth() {

        return getInt("health.base", 20);
    }

    @Override
    public double getBaseHealthModifier() {

        return getDouble("health.level-modifier", 0.0);
    }

    @Override
    public int getBaseMana() {

        return getInt("mana.base", 100);
    }

    @Override
    public double getBaseManaModifier() {

        return getDouble("mana.level-modifier", 0.0);
    }

    @Override
    public int getBaseStamina() {

        return getInt("stamina.base", 20);
    }

    @Override
    public double getBaseStaminaModifier() {

        return getDouble("stamina.level-modifier", 0.0);
    }

    @Override
    public boolean isPrimary() {

        return getBoolean("primary", false);
    }

    @Override
    public Set<Equipment> getEquipment() {

        Set<Equipment> equipment = new HashSet<>();
        ConfigurationSection section = getConfigurationSection("equipment");
        for (String key : section.getKeys(false)) {
            equipment.add(new Equipment(section.getConfigurationSection(key)));
        }
        return equipment;
    }
}
