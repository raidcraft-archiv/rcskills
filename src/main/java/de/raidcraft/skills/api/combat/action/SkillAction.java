package de.raidcraft.skills.api.combat.action;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.effect.common.CastTime;
import de.raidcraft.skills.api.effect.common.Combat;
import de.raidcraft.skills.api.effect.common.GlobalCooldown;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.CommandTriggered;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.trigger.PlayerCastSkillTrigger;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public class SkillAction extends AbstractAction<Hero> {

    private final Skill skill;
    private final CommandContext args;
    private final Map<String, Double> resourceCosts = new HashMap<>();
    private int castTime;
    private boolean delayed = false;

    private final PlayerCastSkillTrigger trigger;

    public SkillAction(Skill skill, CommandContext args) {

        super(skill.getHero());
        this.skill = skill;
        this.args = args;
        this.castTime = skill.getTotalCastTime();
        for (Resource resource : skill.getHero().getResources()) {
            resourceCosts.put(resource.getName(), skill.getTotalResourceCost(resource.getName()));
        }

        // lets issue a trigger that can be modified by other skills
        this.trigger = TriggerManager.callSafeTrigger(new PlayerCastSkillTrigger(this));
    }

    public SkillAction(Skill skill) {

        this(skill, null);
    }

    public Skill getSkill() {

        return skill;
    }

    public double getResourceCost(String resource) {

        return resourceCosts.get(resource);
    }

    public void setResourceCost(String resource, double cost) {

        resourceCosts.put(resource, cost);
    }

    public int getCastTime() {

        return castTime;
    }

    public void setCastTime(int castTime) {

        this.castTime = castTime;
    }

    @Override
    public void run() throws CombatException {

        if (!skill.isActive()) {
            throw new CombatException("Der gewählte Skill gehört zu keiner aktiven Spezialisierung von dir.");
        }
        if (!skill.isUnlocked()) {
            throw new CombatException("Du hast diesen Skill noch nicht freigeschaltet.");
        }
        if (!(skill instanceof CommandTriggered)) {
            throw new CombatException("Der Skill ist passiv und kann nicht aktiv genutzt werden.");
        }

        if (getSource().hasEffect(GlobalCooldown.class)) {
            throw new CombatException(CombatException.Type.ON_GLOBAL_COOLDOWN);
        }

        // lets cancel other casts first
        getSource().removeEffect(CastTime.class);

        if (trigger.isCancelled()) {
            throw new CombatException(CombatException.Type.CANCELLED);
        }

        // check if we meet all requirements to use the skill
        getSkill().checkUsage(this);

        if (delayed) {
            CastTime castTime = getSource().addEffect(skill, this, CastTime.class);
            castTime.setCastTime(getCastTime());
            this.delayed = false;
            return;
        }

        // and call the trigger
        ((CommandTriggered) skill).runCommand(args);

        // lets remove the costs
        // it is important to remove them after the skill usage in order to calculate all the variable properly
        if (!skill.getProperties().getInformation().queuedAttack()) {
            // dont substract usage cost on queued attacks
            // the queued effect will take care of that
            skill.substractUsageCost(this);
        }

        // also trigger combat effect if not supressed
        if (skill.getProperties().getInformation().triggerCombat()) {
            try {
                skill.getHero().addEffect(skill, Combat.class);
            } catch (CombatException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }

        // lets start the global cooldown
        getSource().addEffect(skill, GlobalCooldown.class);

        // lets inform the player that his skill was executed
        skill.getHero().sendMessage(ChatColor.DARK_GRAY + "Skill ausgeführt: " + skill.getFriendlyName());
    }
}
