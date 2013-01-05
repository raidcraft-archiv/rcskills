package de.raidcraft.skills.effects.disabling;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackType;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.AttackTrigger;
import de.raidcraft.skills.trigger.ItemHeldTrigger;
import de.raidcraft.skills.util.ItemUtil;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Disarm",
        description = "Entwaffnet den Gegner",
        types = {EffectType.DEBUFF, EffectType.PHYSICAL, EffectType.HARMFUL}
)
public class Disarm<S> extends ExpirableEffect<S> implements Triggered {


    public Disarm(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    private void checkItem() {

        if (getTarget() instanceof Hero) {
            ItemStack inHand = ((Hero) getTarget()).getPlayer().getItemInHand();
            if (inHand != null && inHand.getTypeId() != 0 && ItemUtil.isWeapon(inHand.getType())) {
                ItemUtil.moveItem((Hero) getTarget(),
                        ((Hero) getTarget()).getPlayer().getInventory().getHeldItemSlot(),
                        inHand);
            }
        }
    }

    @TriggerHandler
    public void onItemHeld(ItemHeldTrigger trigger) {

        checkItem();
    }

    @TriggerHandler
    public void onAttack(AttackTrigger trigger) throws CombatException {

        if (trigger.getAttack().isOfAttackType(AttackType.PHYSICAL)) {
            checkItem();
            throw new CombatException("Du wurdest entwaffnet und kannst nicht angreifen!");
        }
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        warn("Du wurdest entwaffnet und kannst nicht angreifen!");
        checkItem();
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        warn("Du bist nicht mehr entwaffnet und kannst wieder angreifen.");
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
