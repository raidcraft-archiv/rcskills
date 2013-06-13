package de.raidcraft.skills;

import de.raidcraft.api.items.attachments.ItemAttachment;
import de.raidcraft.api.items.attachments.ItemAttachmentException;
import de.raidcraft.api.items.attachments.ItemAttachmentProvider;
import de.raidcraft.api.items.attachments.ProviderInformation;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.loader.GenericJarFileManager;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.IgnoredSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.config.AliasesConfig;
import de.raidcraft.skills.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
@ProviderInformation("skills")
public final class SkillManager extends GenericJarFileManager<Skill> implements ItemAttachmentProvider {

    private final SkillsPlugin plugin;
    private final Map<String, SkillFactory> skillFactories = new HashMap<>();
    private final Map<String, Class<? extends Skill>> skillClasses = new HashMap<>();
    // list of cached skills mapped to a hero
    private final Map<String, Map<CachedSkill, Skill>> cachedSkills = new HashMap<>();
    private int loadedSkills;
    private int failedSkills;

    protected SkillManager(SkillsPlugin plugin) {

        super(Skill.class, new File(plugin.getDataFolder(), plugin.getCommonConfig().skill_jar_path));
        this.plugin = plugin;
        // create the config path
        new File(plugin.getDataFolder(), plugin.getCommonConfig().skill_config_path).mkdirs();
    }

    public void reload() {

        loadedSkills = 0;
        failedSkills = 0;
        skillFactories.clear();
        loadFactories();
    }

    @Override
    public void loadFactories() {

        for (Class<? extends Skill> clazz : loadClasses()) {
            try {
                registerClass(clazz);
            } catch (UnknownSkillException e) {
                plugin.getLogger().warning(e.getMessage());
                failedSkills++;
            }
        }
        plugin.getLogger().info("Loaded " + loadedSkills + "/" + (failedSkills + loadedSkills) + " skills.");
    }

    @Override
    public ItemAttachment getItemAttachment(Player player, String attachmentName) throws ItemAttachmentException {

        try {
            Hero hero = plugin.getCharacterManager().getHero(player);
            Skill skill = hero.getSkill(attachmentName);
            if (skill instanceof ItemAttachment) {
                return (ItemAttachment) skill;
            }
            throw new ItemAttachmentException("The skill " + skill.getName() + " is not a valid ItemAttachment!");
        } catch (UnknownSkillException e) {
            throw new ItemAttachmentException(e.getMessage());
        }
    }

    /**
     * Registers a skill directly with the skill manager making it possible to use that skill.
     * Skills in external files are loaded via our own class loader on loadSkillClasses() call.
     *
     * @param skillClass of the skill
     */
    public void registerClass(Class<? extends Skill> skillClass) throws UnknownSkillException {

        if (skillClass.isAnnotationPresent(IgnoredSkill.class)) {
            return;
        }

        if (skillClass.isAnnotationPresent(SkillInformation.class)) {
            String skillName = StringUtils.formatName(skillClass.getAnnotation(SkillInformation.class).name());
            // check for duplicate skills
            if (skillFactories.containsKey(skillName)) {
                throw new UnknownSkillException("Found duplicate Skill: " + skillName);
            }
            // load the skill factory
            SkillFactory factory = new SkillFactory(plugin, skillClass, skillName);
            skillFactories.put(skillName, factory);
            skillClasses.put(skillName, skillClass);
            // lets create the skill once to make a default config
            factory.createDefaults();
            loadedSkills++;
        } else {
            throw new UnknownSkillException("Found skill without SkillInformation: " + skillClass.getCanonicalName());
        }
    }

    protected void createAliasFactory(String alias, String skill, AliasesConfig config) throws UnknownSkillException {

        SkillFactory factory = new SkillFactory(plugin, skillClasses.get(skill), skill, config);
        skillFactories.put(alias, factory);
        factory.createDefaults();
    }

