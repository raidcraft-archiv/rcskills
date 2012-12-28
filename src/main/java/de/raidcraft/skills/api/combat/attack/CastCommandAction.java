package de.raidcraft.skills.api.combat.attack;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.skills.api.effect.common.CastTime;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.CommandTrigger;

/**
 * @author Silthus
 */
public class CastCommandAction extends AbstractAction<Hero> {

    private final Skill skill;
    private final CommandContext args;
    private boolean delayed = false;

    public CastCommandAction(Skill skill, CommandContext args) {

        super(skill.getHero());
        this.skill = skill;
        this.args = args;
        this.delayed = skill.getTotalCastTime() > 0;
    }

    public Skill getSkill() {

        return skill;
    }

    @Override
    public void run() throws CombatException {

        // check if we meet all requirements to use the skill
        getSkill().checkUsage();

        // lets cancel other casts first
        getSource().removeEffect(CastTime.class);

        // TODO: do some fancy checks for the resistence and stuff
        if (delayed) {
            getSource().addEffect(skill, this, CastTime.class);
            this.delayed = false;
            return;
        }

        TriggerManager.callTrigger(new CommandTrigger(getSource(), args));
    }
}
