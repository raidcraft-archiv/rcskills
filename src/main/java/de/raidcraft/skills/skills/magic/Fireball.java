package de.raidcraft.skills.skills.magic;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.ProjectileType;
import de.raidcraft.skills.api.combat.action.MagicalAttack;
import de.raidcraft.skills.api.combat.action.RangedAttack;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.SkillProperties;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.AbstractLevelableSkill;
import de.raidcraft.skills.api.skill.SkillInformation;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.effects.damaging.Burn;
import de.raidcraft.skills.effects.disabling.KnockBack;
import de.raidcraft.skills.tables.THeroSkill;

/**
 * @author Silthus
 */
@SkillInformation(
        name = "fireball",
        desc = "Schießt einen Feuerball auf den Gegener.",
        types = {EffectType.DAMAGING, EffectType.DEBUFF, EffectType.HARMFUL, EffectType.MAGICAL},
        elements = {EffectElement.FIRE},
        triggerCombat = true
)
public class Fireball extends AbstractLevelableSkill implements CommandTriggered {

    public Fireball(Hero hero, SkillProperties skillData, Profession profession, THeroSkill database) {

        super(hero, skillData, profession, database);
    }

    @TriggerHandler
    public void runCommand(CommandContext args) throws CombatException {

        final RangedAttack rangedAttack = new RangedAttack(getHero(), ProjectileType.FIREBALL);
        rangedAttack.addCallback(new RangedCallback() {
            @Override
            public void run(CharacterTemplate target) throws CombatException, InvalidTargetException {

                new MagicalAttack(getHero(), target, getTotalDamage()).run();
                addEffect(target, Burn.class);
                addEffect(rangedAttack.getProjectile().getLocation(), target, KnockBack.class);
                // add some exp to the profession and skill
                getProfession().getLevel().addExp(1);
                getLevel().addExp(5);
            }
        });
        rangedAttack.run();
    }

    @Override
    public void apply() {

    }

    @Override
    public void remove() {

    }
}