    public Skill getSkill(Hero hero, Profession profession, String skillName) throws UnknownSkillException {

        Skill skill;
        skillName = StringUtils.formatName(skillName);
        if (!skillFactories.containsKey(skillName)) {
            throw new UnknownSkillException("Es gibt keinen Skill mit dem Namen: " + skillName);
        }
        String heroName = StringUtils.formatName(hero.getName());
        if (!cachedSkills.containsKey(heroName)) {
            cachedSkills.put(heroName, new HashMap<CachedSkill, Skill>());
        }
        // lets create a cached skill instance to counter check with our cache
        // the skill will be null in this cached skill instance
        CachedSkill cache = new CachedSkill(hero, profession, skillName);
        if (cachedSkills.get(heroName).containsKey(cache)) {
            return cachedSkills.get(heroName).get(cache);
        }
        // lets create a new skill for this name
        skill = skillFactories.get(skillName).create(hero, profession);
        cachedSkills.get(heroName).put(cache, skill);
        // lets add the skill as a trigger handler
        if (skill instanceof Triggered) {
            TriggerManager.registerListeners((Triggered) skill);
        }
        return skill;
    }

    public SkillFactory getFactory(Skill skill) {

        return getFactory(skill.getName());
    }

    protected SkillFactory getFactory(String name) {

        return skillFactories.get(name);
    }

    public Collection<? extends Skill> getAllSkills(Hero hero) {

        Set<Skill> skills = new HashSet<>();
        for (Profession profession : plugin.getProfessionManager().getAllProfessions(hero)) {
            skills.addAll(profession.getSkills());
        }
        return skills;
    }

    public Collection<? extends Skill> getAllVirtualSkills(Hero hero) {

        List<Skill> skills = new ArrayList<>();
        // also add a virtual skill of all
        for (String skillName : skillFactories.keySet()) {
            try {
                Skill skill = getSkill(hero, plugin.getProfessionManager().getVirtualProfession(hero), skillName);
                skills.add(skill);
            } catch (UnknownSkillException e) {
                hero.sendMessage(ChatColor.RED + e.getMessage());
                plugin.getLogger().warning(e.getMessage());
            }
        }
        return skills;
    }

    protected Collection<SkillFactory> getSkillFactoriesFor(Class<? extends Skill> sClass) {

        Set<SkillFactory> skills = new HashSet<>();
        for (SkillFactory factory : skillFactories.values()) {
            if (factory.getSkillClass() == sClass) {
                skills.add(factory);
            }
        }
        return skills;
    }

    public boolean hasSkill(String skill) {

        skill = StringUtils.formatName(skill);
        return skillFactories.containsKey(skill);
    }

    public void clearSkillCache(String heroName) {

        Map<CachedSkill, Skill> cache = cachedSkills.remove(StringUtils.formatName(heroName));
        if (cache == null) return;
        for (Skill skill : cache.values()) {
            if (skill instanceof Triggered) {
                TriggerManager.unregisterListeners((Triggered) skill);
            }
        }
    }

    public static class CachedSkill {

        private final String player;
        private final String name;
        private final String profession;

        public CachedSkill(Skill skill) {

            this.player = StringUtils.formatName(skill.getHolder().getName());
            this.name = StringUtils.formatName(skill.getName());
            this.profession = StringUtils.formatName(skill.getProfession().getName());
        }

        public CachedSkill(Hero hero, Profession profession, String skillName) {

            this.player = StringUtils.formatName(hero.getName());
            this.name = StringUtils.formatName(skillName);
            this.profession = StringUtils.formatName(profession.getName());
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CachedSkill that = (CachedSkill) o;

            return name.equals(that.name) && player.equals(that.player) && profession.equals(that.profession);

        }

        @Override
        public int hashCode() {

            int result = player.hashCode();
            result = 31 * result + name.hashCode();
            result = 31 * result + profession.hashCode();
            return result;
        }
    }
}
