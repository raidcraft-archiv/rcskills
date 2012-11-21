package de.raidcraft.skills.professions;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownPlayerProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.hero.Hero;
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
    private final Map<String, Map<String, LevelableProfession>> playerProfessions = new HashMap<>();

    public ProfessionManager(SkillsPlugin component) {

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

    public LevelableProfession getPlayerProfession(String name, Hero player) throws UnknownPlayerProfessionException, UnknownProfessionException {

        if (!playerProfessions.containsKey(player.getName())) {
            playerProfessions.put(player.getName(), new HashMap<String, LevelableProfession>());
        }
        Map<String, LevelableProfession> professionMap = playerProfessions.get(player.getName());
        LevelableProfession profession;
        if (!professionMap.containsKey(name)) {
            profession = factory.load(name, player);
            professionMap.put(name, profession);
        } else {
            profession = professionMap.get(name);
        }
        return profession;
    }
}
