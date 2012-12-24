package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public final class ProfessionManager {

    private final SkillsPlugin plugin;
    private final Map<String, ProfessionFactory> professionFactories = new HashMap<>();
    private final Map<String, Map<String, Profession>> professions = new HashMap<>();

    protected ProfessionManager(SkillsPlugin plugin) {

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
        if (!professions.containsKey(hero.getName())) {
            professions.put(hero.getName(), new HashMap<String, Profession>());
        }
        if (!professions.get(hero.getName()).containsKey(profId)) {
            Profession profession = professionFactories.get(profId).create(hero);
            professions.get(hero.getName()).put(profId, profession);
        }
        return professions.get(hero.getName()).get(profId);
    }

    public List<Profession> getAllProfessions(Hero hero) throws UnknownSkillException {

        List<Profession> professions = new ArrayList<>();
        for (ProfessionFactory factory : professionFactories.values()) {
            professions.add(factory.create(hero));
        }
        return professions;
    }

    public ProfessionFactory getFactory(String name) throws UnknownProfessionException {

        if (professionFactories.containsKey(name)) {
            return professionFactories.get(name);
        }
        throw new UnknownProfessionException("Es gibt keinen Beruf/Klasse mit dem Namen: " + name);
    }

    public ProfessionFactory getFactory(Profession profession) {

        return professionFactories.get(profession.getProperties().getName());
    }
}
