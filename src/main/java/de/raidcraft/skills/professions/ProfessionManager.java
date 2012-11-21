package de.raidcraft.skills.professions;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.profession.Profession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class ProfessionManager {

    private final SkillsPlugin component;
    private final ProfessionFactory factory;
    private final Map<String, Profession> professions = new HashMap<>();

    public ProfessionManager(SkillsPlugin component) {

        this.component = component;
        this.factory = new ProfessionFactory(component);
    }
}
