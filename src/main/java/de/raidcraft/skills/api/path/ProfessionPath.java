package de.raidcraft.skills.api.path;

import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.config.PathConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class ProfessionPath implements Path<Profession> {

    private final String name;
    private final PathConfig config;

    public ProfessionPath(PathConfig config, String name) {

        this.name = name;
        this.config = config;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getFriendlyName() {

        return config.getString(getName() + ".name");
    }

    @Override
    public List<Profession> getParents(Hero hero) {

        ArrayList<Profession> professions = new ArrayList<>();
        for (String key : config.getStringList(getName() + ".parents")) {
            try {
                professions.add(config.getPlugin().getProfessionManager().getProfession(hero, key));
            } catch (UnknownSkillException | UnknownProfessionException e) {
                config.getPlugin().getLogger().severe(e.getMessage());
            }
        }
        return professions;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProfessionPath that = (ProfessionPath) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {

        return name.hashCode();
    }
}
