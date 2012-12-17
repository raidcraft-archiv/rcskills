package de.raidcraft.skills.skills.magic;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.attack.RangedAttack;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.skill.type.AreaAttack;
import de.raidcraft.skills.tables.THeroSkill;
import de.raidcraft.util.DataMap;
import org.bukkit.Location;

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
    public void run(final Hero hero, final Location target) throws CombatException, InvalidTargetException {

        new RangedAttack(hero, org.bukkit.entity.Fireball.class, new RangedCallback() {
            @Override
            public void run(CharacterTemplate target) throws CombatException {

                target.damage(getTotalDamage());
                // TODO: replace with generic burn effect from effects common package
                // target.addEffect(new FireballEffect(Fireball.this));
                // add some exp to the profession and skill
                getProfession().getLevel().addExp(2);
                getLevel().addExp(5);
            }
        }).run();
    }
}
