package de.raidcraft.skills.skills.magic;

import de.raidcraft.skills.api.AreaAttack;
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
import org.bukkit.Location;
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
public class Fireball extends AbstractLevelableSkill implements AreaAttack {

    private boolean incinerate = false;
    private boolean bounce = false;
    private float yield = 1.0F;
    private int fireTicks = 0;

    public Fireball(Hero hero, SkillProperties skillData, Profession profession, THeroSkill database) {

        super(hero, skillData, profession, database);
    }

    @Override
    public void load(DataMap data) {

        incinerate = data.getBoolean("incinerate", incinerate);
        bounce = data.getBoolean("bounce", bounce);
        yield = (float) data.getDouble("strength", yield);
        fireTicks = data.getInt("fireticks", fireTicks);
    }

    @Override
    public void run(final Hero hero, final Location target) throws CombatException {

        Player caster = hero.getBukkitPlayer();
        // lets create a new Spell from the Spells component
        // you can also do your own stuff here but if you think
        // a boss can do this stuff too add a spell please
        org.bukkit.entity.Fireball fireball = caster.getWorld().spawn(caster.getEyeLocation(), org.bukkit.entity.Fireball.class);
        fireball.setShooter(caster);
        fireball.setIsIncendiary(incinerate);
        fireball.setBounce(bounce);
        fireball.setFireTicks(fireTicks);
        fireball.setYield(yield);
        // lets register a spell callback that is called when the fireball hits
        hero.castRangeAttack(new RangedCallback() {
            @Override
            public void run(LivingEntity target) throws CombatException {

                hero.damageEntity(target, getTotalDamage());
                addEffect(new FireballEffect(Fireball.this), target);
                // add some exp to the profession and skill
                getProfession().getLevel().addExp(2);
                getLevel().addExp(5);
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
        }
    }
}
