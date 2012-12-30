package de.raidcraft.skills.api.combat.action;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.skills.api.effect.common.CastTime;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.CommandTriggered;

/**
 * @author Silthus
 */
public class CastAction extends AbstractAction<Hero> {

    private final Skill skill;
    private final CommandContext args;
    private boolean delayed = false;

    public CastAction(Skill skill, CommandContext args) {

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

        if (!(skill instanceof CommandTriggered)) {
            throw new CombatException("Du kannst diesen Skill nicht via Command ausführen.");
        }

        // check if we meet all requirements to use the skill
        getSkill().checkUsage();

        // lets cancel other casts first
        getSource().removeEffect(CastTime.class);

        if (delayed) {
            getSource().addEffect(skill, this, CastTime.class);
            this.delayed = false;
            return;
        }

        // lets remove the costs
        skill.substractUsageCost();

        // and call the trigger
        ((CommandTriggered) skill).runCommand(args);
    }
}
