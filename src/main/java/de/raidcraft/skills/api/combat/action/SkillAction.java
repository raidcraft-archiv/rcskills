package de.raidcraft.skills.api.combat.action;

import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.skills.api.effect.common.CastTime;
import de.raidcraft.skills.api.effect.common.GlobalCooldown;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.PlayerCastSkillTrigger;

/**
 * @author Silthus
 */
public class SkillAction extends AbstractAction<Hero> {

    private final Skill skill;
    private final CommandContext args;
    private boolean delayed = false;

    public SkillAction(Skill skill, CommandContext args) {

        super(skill.getHero());
        this.skill = skill;
        this.args = args;
        this.delayed = skill.getTotalCastTime() > 0;
    }

    public SkillAction(Skill skill) throws CommandException {

        this(skill, new CommandContext(""));
    }

    public Skill getSkill() {

        return skill;
    }

    @Override
    public void run() throws CombatException {

        if (!(skill instanceof CommandTriggered)) {
            throw new CombatException("Du kannst diesen Skill nicht via Command ausführen.");
        }

        if (getSource().hasEffect(GlobalCooldown.class)) {
            throw new CombatException(CombatException.Type.ON_GLOBAL_COOLDOWN);
        }

        // check if we meet all requirements to use the skill
        getSkill().checkUsage();

        // lets cancel other casts first
        getSource().removeEffect(CastTime.class);

        PlayerCastSkillTrigger trigger = TriggerManager.callTrigger(new PlayerCastSkillTrigger(getSource(), getSkill()));
        if (trigger.isCancelled()) {
            throw new CombatException(CombatException.Type.CANCELLED);
        }

        if (delayed) {
            CastTime castTime = getSource().addEffect(skill, this, CastTime.class);
            if (trigger.isCastTimeChanged()) {
                castTime.setCastTime(trigger.getCastTime());
            }
            this.delayed = false;
            return;
        }

        // and call the trigger
        ((CommandTriggered) skill).runCommand(args);

        // lets remove the costs
        // it is important to remove them after the skill usage in order to calculate all the variable properly
        skill.substractUsageCost();

        // lets start the global cooldown
        getSource().addEffect(skill, GlobalCooldown.class);
    }
}
