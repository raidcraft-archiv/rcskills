package de.raidcraft.skills;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.config.ProfessionConfig;
import de.raidcraft.skills.professions.SimpleProfession;
import de.raidcraft.skills.professions.VirtualProfession;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroProfession;

/**
 * @author Silthus
 */
public final class ProfessionFactory {

    private final SkillsPlugin plugin;
    private final String professionName;
    private final ProfessionConfig config;

    protected ProfessionFactory(SkillsPlugin plugin, String professionName) {

        this.plugin = plugin;
        this.professionName = professionName;
        this.config = plugin.configure(new ProfessionConfig(this));
    }

    protected Profession create(Hero hero) throws UnknownSkillException {

        if (professionName.equals(ProfessionManager.VIRTUAL_PROFESSION)) {
            return new VirtualProfession(hero, config, loadDatabase(hero, professionName));
        }
        return new SimpleProfession(hero, config, loadDatabase(hero, professionName));
    }

    private THeroProfession loadDatabase(Hero hero, String name) {

        // then load the hero stats from the database
        THeroProfession database = Ebean.find(THeroProfession.class).where()
                .eq("name", name)
                .eq("hero_id", hero.getId()).findUnique();

        if (database == null) {
            // create a new entry
            database = new THeroProfession();
            database.setName(getProfessionName());
            database.setHero(Ebean.find(THero.class, hero.getId()));
            database.setLevel(1);
            database.setExp(0);
            database.setActive(false);
            Database.save(database);
        }
        return database;
    }

    public SkillsPlugin getPlugin() {

        return plugin;
    }

    public String getProfessionName() {

        return professionName;
    }

    public ProfessionConfig getConfig() {

        return config;
    }
}
