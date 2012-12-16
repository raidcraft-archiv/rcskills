package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.type.Active;
import de.raidcraft.skills.api.skill.type.Passive;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public class SkillAttack<T> extends AbstractAttack<Hero, T> {

    private final Skill skill;
    private final Hero hero;

    public SkillAttack(Skill skill, T target) {

        super(skill.getHero(), target, skill.getTotalDamage());
        this.skill = skill;
        this.hero = skill.getHero();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() throws CombatException, InvalidTargetException {

        if (skill instanceof Passive) {
            throw new CombatException(CombatException.Type.PASSIVE);
        }
        // lets check the resources of the skill and if the hero has it
        if (skill.getTotalManaCost() > hero.getMana()) {
            throw new CombatException(CombatException.Type.LOW_MANA);
        }
        if (skill.getTotalStaminaCost() > hero.getStamina()) {
            throw new CombatException(CombatException.Type.LOW_STAMINA);
        }
        if (skill.getTotalHealthCost() > hero.getHealth()) {
            throw new CombatException(CombatException.Type.LOW_HEALTH);
        }
        // lets check if the player has the required reagents
        for (ItemStack itemStack : skill.getProperties().getReagents()) {
            if (!hero.getPlayer().getInventory().contains(itemStack)) {
                throw new CombatException(CombatException.Type.MISSING_REAGENT);
            }
        }

        // TODO: do some fancy checks for the resistence and stuff

        try {
            ((Active<T>) skill).run(hero, getTarget());
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new CombatException("Error when casting Target Type! Please check the console...");
        }
        // keep this last or items will be removed before casting
        hero.getPlayer().getInventory().removeItem(skill.getProperties().getReagents());
    }
}
