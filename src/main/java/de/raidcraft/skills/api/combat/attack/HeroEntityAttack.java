package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
public class HeroEntityAttack extends AbstractAttack<Hero, LivingEntity> {

    public HeroEntityAttack(Hero attacker, LivingEntity target) {

        super(attacker, target, attacker.getDamage());
    }

    @Override
    public void run() {

        RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(getTarget());
    }
}
