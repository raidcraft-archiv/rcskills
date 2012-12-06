package de.raidcraft.skills.skills.magic;

import de.raidcraft.skills.api.TargetedAttack;
import de.raidcraft.skills.api.combat.AbstractEffect;
import de.raidcraft.skills.api.combat.EffectInformation;
import de.raidcraft.skills.api.combat.RangedCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.util.DataMap;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "fireball",
        desc = "Schießt einen Feuerball auf den Gegener.",
        types = {Skill.Type.DAMAGING, Skill.Type.FIRE, Skill.Type.HARMFUL},
        defaults = {"incinerate: false", "bounce: false"}
)
public class Fireball extends AbstractLevelableSkill implements TargetedAttack {

    private boolean incinerate = false;
    private boolean bounce = false;

    public Fireball(Hero hero, SkillProperties skillData, Profession profession, THeroSkill database) {

        super(hero, skillData, profession, database);
    }

    @Override
    public void load(DataMap data) {

        incinerate = data.getBoolean("incinerate", false);
        bounce = data.getBoolean("bounce", false);
    }

    @Override
    public void run(final Hero hero, final LivingEntity target) throws CombatException {

        Player caster = hero.getBukkitPlayer();
        // lets create a new Spell from the Spells component
        // you can also do your own stuff here but if you think
        // a boss can do this stuff too add a spell please
        org.bukkit.entity.Fireball fireball = caster.getWorld().spawn(caster.getEyeLocation(), org.bukkit.entity.Fireball.class);
        fireball.setShooter(caster);
        fireball.setIsIncendiary(incinerate);
        fireball.setBounce(bounce);
        fireball.setFireTicks(0);
        // lets register a spell callback that is called when the fireball hits
        hero.damageEntity(target, 0, new RangedCallback() {
            @Override
            public void run(LivingEntity entity) {

                try {
                    hero.damageEntity(target, getTotalDamage());
                    addEffect(new FireballEffect(Fireball.this), target);
                    // add some exp to the profession and skill
                    getProfession().getLevel().addExp(2);
                    getLevel().addExp(5);
                } catch (CombatException e) {
                    // TODO: catch exception
                }
            }
        });
    }

    @EffectInformation(
            name = "FireballEffect",
            description = "Setzt das Ziel in Flammen."
    )
    public class FireballEffect extends AbstractEffect {

        public FireballEffect(Skill skill) {

            super(skill);
        }

        @Override
        public void apply(Hero hero, LivingEntity target) throws CombatException {

            // reminder: this method is called every set interval for the duration after the delay
            // damage entity for 5 and let it burn for 1 second
            hero.damageEntity(target, 5);
            target.setFireTicks(20);
        }
    }
}
