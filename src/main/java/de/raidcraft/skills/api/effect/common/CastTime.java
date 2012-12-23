package de.raidcraft.skills.api.effect.common;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.attack.SkillAction;
import de.raidcraft.skills.api.effect.DelayedEffect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Casttime",
        description = "Keeps track of the casttime for a char template",
        priority = -1.0
)
public class CastTime extends DelayedEffect<Skill> implements Listener {

    public CastTime(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        setPriority(-1.0);
        delay = source.getTotalCastTime();
        RaidCraft.getComponent(SkillsPlugin.class).registerEvents(this);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        try {
            new SkillAction(getSource()).run();
        } catch (InvalidTargetException e) {
            getSource().getHero().sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {
        //TODO: implement
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {
        //TODO: implement
    }
}
