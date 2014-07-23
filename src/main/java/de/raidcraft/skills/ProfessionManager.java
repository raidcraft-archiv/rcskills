package de.raidcraft.skills;

import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.path.VirtualPath;
import de.raidcraft.skills.api.profession.AbstractProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.util.StringUtils;
import de.raidcraft.util.CaseInsensitiveMap;

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
    private final Map<String, ProfessionFactory> professionFactories = new CaseInsensitiveMap<>();
    private final Map<String, Map<String, Profession>> cachedProfessions = new CaseInsensitiveMap<>();
    private final File configBaseDir;

    protected ProfessionManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        this.configBaseDir = new File(plugin.getDataFolder(), plugin.getCommonConfig().profession_config_path);
        loadProfessions();
    }

    private void loadProfessions() {

        Map<String, File> fileMap = loadProfessions(configBaseDir);
        // get all registered paths from the path config
        for (Path<Profession> path : plugin.getPathConfig().getPaths()) {
            // and now create factories for all the professions defined in this path
            for (String profName : path.getParents()) {
                if (!fileMap.containsKey(profName.toLowerCase())) {
                    plugin.getLogger().warning("Profession in paths.yml defined but not found in config folder: " + profName);
                    continue;
                }
                ProfessionFactory factory = loadProfessionFactory(path, profName, fileMap.get(profName.toLowerCase()));
                if (factory == null) {
                    continue;
                }
                // now we need to load all childs
                for (String child : factory.getConfig().getChildren()) {
                    if (!fileMap.containsKey(child.toLowerCase())) {
                        plugin.getLogger().warning("Tried to load non existant child profession in " + factory.getProfessionName());
                        continue;
                    }
                    loadProfessionFactory(path, child, fileMap.get(child.toLowerCase()));
                }
            }
            plugin.getLogger().info("Loaded all Professions for the path: " + path.getName());
        }
        // lets create the factory for the virtual profession
        professionFactories.put(VIRTUAL_PROFESSION, new ProfessionFactory(plugin, new VirtualPath(), VIRTUAL_PROFESSION, new File(configBaseDir, "virtual.yml")));
    }

    private Map<String, File> loadProfessions(File dir) {

        Map<String, File> files = new HashMap<>();
        dir.mkdirs();
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                files.putAll(loadProfessions(file));
                continue;
            }
            if (!file.getName().endsWith(".yml") || file.getName().equalsIgnoreCase("virtual.yml")) {
                continue;
            }
            files.put(file.getName().replace(".yml", "").toLowerCase(), file);
        }
        return files;
    }

    private ProfessionFactory loadProfessionFactory(Path<Profession> path, String profName, File file) {

        ProfessionFactory factory = new ProfessionFactory(plugin, path, profName, file);
        // check if the profession is enabled
        if (!factory.getConfig().isEnabled()) {
            plugin.getLogger().info("Not loading profession: " + factory.getProfessionName() + " because it is not enabled.");
            return null;
        }
        professionFactories.put(factory.getProfessionName(), factory);
        plugin.getLogger().info("Loaded Profession: " + factory.getProfessionName());
        return factory;
    }

    public Profession getProfession(Profession profession, String profId) throws UnknownProfessionException, UnknownSkillException {

        return getProfession(profession.getHero(), profession, profId);
    }

    public Profession getProfession(Hero hero, Profession parent, String profId) throws UnknownProfessionException, UnknownSkillException {

        profId = StringUtils.formatName(profId);
        if (!professionFactories.containsKey(profId)) {
            throw new UnknownProfessionException("The profession " + profId + " is not loaded or does not exist.");
        }
        if (!cachedProfessions.containsKey(hero.getName())) {
            cachedProfessions.put(hero.getName(), new CaseInsensitiveMap<>());
        }
        if (cachedProfessions.get(hero.getName()).containsKey(profId)) {
            return cachedProfessions.get(hero.getName()).get(profId);
        }
        Profession profession;
        if (hero.hasProfession(profId)) {
            profession = hero.getProfession(profId);
            cachedProfessions.get(hero.getName()).put(profId, profession);
        } else {
            // create a new profession
            profession = professionFactories.get(profId).create(hero, parent);
            cachedProfessions.get(hero.getName()).put(profId, profession);
            if (profession instanceof AbstractProfession) {
                ((AbstractProfession) profession).loadResources();
                ((AbstractProfession) profession).loadSkills();
            }
        }
        return profession;
    }

    public List<Profession> getAllProfessions(Hero hero) {

        return getAllProfessions(hero, true);
    }

    public List<Profession> getAllProfessions(Hero hero, boolean virtual) {

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
        if (virtual) {
            professions.add(getVirtualProfession(hero));
        }
        return professions;
    }

    public Profession getProfession(Hero hero, String profId) throws UnknownSkillException, UnknownProfessionException {

        return getProfession(hero, null, profId);
    }

    public Profession getVirtualProfession(Hero hero) {

        try {
            return getProfession(hero, VIRTUAL_PROFESSION);
        } catch (UnknownSkillException | UnknownProfessionException e) {
            plugin.getLogger().warning(e.getMessage());
        }
        return null;
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

    public void clearProfessionCache(String player) {

        cachedProfessions.remove(player);
    }
}
