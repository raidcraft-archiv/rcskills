package de.raidcraft.skills.api.combat.attack;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.callback.Callback;
import de.raidcraft.skills.api.combat.callback.RangedCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.skill.type.Active;
import de.raidcraft.skills.api.skill.type.Passive;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public class SkillAttack extends AbstractAttack {

    private final Skill skill;
    private final Hero hero;
    private Callback callback;

    public SkillAttack(Skill skill, CharacterTemplate target) {

        super(skill.getHero(), target, skill.getTotalDamage());
        this.skill = skill;
        this.hero = skill.getHero();
    }

    public SkillAttack(Skill skill, CharacterTemplate target, Callback callback) {

        this(skill, target);
        this.callback = callback;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() throws CombatException, InvalidTargetException {

        try {
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

            ((Active) skill).run(hero, getTarget());
            // keep this last or items will be removed before casting
            hero.getPlayer().getInventory().removeItem(skill.getProperties().getReagents());

            // issue the callback if it was a direct callback
            if (callback != null && !(callback instanceof RangedCallback)) {
                callback.run(getTarget());
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new CombatException("Error when casting Target Type! Please check the console...");
        }
    }
}
