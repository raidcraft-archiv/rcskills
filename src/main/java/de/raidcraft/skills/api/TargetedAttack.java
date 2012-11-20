package de.raidcraft.skills.api;

import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.SkillResult;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public interface TargetedAttack extends Active<LivingEntity> {

    @Override
    public SkillResult run(Hero hero, LivingEntity target) throws CombatException;
}
