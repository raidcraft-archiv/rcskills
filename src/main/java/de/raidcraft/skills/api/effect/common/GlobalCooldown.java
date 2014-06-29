package de.raidcraft.skills.api.effect.common;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.skill.Skill;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "GlobalCooldown",
        description = "Wenn du diesen Effekt hast kannst du keine aktiven Zauber wirken.",
        global = true
)
public class GlobalCooldown extends ExpirableEffect<Skill> {

    public GlobalCooldown(Skill source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
        this.duration = (long) (RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig().global_cooldown * 20);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

    }
}
