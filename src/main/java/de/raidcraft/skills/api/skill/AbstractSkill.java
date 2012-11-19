package de.raidcraft.skills.api.skill;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.inheritance.Child;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.SkillsComponent;
import de.raidcraft.skills.api.persistance.SkillData;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Silthus
 */
public abstract class AbstractSkill implements Skill, Child<Skill> {

    private final int id;
    private String name;
    private String friendlyName;
    private String description;
    private String[] usage;
    private Collection<Skill> strongParents = new HashSet<>();
    private Collection<Skill> weakParents = new HashSet<>();

    protected AbstractSkill(int id, SkillData data) {

        this.id = id;
        this.name = getClass().getAnnotation(SkillInformation.class).name();
        this.friendlyName = data.getFriendlyName();
        this.usage = data.getUsage();
        this.strongParents = data.getStrongParents();
        this.weakParents = data.getWeakParents();
        load(data);
    }

    @Override
    public void load(SkillData data) {
        // override when custom data needs to be loaded
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public String getFriendlyName() {

        return friendlyName;
    }

    protected void setDescription(String description) {

        this.description = description;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public String[] getUsage() {

        return usage;
    }

    @Override
    public boolean hasUsePermission(RCPlayer player) {

        return hasPermission(player,
                "rcskills.admin",
                "rcskills.use.*",
                "rcskills.use." + getId(),
                "rcskills.use." + getName().replace(" ", "_").toLowerCase());
    }

    protected boolean hasPermission(RCPlayer player, String... permissions) {

        if (RaidCraft.getComponent(SkillsComponent.class).getLocalConfiguration().allow_op && player.isOp()) return true;
        for (String perm : permissions) {
            if (player.hasPermission(perm)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<Skill> getStrongParents() {

        return strongParents;
    }

    @Override
    public Collection<Skill> getWeaksParents() {

        return weakParents;
    }

    @Override
    public void addStrongParent(Skill skill) {

        strongParents.add(skill);
    }

    @Override
    public void addWeakParent(Skill skill) {

        weakParents.add(skill);
    }

    @Override
    public void removeStrongParent(Skill skill) {

        strongParents.remove(skill);
    }

    @Override
    public void removeWeakParent(Skill skill) {

        weakParents.remove(skill);
    }

    @Override
    public void setStrongParents(Collection<Skill> skills) {

        this.strongParents = skills;
    }

    @Override
    public void setWeakParents(Collection<Skill> skills) {

        this.weakParents = skills;
    }

    @Override
    public String toString() {

        return "[S" + getId() + "-" + getClass().getName() + "]" + getName();
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Skill && ((Skill) obj).getId() == getId();
    }
}
