package de.raidcraft.skills.api.profession;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Reasonable;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.ProfessionAttachedLevel;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.resource.ConfigurableResource;
import de.raidcraft.skills.api.resource.HealthResource;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.hero.TemporaryHero;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroResource;
import de.raidcraft.skills.util.StringUtils;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractProfession implements Profession {

    protected final Map<String, Skill> skills = new CaseInsensitiveMap<>();
    private final int id;
    private final ProfessionProperties properties;
    private final Hero hero;
    private final Path<Profession> path;
    private final List<Profession> children;
    // list of requirements to unlock this profession
    private final List<Requirement<Player>> requirements = new ArrayList<>();
    private final Map<String, Resource> resources = new CaseInsensitiveMap<>();
    private boolean active = false;
    // can be null - if it is this profession has no parents :*(
    private Profession parent;
    private AttachedLevel<Profession> attachedLevel;

    protected AbstractProfession(Hero hero, ProfessionProperties data, Path<Profession> path, Profession parent, THeroProfession database) {

        this.id = database.getId();
        this.properties = data;
        this.hero = hero;
        this.path = path;
        this.parent = parent;
        this.children = data.loadChildren(this);
        this.active = database.isActive();
        // attach a level
        attachLevel(new ProfessionAttachedLevel(this, database));
    }

    public void loadResources() {

        for (Resource resource : resources.values()) {
            resource.destroy();
        }
        resources.clear();
        // first we need to get the defined resources out of the config
        for (String key : getProperties().getResources()) {
            key = StringUtils.formatName(key);
            boolean healthResource = key.equalsIgnoreCase("health");
            // query the database and check if we already have an entry for the player
            EbeanServer database = RaidCraft.getComponent(SkillsPlugin.class).getDatabase();
            THeroResource tHeroResource = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroResource.class).where()
                    .eq("name", key)
                    .eq("profession_id", getId()).findUnique();
            // create a new entry if none exists
            if (tHeroResource == null) {
                tHeroResource = new THeroResource();
                tHeroResource.setName(key);
                tHeroResource.setProfession(database.find(THeroProfession.class, getId()));
                database.save(tHeroResource);
            }
            Resource resource;
            if (healthResource) {
                resource = new HealthResource(tHeroResource, this, getProperties().getResourceConfig(key));
            } else {
                resource = new ConfigurableResource(tHeroResource, this, getProperties().getResourceConfig(key));
            }
            resources.put(key, resource);
            getHero().attachResource(resource);
        }
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return getProperties().getName();
    }

    @Override
    public boolean isMastered() {

        return getAttachedLevel().hasReachedMaxLevel();
    }

    @Override
    public String getFriendlyName() {

        return getProperties().getFriendlyName();
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public boolean isActive() {

        return active;
    }

    @Override
    public void setActive(boolean active) {

        this.active = active;
        for (Resource resource : getResources()) {
            if (active) {
                getHero().attachResource(resource);
            } else {
                getHero().detachResource(resource.getName());
            }
        }
    }

    @Override
    public Set<Resource> getResources() {

        return new HashSet<>(resources.values());
    }

    @Override
    public ProfessionProperties getProperties() {

        return properties;
    }

    @Override
    public Collection<Skill> getSkills() {

        return skills.values();
    }

    @Override
    public void addSkill(Skill skill) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeSkill(Skill skill) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void save() {

        if (getHero() instanceof TemporaryHero) return;
        saveLevelProgress(getAttachedLevel());

        THeroProfession profession = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroProfession.class, getId());
        profession.setActive(isActive());
        RaidCraft.getDatabase(SkillsPlugin.class).save(profession);
    }

    @Override
    public void checkSkillsForUnlock() {

        for (Skill skill : getSkills()) {
            // check all skills and if we need to unlock any
            if (!skill.isUnlocked() && isActive() && skill.isMeetingAllRequirements(getHero().getPlayer())) {
                skill.unlock();
            }
            // check if we need to lock any skills
            if (skill.isUnlocked() && !skill.isMeetingAllRequirements(getHero().getPlayer())) {
                skill.lock();
            }
        }
    }

    @Override
    public boolean hasSkill(String id) {

        return skills.containsKey(id);
    }

    @Override
    public Skill getSkill(String id) {

        return skills.get(id);
    }

    @Override
    public int getTotalMaxLevel() {

        int maxLevel = getMaxLevel();
        Profession profession = this;
        while (profession.hasParent()) {
            profession = profession.getParent();
            maxLevel += profession.getMaxLevel();
        }
        return maxLevel;
    }

    @Override
    public int getTotalLevel() {

        int level = getAttachedLevel().getLevel();
        Profession profession = this;
        while (profession.hasParent()) {
            profession = profession.getParent();
            level += profession.getAttachedLevel().getLevel();
        }
        return level;
    }

    @Override
    public final AttachedLevel<Profession> getAttachedLevel() {

        return attachedLevel;
    }

    @Override
    public final void attachLevel(AttachedLevel<Profession> attachedLevel) {

        this.attachedLevel = attachedLevel;
    }

    @Override
    public int getMaxLevel() {

        return getProperties().getMaxLevel();
    }

    @Override
    public void saveLevelProgress(AttachedLevel<Profession> attachedLevel) {

        if (getHero() instanceof TemporaryHero) return;
        THeroProfession profession = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroProfession.class, getId());
        profession.setLevel(attachedLevel.getLevel());
        profession.setExp(attachedLevel.getExp());
        RaidCraft.getDatabase(SkillsPlugin.class).save(profession);
    }

    public void loadSkills() {

        this.skills.clear();
        this.skills.putAll(properties.loadSkills(this));
        checkSkillsForUnlock();
    }

    @Override
    public Path getPath() {

        return path;
    }

    @Override
    public boolean hasChildren() {

        return children != null && children.size() > 0;
    }

    @Override
    public List<Profession> getChildren() {

        return children;
    }

    @Override
    public List<Requirement<Player>> getRequirements() {

        if (requirements.isEmpty()) {
            requirements.addAll(getProperties().loadRequirements(this));
        }
        return requirements;
    }

    @Override
    public boolean isMeetingAllRequirements(Player player) {

        return getRequirements().stream().allMatch(requirement -> requirement.test(player));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getResolveReason(Player player) {

        for (Requirement<Player> requirement : getRequirements()) {
            if (!requirement.test(getHero().getPlayer())) {
                if (requirement instanceof Reasonable) {
                    return ((Reasonable<Player>) requirement).getReason(player);
                }
            }
        }
        return "Spezialisierung kann freigeschaltet werden.";
    }

    @Override
    public boolean hasParent() {

        return getParent() != null;
    }

    @Override
    public Profession getParent() {

        return parent;
    }

    @Override
    public void setParent(Profession parent) {

        this.parent = parent;
    }

    @Override
    public int compareTo(Profession o) {

        return o.getProperties().getFriendlyName().compareTo(getProperties().getFriendlyName());
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Profession
                && ((Profession) obj).getId() != 0 && getId() != 0
                && ((Profession) obj).getId() == getId();
    }

    @Override
    public String toString() {

        return getProperties().getFriendlyName();
    }
}
