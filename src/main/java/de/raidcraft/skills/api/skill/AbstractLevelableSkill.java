package de.raidcraft.skills.api.skill;

import de.raidcraft.api.database.Database;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.ConfigurableLevel;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public abstract class AbstractLevelableSkill extends AbstractSkill implements LevelableSkill {

    private Level<LevelableSkill> level;

    public AbstractLevelableSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data, profession, database);
        attachLevel(new ConfigurableLevel<LevelableSkill>(this, data.getLevelFormula(), database));
    }

    @Override
    public final Level<LevelableSkill> getLevel() {

        return level;
    }

    @Override
    public final void attachLevel(Level<LevelableSkill> level) {

        this.level = level;
    }

    @Override
    public final int getMaxLevel() {

        return getProperties().getMaxLevel();
    }

    @Override
    public final int getTotalDamage() {

        return (int) (super.getTotalDamage() + (getProperties().getSkillLevelDamageModifier() * getLevel().getLevel()));
    }

    @Override
    public final int getTotalResourceCost(String resource) {

        return (int) (super.getTotalResourceCost(resource) +
                (getProperties().getResourceCostSkillLevelModifier(resource) * getLevel().getLevel()));
    }

    @Override
    public final int getTotalCastTime() {

        return (int) (super.getTotalCastTime() + (getProperties().getCastTimeSkillLevelModifier() * getLevel().getLevel()));
    }

    @Override
    public int getTotalRange() {

        return (int) (super.getTotalRange() + (getProperties().getRangeSkillLevelModifier() * getLevel().getLevel()));
    }

    @Override
    public final double getTotalCooldown() {

        return (super.getTotalCooldown() + (getProperties().getCooldownSkillLevelModifier() * getLevel().getLevel()));
    }

    @Override
    public final boolean isMastered() {

        return getLevel().hasReachedMaxLevel();
    }

    @Override
    public void onExpGain(int exp) {

        // override if needed
    }

    @Override
    public void onExpLoss(int exp) {

        // override if needed
    }

    @Override
    public void onLevelGain() {

        getHero().sendMessage(ChatColor.GREEN + "Du hast dein Skill Level gesteigert: " +
                ChatColor.AQUA + getProperties().getFriendlyName() +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getLevel().getLevel());
        // lets check the skills of the profession if we need to unlock any
        getProfession().checkSkillsForUnlock();
    }

    @Override
    public void onLevelLoss() {

        getHero().sendMessage(ChatColor.RED + "Du hast ein Skill Level verloren: " +
                ChatColor.AQUA + getProperties().getFriendlyName() +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getLevel().getLevel());
        getProfession().checkSkillsForUnlock();
    }

    @Override
    public final void save() {

        super.save();
        level.saveLevelProgress();
    }

    @Override
    public final void saveLevelProgress(Level<LevelableSkill> level) {

        database.setLevel(level.getLevel());
        database.setExp(level.getExp());
        Database.save(database);
    }

    @Override
    public final boolean equals(Object obj) {

        return obj instanceof LevelableSkill
                && super.equals(obj);
    }
}
