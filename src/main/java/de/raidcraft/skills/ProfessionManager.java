package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public final class ProfessionManager {

    private final SkillsPlugin plugin;
    private final Set<String> availableProfessions = new HashSet<>();
    private final Map<String, Map<String, Profession>> professions = new HashMap<>();

    public ProfessionManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        loadProfessions();
    }

    private void loadProfessions() {

        File file = new File(plugin.getDataFolder(), "/professions/");
        file.mkdirs();
        // go thru all files in the directory and register them as professions
        for (String yml : file.list()) {
            yml = yml.toLowerCase().replace(".yml", "").replace("professions", "").trim();
            availableProfessions.add(yml);
        }
    }

    public Profession getProfession(Hero hero, String profId) throws UnknownSkillException, UnknownProfessionException {

        profId = profId.toLowerCase();
        if (!availableProfessions.contains(profId)) {
            throw new UnknownProfessionException("The profession " + profId + " is not loaded.");
        }
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
