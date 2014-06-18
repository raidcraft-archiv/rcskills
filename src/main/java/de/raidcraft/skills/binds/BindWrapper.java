package de.raidcraft.skills.binds;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.skills.api.skill.Skill;
import lombok.Value;

/**
 * Immutable Wrapper class to avoid code changes in SkillAction, Skill, Hero, etc.. :P
 */
@Value
public class BindWrapper {

    private final Skill skill;
    private final CommandContext commandContext;

}
