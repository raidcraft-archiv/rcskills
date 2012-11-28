package de.raidcraft.skills.skills.magic;

import de.raidcraft.skills.api.TargetedAttack;
import de.raidcraft.skills.api.combat.AbstractEffect;
import de.raidcraft.skills.api.combat.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.spells.api.SpellCallback;
import de.raidcraft.spells.fire.RCFireball;
import de.raidcraft.util.DataMap;
import org.bukkit.entity.LivingEntity;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "fireball",
        desc = "Schießt einen Feuerball auf den Gegener.",
        types = {Skill.Type.DAMAGING, Skill.Type.FIRE, Skill.Type.HARMFUL}
)
public class Fireball extends AbstractLevelableSkill implements TargetedAttack {

    private boolean afterBurner;

    public Fireball(Hero hero, SkillProperties skillData, THeroSkill database) {

        super(hero, skillData, database);
    }

    @Override
    public void load(DataMap data) {

        afterBurner = data.getBoolean("burn-after-hit", false);
    }

    @Override
    public void run(final Hero hero, final LivingEntity target) throws CombatException {

        // lets create a new Spell from the Spells component
        // you can also do your own stuff here but if you think
        // a boss can do this stuff too add a spell please
        RCFireball fireball = new RCFireball();
        // set the firetick damage based on the player level
        fireball.fireTicks = 20 * hero.getLevel().getLevel();
        // let it burn the target
        fireball.incinerate = true;
        // cast the fireball and wait for a callback after it hit
        fireball.run(hero.getBukkitPlayer(), new SpellCallback() {
            @Override
            public void run() {

                // only apply the after burn effect if set in our custom config
                if (!afterBurner) {
                    return;
                }
                // if the fireball hit the target add a burn effect
                if (isHit(target)) {
                    // also add some extra damage after the fireball hit
                    // the total damage is calculated from config settings and the player, prof and skill level
                    hero.damageEntity(target, getTotalDamage());
                    addEffect(new FireballEffect().setDelay(5).setDuration(20).setInterval(2), target);
                }
            }
        }, target);
        // add some exp to the profession and skill
        getProfession().getLevel().addExp(2);
        getLevel().addExp(5);
    }

    @EffectInformation(
            name = "FireballEffect",
            description = "Das Ziel brennt von der Hitze des Feuerballs."
    )
    public class FireballEffect extends AbstractEffect {

        @Override
        public void apply(Hero hero, LivingEntity target) throws CombatException {

            // reminder: this method is called every set interval for the duration after the delay
            // damage entity for 5 and let it burn for 1 second
            hero.damageEntity(target, 5);
            target.setFireTicks(20);
        }
    }
}
