package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.path.VirtualPath;
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

    protected ProfessionManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        loadProfessions();
    }

    public void reload() {

        professionFactories.clear();
        loadProfessions();
    }

    private void loadProfessions() {

        File dir = new File(plugin.getDataFolder(), plugin.getCommonConfig().profession_config_path);
        dir.mkdirs();
        // get all registered paths from the path config
        for (Path<Profession> path : plugin.getPathConfig().getPaths()) {
            // and now create factories for all the professions defined in this path
            for (String profName : path.getParents()) {
                loadProfessionFactory(path, profName);
            }
            plugin.getLogger().info("Loaded all Professions for the path: " + path.getName());
        }

        // lets create the factory for the virtual profession
        professionFactories.put(VIRTUAL_PROFESSION, new ProfessionFactory(plugin, new VirtualPath(), VIRTUAL_PROFESSION));
    }

    private void loadProfessionFactory(Path<Profession> path, String profName) {

        ProfessionFactory factory = new ProfessionFactory(plugin, path, profName);
        // check if the profession is enabled
        if (!factory.getConfig().isEnabled()) {
            plugin.getLogger().info("Not loading profession: " + factory.getProfessionName() + " because it is not enabled.");
            return;
        }
        professionFactories.put(factory.getProfessionName(), factory);
        plugin.getLogger().info("Loaded Profession: " + factory.getProfessionName());
        // now we need to load all childs
        for (String child : factory.getConfig().getChildren()) {
            loadProfessionFactory(path, child);
        }
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

        return getProfession(hero, null, profId);
    }

    public Profession getProfession(Profession profession, String profId) throws UnknownProfessionException, UnknownSkillException {

        return getProfession(profession.getHero(), profession, profId);
    }

    public Profession getProfession(Hero hero, Profession parent, String profId) throws UnknownProfessionException, UnknownSkillException {

        profId = StringUtils.formatName(profId);
        if (!professionFactories.containsKey(profId)) {
            throw new UnknownProfessionException("The profession " + profId + " is not loaded or does not exist.");
        }
        if (!hero.hasProfession(profId)) {
            return professionFactories.get(profId).create(hero, parent);
        }
        return hero.getProfession(profId);
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
        throw new UnknownProfessionException("Es gibt keine Spezialisierung mit dem Namen: " + name);
    }

    public ProfessionFactory getFactory(Profession profession) {

        return professionFactories.get(profession.getProperties().getName());
    }
}
