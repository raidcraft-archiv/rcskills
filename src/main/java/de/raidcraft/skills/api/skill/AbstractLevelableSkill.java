package de.raidcraft.skills.api.skill;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.ProfessionManager;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.SkillAttachedLevel;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.tables.THeroSkill;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
@IgnoredSkill
public abstract class AbstractLevelableSkill extends AbstractSkill implements LevelableSkill {

    private AttachedLevel<LevelableSkill> attachedLevel;

    public AbstractLevelableSkill(Hero hero, SkillProperties data, Profession profession, THeroSkill database) {

        super(hero, data, profession, database);
        attachLevel(new SkillAttachedLevel(this, database));
    }

    @Override
    public final boolean isMastered() {

        return getAttachedLevel().hasReachedMaxLevel();
    }

    @Override
    public final AttachedLevel<LevelableSkill> getAttachedLevel() {

        return attachedLevel;
    }

    @Override
    public final void attachLevel(AttachedLevel<LevelableSkill> attachedLevel) {

        this.attachedLevel = attachedLevel;
    }

    @Override
    public final int getMaxLevel() {

        return getSkillProperties().getMaxLevel();
    }

    @Override
    public void onExpGain(int exp) {
        //TODO: implement
    }

    @Override
    public void onExpLoss(int exp) {
        //TODO: implement
    }

    @Override
    public void onLevelGain() {

        getHolder().sendMessage(ChatColor.GREEN + "Du hast dein Skill Level gesteigert: " +
                ChatColor.AQUA + getSkillProperties().getFriendlyName() +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getAttachedLevel().getLevel());
        // lets check the skills of the profession if we need to unlock any
        getProfession().checkSkillsForUnlock();
    }

    @Override
    public void onLevelLoss() {

        getHolder().sendMessage(ChatColor.RED + "Du hast ein Skill Level verloren: " +
                ChatColor.AQUA + getSkillProperties().getFriendlyName() +
                ChatColor.ITALIC + ChatColor.YELLOW + " Level " + getAttachedLevel().getLevel());
        getProfession().checkSkillsForUnlock();
    }

    @Override
    public final void saveLevelProgress(AttachedLevel<LevelableSkill> attachedLevel) {

        THeroSkill skill = RaidCraft.getDatabase(SkillsPlugin.class).find(THeroSkill.class, getId());
        skill.setLevel(attachedLevel.getLevel());
        skill.setExp(attachedLevel.getExp());

        // dont save when the player is in a blacklist world
        if (getProfession().getName().equalsIgnoreCase(ProfessionManager.VIRTUAL_PROFESSION)
                || RaidCraft.getComponent(SkillsPlugin.class).isSavingWorld(getHolder().getPlayer().getWorld().getName())) {
            RaidCraft.getDatabase(SkillsPlugin.class).save(skill);
        }
    }

    @Override
    public final void save() {

        super.save();
        attachedLevel.saveLevelProgress();
    }

    @Override
    public final boolean equals(Object obj) {

        return obj instanceof LevelableSkill
                && super.equals(obj);
    }
}
