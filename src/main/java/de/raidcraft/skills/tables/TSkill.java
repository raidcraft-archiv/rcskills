package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.*;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "s_skills")
public class TSkill {

    @Id
    private int id;

    // some relation ships
    @OneToMany(cascade = CascadeType.ALL)
    private List<TSkillData> skillData;

    @OneToMany(cascade = CascadeType.ALL)
    private List<TProfessionSkill> professionSkills;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    private List<TSkillParents> parents;

    @NotNull
    @Column(unique = true)
    private String name;
    private String friendlyName;
    private String description;
    @Column(name = "s_usage")
    private String usage;
    private int manaCost;
    private double manaLevelModifier;
    private int staminaCost;
    private double staminaLevelModifier;
    private int healthCost;
    private double healthLevelModifier;
    private int requiredLevel;
    private int damage;
    private double damageLevelModifier;
    private double castTime;
    private double castTimeLevelModifier;
    private double duration;
    private double durationLevelModifier;


    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public List<TSkillData> getSkillData() {

        return skillData;
    }

    public void setSkillData(List<TSkillData> skillData) {

        this.skillData = skillData;
    }

    public List<TProfessionSkill> getProfessionSkills() {

        return professionSkills;
    }

    public void setProfessionSkills(List<TProfessionSkill> professionSkills) {

        this.professionSkills = professionSkills;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getFriendlyName() {

        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {

        this.friendlyName = friendlyName;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public String getUsage() {

        return usage;
    }

    public List<TSkillParents> getParents() {

        return parents;
    }

    public void setParents(List<TSkillParents> parents) {

        this.parents = parents;
    }

    public void setUsage(String usage) {

        this.usage = usage;
    }

    public int getManaCost() {

        return manaCost;
    }

    public void setManaCost(int manaCost) {

        this.manaCost = manaCost;
    }

    public double getManaLevelModifier() {

        return manaLevelModifier;
    }

    public void setManaLevelModifier(double manaLevelModifier) {

        this.manaLevelModifier = manaLevelModifier;
    }

    public int getStaminaCost() {

        return staminaCost;
    }

    public void setStaminaCost(int staminaCost) {

        this.staminaCost = staminaCost;
    }

    public double getStaminaLevelModifier() {

        return staminaLevelModifier;
    }

    public void setStaminaLevelModifier(double staminaLevelModifier) {

        this.staminaLevelModifier = staminaLevelModifier;
    }

    public int getHealthCost() {

        return healthCost;
    }

    public void setHealthCost(int healthCost) {

        this.healthCost = healthCost;
    }

    public double getHealthLevelModifier() {

        return healthLevelModifier;
    }

    public void setHealthLevelModifier(double healthLevelModifier) {

        this.healthLevelModifier = healthLevelModifier;
    }

    public int getRequiredLevel() {

        return requiredLevel;
    }

    public void setRequiredLevel(int requiredLevel) {

        this.requiredLevel = requiredLevel;
    }

    public int getDamage() {

        return damage;
    }

    public void setDamage(int damage) {

        this.damage = damage;
    }

    public double getDamageLevelModifier() {

        return damageLevelModifier;
    }

    public void setDamageLevelModifier(double damageLevelModifier) {

        this.damageLevelModifier = damageLevelModifier;
    }

    public double getCastTime() {

        return castTime;
    }

    public void setCastTime(double castTime) {

        this.castTime = castTime;
    }

    public double getCastTimeLevelModifier() {

        return castTimeLevelModifier;
    }

    public void setCastTimeLevelModifier(double castTimeLevelModifier) {

        this.castTimeLevelModifier = castTimeLevelModifier;
    }

    public double getDuration() {

        return duration;
    }

    public void setDuration(double duration) {

        this.duration = duration;
    }

    public double getDurationLevelModifier() {

        return durationLevelModifier;
    }

    public void setDurationLevelModifier(double durationLevelModifier) {

        this.durationLevelModifier = durationLevelModifier;
    }
}
