package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class ProfessionManager {

    private final SkillsPlugin plugin;
    private final Map<String, Map<String, Profession>> professions = new HashMap<>();

    public ProfessionManager(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    public Profession getProfession(Hero hero, String profId) throws UnknownSkillException, UnknownProfessionException {

        profId = profId.toLowerCase();
        if (!professions.containsKey(hero.getUserName())) {
            professions.put(hero.getUserName(), new HashMap<String, Profession>());
        }
        if (!professions.get(hero.getUserName()).containsKey(profId)) {
            Profession profession = new ProfessionFactory(plugin, hero, profId).create();
            professions.get(hero.getUserName()).put(profId, profession);
        }
        return professions.get(hero.getUserName()).get(profId);
    }
}
