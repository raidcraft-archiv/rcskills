package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.type.Active;
import de.raidcraft.skills.api.skill.type.AreaAttack;
import de.raidcraft.skills.api.skill.type.Passive;
import de.raidcraft.skills.api.skill.type.TargetedAttack;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public class SkillAction extends AbstractAction<Hero> {

    private final Skill skill;

    public SkillAction(Skill skill) {

        super(skill.getHero());
        this.skill = skill;
    }

    @Override
    public void run() throws CombatException, InvalidTargetException {

        if (skill instanceof Passive) {
            throw new CombatException(CombatException.Type.PASSIVE);
        }
        // lets check the resources of the skill and if the hero has it
        if (skill.getTotalManaCost() > getSource().getMana()) {
            throw new CombatException(CombatException.Type.LOW_MANA);
        }
        if (skill.getTotalStaminaCost() > getSource().getStamina()) {
            throw new CombatException(CombatException.Type.LOW_STAMINA);
        }
        if (skill.getTotalHealthCost() > getSource().getHealth()) {
            throw new CombatException(CombatException.Type.LOW_HEALTH);
        }
        // lets check if the player has the required reagents
        for (ItemStack itemStack : skill.getProperties().getReagents()) {
            if (!getSource().getPlayer().getInventory().contains(itemStack)) {
                throw new CombatException(CombatException.Type.MISSING_REAGENT);
            }
        }

        // TODO: do some fancy checks for the resistence and stuff

        if (skill instanceof Active) {
            if (skill instanceof TargetedAttack) {
                ((TargetedAttack) skill).run(getSource(), getSource().getTarget());
            } else if (skill instanceof AreaAttack) {
                ((AreaAttack) skill).run(getSource(), getSource().getBlockTarget());
            }
        } else {
            // simply apply the skill - if it is overriden something will trigger
            skill.apply(getSource());
        }
        // keep this last or items will be removed before casting
        getSource().getPlayer().getInventory().removeItem(skill.getProperties().getReagents());
    }
}
