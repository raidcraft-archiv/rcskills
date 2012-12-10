package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;

import java.io.File;
import java.util.*;

/**
 * @author Silthus
 */
public final class ProfessionManager {

    private final SkillsPlugin plugin;
    private final Map<String, ProfessionFactory> professionFactories = new HashMap<>();
    private final Map<String, Map<String, Profession>> professions = new HashMap<>();

    public ProfessionManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        loadProfessions();
    }

    private void loadProfessions() {

        File dir = new File(plugin.getDataFolder(), "/professions/");
        dir.mkdirs();
        // go thru all files in the directory and register them as professions
        for (File file : dir.listFiles()) {
            ProfessionFactory factory = plugin.configure(new ProfessionFactory(plugin, file));
            professionFactories.put(factory.getName(), factory);
        }
    }

    public Profession getProfession(Hero hero, String profId) throws UnknownSkillException, UnknownProfessionException {

        profId = profId.toLowerCase();
        if (!professionFactories.containsKey(profId)) {
            throw new UnknownProfessionException("The profession " + profId + " is not loaded or does not exist.");
        }
        if (!professions.containsKey(hero.getUserName())) {
            professions.put(hero.getUserName(), new HashMap<String, Profession>());
        }
        if (!professions.get(hero.getUserName()).containsKey(profId)) {
            Profession profession = professionFactories.get(profId).create(hero);
            professions.get(hero.getUserName()).put(profId, profession);
        }
        return professions.get(hero.getUserName()).get(profId);
    }

    public List<Profession> getAllProfessions(Hero hero) throws UnknownSkillException {

        List<Profession> professions = new ArrayList<>();
        for (ProfessionFactory factory : professionFactories.values()) {
            professions.add(factory.create(hero));
        }
        return professions;
    }
}
