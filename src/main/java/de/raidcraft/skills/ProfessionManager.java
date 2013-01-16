package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public final class ProfessionManager {

    public static final String VIRTUAL_PROFESSION = "virtual";
    private final SkillsPlugin plugin;
    private final Map<String, ProfessionFactory> professionFactories = new HashMap<>();
    private final Map<String, Map<String, Profession>> professions = new HashMap<>();

    protected ProfessionManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        loadProfessions();
    }

    public void reload() {

        professionFactories.clear();
        professions.clear();
        loadProfessions();
    }

    private void loadProfessions() {

        File dir = new File(plugin.getDataFolder(), plugin.getCommonConfig().profession_config_path);
        dir.mkdirs();
        // go thru all files in the directory and register them as professions
        for (String file : dir.list()) {
            if (file.contains(VIRTUAL_PROFESSION) || !file.endsWith(".yml")) {
                continue;
            }
            ProfessionFactory factory = new ProfessionFactory(plugin, file.replace(".yml", ""));
            professionFactories.put(factory.getProfessionName(), factory);
            plugin.getLogger().info("Loaded Profession: " + factory.getProfessionName());
        }
        // lets create the factory for the virtual profession
        professionFactories.put(VIRTUAL_PROFESSION, new ProfessionFactory(plugin, VIRTUAL_PROFESSION));
    }

    public Profession getVirtualProfession(Hero hero) {

        try {
            return getProfession(hero, VIRTUAL_PROFESSION);
        } catch (UnknownSkillException | UnknownProfessionException e) {
            plugin.getLogger().warning(e.getMessage());
        }
        return null;
    }

    public Profession getProfession(Hero hero, String profId) throws UnknownSkillException, UnknownProfessionException {

        profId = StringUtils.formatName(profId);
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

    public List<Profession> getAllProfessions(Hero hero) {

        List<Profession> professions = new ArrayList<>();
        for (String prof : professionFactories.keySet()) {
            if (!prof.equals(VIRTUAL_PROFESSION)) {
                try {
                    professions.add(getProfession(hero, prof));
                } catch (UnknownSkillException | UnknownProfessionException e) {
                    plugin.getLogger().warning(e.getMessage());
                }
            }
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
