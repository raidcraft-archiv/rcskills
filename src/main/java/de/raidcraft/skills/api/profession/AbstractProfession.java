package de.raidcraft.skills.api.profession;

import com.avaje.ebean.Ebean;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.requirement.Requirement;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.ConfigurableAttachedLevel;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.persistance.ProfessionProperties;
import de.raidcraft.skills.api.resource.ConfigurableResource;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THeroProfession;
import de.raidcraft.skills.tables.THeroResource;
import de.raidcraft.skills.util.StringUtils;

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
public abstract class AbstractProfession implements Profession {

    private final ProfessionProperties properties;
    private final Hero hero;
    private final Path<Profession> path;
    // can be null - if it is this profession has no parents :*(
    private final Profession parent;
    private final List<Profession> children;
    // list of requirements to unlock this profession
    private final List<Requirement> requirements = new ArrayList<>();
    private final Map<String, Resource> resources = new HashMap<>();
    protected final Map<String, Skill> skills;
    protected final THeroProfession database;

    private AttachedLevel<Profession> attachedLevel;

    protected AbstractProfession(Hero hero, ProfessionProperties data, Path<Profession> path, Profession parent, THeroProfession database) {

        this.properties = data;
        this.hero = hero;
        this.path = path;
        this.database = database;
        this.parent = parent;
        this.children = data.loadChildren(this);
        // attach a level
        attachLevel(new ConfigurableAttachedLevel<Profession>(this, data.getLevelFormula(), database));
        // first we need to get the defined resources out of the config
        for (String key : data.getResources()) {
            key = StringUtils.formatName(key);
            // query the database and check if we already have an entry for the player
            THeroResource tHeroResource = Ebean.find(THeroResource.class).where()
                    .eq("name", key)
                    .eq("profession_id", database.getId()).findUnique();
            // create a new entry if none exists
            if (tHeroResource == null) {
                tHeroResource = new THeroResource();
                tHeroResource.setName(key);
                tHeroResource.setProfession(database);
            }
            ConfigurableResource resource = new ConfigurableResource(tHeroResource, this, data.getResourceConfig(key));
            resources.put(key, resource);
        }
        // load the skills at the end because they access the level of the profession and more
        this.skills = properties.loadSkills(this);
        getProperties().loadRequirements(this);
    }

    public THeroProfession getDatabase() {

        return database;
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
    public int getId() {

        return database.getId();
    }

    @Override
    public String getName() {

        return getProperties().getName();
    }

    @Override
    public String getFriendlyName() {

        return getProperties().getFriendlyName();
    }

    @Override
    public ProfessionProperties getProperties() {

        return properties;
    }

    @Override
    public Path getPath() {

        return path;
    }

    @Override
    public Hero getHero() {

        return hero;
    }

    @Override
    public boolean isActive() {

        return database.isActive();
    }

    @Override
    public void setActive(boolean active) {

        database.setActive(active);
    }

    @Override
    public boolean isMastered() {

        return getAttachedLevel().hasReachedMaxLevel();
    }

    @Override
    public int getMaxLevel() {

        return getProperties().getMaxLevel();
    }

    @Override
    public Collection<Skill> getSkills() {

        return skills.values();
    }

    @Override
    public boolean hasSkill(String id) {

        return skills.containsKey(id);
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
    public Set<Resource> getResources() {

        return new HashSet<>(resources.values());
    }

    @Override
    public Resource getResource(String type) {

        return resources.get(type);
    }

    @Override
    public List<Requirement> getRequirements() {

        if (requirements.size() < 1) {
            requirements.addAll(getProperties().loadRequirements(this));
        }
        return requirements;
    }

    @Override
    public boolean isMeetingAllRequirements() {

        for (Requirement requirement : getRequirements()) {
            if (!requirement.isMet()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getResolveReason() {

        for (Requirement requirement : requirements) {
            if (!requirement.isMet()) {
                return requirement.getLongReason();
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
    public boolean hasChildren() {

        return children != null && children.size() > 0;
    }

    @Override
    public List<Profession> getChildren() {

        return children;
    }

    @Override
    public void save() {

        // save all resources
        for (Resource resource : getResources()) {
            resource.save();
        }
        saveLevelProgress(getAttachedLevel());
        database.setActive(isActive());
        Database.save(database);
    }

    @Override
    public void saveLevelProgress(AttachedLevel<Profession> attachedLevel) {

        database.setLevel(attachedLevel.getLevel());
        database.setExp(attachedLevel.getExp());
        Database.save(database);
    }

    @Override
    public String toString() {

        return getProperties().getFriendlyName();
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
}
