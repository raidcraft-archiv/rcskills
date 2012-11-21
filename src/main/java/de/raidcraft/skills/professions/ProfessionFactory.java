package de.raidcraft.skills.professions;

import de.raidcraft.api.database.Database;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownPlayerProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.tables.professions.ProfessionsTable;

/**
 * @author Silthus
 */
public final class ProfessionFactory {

    private final SkillsPlugin component;

    protected ProfessionFactory(SkillsPlugin component) {

        this.component = component;
    }

    public Profession load(String id) throws UnknownProfessionException {

        switch (component.getLocalConfiguration().config_type) {

            case MYSQL:
                return new TemplateProfession(Database.getTable(ProfessionsTable.class).getProfessionData(id));
            case YAML:
                return new TemplateProfession(component.getProfessionConfig().getProfessionData(id));
            case SQLITE:
            default:
                component.getLogger().warning("Unsupported Storage Type selected.");
        }
        return null;
    }

    public LevelableProfession load(String id, Hero player) throws UnknownProfessionException, UnknownPlayerProfessionException {

        switch (component.getLocalConfiguration().config_type) {

            case MYSQL:
                return new RCProfession(player, Database.getTable(ProfessionsTable.class).getProfessionData(id));
            case YAML:
                return new RCProfession(player, component.getProfessionConfig().getProfessionData(id));
            case SQLITE:
            default:
                component.getLogger().warning("Unsupported Storage Type selected.");
        }
        return null;
    }
}
