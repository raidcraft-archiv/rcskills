package de.raidcraft.skills.api.combat.action;

import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.api.effect.common.CastTime;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.effect.common.GlobalCooldown;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import de.raidcraft.skills.api.skill.PlayerCastSkillEvent;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.PlayerCastSkillTrigger;
import lombok.Data;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
@Data
public class SkillAction extends AbilityAction<Hero> {

    private final Skill skill;
    private final Map<String, Double> resourceCosts = new HashMap<>();
    private final PlayerCastSkillTrigger trigger;
    private CommandContext args;
    private double castTime;
    private double cooldown;
    private boolean delayed = false;
    private boolean bypassChecks = false;

    public SkillAction(Skill skill) {

        this(skill, null);
    }

    public SkillAction(Skill skill, CommandContext args) {

        super(skill);
        this.skill = skill;
        this.args = args;
        this.castTime = skill.getTotalCastTime();
        this.cooldown = skill.getConfiguredCooldown();

        for (Resource resource : skill.getHolder().getResources()) {
            resourceCosts.put(resource.getName(), skill.getTotalResourceCost(resource.getName()));
        }
        // lets issue a trigger that can be modified by other skills
        this.trigger = TriggerManager.callSafeTrigger(new PlayerCastSkillTrigger(this));
        // set the variable after the trigger call
        this.delayed = castTime > 0;
    }

    public double getResourceCost(String resource) {

        return resourceCosts.get(resource);
    }

    public void setResourceCost(String resource, double cost) {

        resourceCosts.put(resource, cost);
    }

    public double getCastTime() {

        return castTime;
    }

    public void setCastTime(double castTime) {

        if (castTime < 0.0) {
            castTime = 0.0;
        }
        this.castTime = castTime;
    }

    public double getCooldown() {

        return cooldown;
    }

    public void setCooldown(double cooldown) {

        if (cooldown < 0.0) {
            cooldown = 0.0;
        }
        this.cooldown = cooldown;
    }

    @Override
    public void run() throws CombatException {

        if (!bypassChecks && skill.getHolder().getPlayer().hasMetadata("GHOST")) {
            throw new CombatException("Du kannst als Geist keine Skills nutzen.");
        }
        if (!bypassChecks && !skill.isActive()) {
            throw new CombatException("Der gewählte Skill gehört zu keiner aktiven Spezialisierung von dir.");
        }
        if (!bypassChecks && !skill.isUnlocked()) {
            throw new CombatException("Du hast diesen Skill noch nicht freigeschaltet.");
        }
        if (!bypassChecks && !(skill instanceof CommandTriggered)) {
            throw new CombatException("Der Skill ist passiv und kann nicht aktiv genutzt werden.");
        }

        if (!bypassChecks && getSource().hasEffect(GlobalCooldown.class)) {
            throw new CombatException(CombatException.Type.ON_GLOBAL_COOLDOWN);
        }

        // lets cancel other casts first
        getSource().removeEffect(CastTime.class);

        if (trigger.isCancelled()) {
            throw new CombatException(CombatException.Type.CANCELLED);
        }

        // check if we meet all requirements to use the skill
        if (!bypassChecks) getSkill().checkUsage(this);

        if (delayed) {
            getSource().addEffect(skill, this, CastTime.class);
            this.delayed = false;
            return;
        }

        // run ambient stuff
        for (AmbientEffect ambientEffect : getSkill().getAmbientEffects(AbilityEffectStage.CAST)) {
            ambientEffect.run(getSource().getEntity().getLocation());
        }

        try {
            if (args == null) args = new CommandContext("");
        } catch (CommandException e) {
            throw new CombatException(e.getMessage());
        }

        // and call the trigger
        ((CommandTriggered) skill).runCommand(args);

        // lets remove the costs
        // it is important to remove them after the skill usage in order to calculate all the variable properly
        if (!skill.getSkillProperties().getInformation().queuedAttack()) {
            // dont substract usage cost on queued attacks
            // the queued effect will take care of that
            skill.substractUsageCost(this);
        }

        // also trigger combat effect if not supressed
        if (skill.getSkillProperties().getInformation().triggerCombat()) {
            try {
                skill.getHolder().addEffect(skill, Combat.class);
            } catch (CombatException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
                e.printStackTrace();
            }
        }

        // lets start the global cooldown
        getSource().addEffect(skill, GlobalCooldown.class);

        // run ambient stuff
        for (AmbientEffect ambientEffect : getSkill().getAmbientEffects(AbilityEffectStage.CASTED)) {
            ambientEffect.run(getSource().getEntity().getLocation());
        }

        getSource().setLastAction(this);
        RaidCraft.callEvent(new PlayerCastSkillEvent(this));
        // lets inform the player that his skill was executed
        skill.getHolder().sendMessage(ChatColor.DARK_GRAY + "Skill ausgeführt: " + skill.getFriendlyName());
    }

    public Skill getSkill() {

        return skill;
    }
}
