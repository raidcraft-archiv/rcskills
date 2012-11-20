package de.raidcraft.skills.professions;

import de.raidcraft.skills.SkillsComponent;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.profession.Profession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class ProfessionManager {

    private final SkillsComponent component;
    private final ProfessionFactory factory;
    private final Map<String, Profession> professions = new HashMap<>();

    public ProfessionManager(SkillsComponent component) {

        this.component = component;
        this.factory = new ProfessionFactory(component);
    }

    public Profession getProfession(String name) throws UnknownProfessionException {

        if (professions.containsKey(name)) {
            return professions.get(name);
        } else {
            Profession profession = factory.load(name);
            professions.put(profession.getName(), profession);
            return profession;
        }
    }
}
