package de.raidcraft.skills;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.AttackSource;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.effect.common.SunderingArmor;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.trigger.DamageTrigger;
import de.raidcraft.util.CustomItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Silthus
 */
public final class ArmorManager implements Triggered, Listener {

    private final SkillsPlugin plugin;

    protected ArmorManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        TriggerManager.registerListeners(this);
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {

        plugin.getCharacterManager().getHero((Player) event.getPlayer()).checkArmor();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getAction() != Action.RIGHT_CLICK_AIR || !event.hasItem()) {
            return;
        }
        if (CustomItemUtil.isArmor(event.getItem())) {
            plugin.getCharacterManager().getHero(event.getPlayer()).checkArmor();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClickEvent(InventoryClickEvent event) {

        if (event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            plugin.getCharacterManager().getHero((Player) event.getWhoClicked()).checkArmor();
        }
    }

    @TriggerHandler(ignoreCancelled = true, filterTargets = false, priority = TriggerPriority.HIGHEST)
    public void onDamage(DamageTrigger trigger) {

        Attack<?,CharacterTemplate> attack = trigger.getAttack();
        // dont reduce environment or non physical damage
        if (attack.hasSource(AttackSource.ENVIRONMENT)) {
            return;
        }
        if (attack.isOfAttackType(EffectType.IGNORE_ARMOR) || !attack.isOfAttackType(EffectType.PHYSICAL)) {
            return;
        }

        int totalArmor = attack.getTarget().getTotalArmorValue();
        // lets check if sunder armor effect is active
        if (attack.getTarget().hasEffect(SunderingArmor.class)) {
            totalArmor = (int) (totalArmor - totalArmor * attack.getTarget().getEffect(SunderingArmor.class).getArmorReduction());
        }

        double damageReduction = getDamageReduction(attack, totalArmor);
        double damage = attack.getDamage();
        double reducedDamage = damage * damageReduction;
        attack.setDamage(damage - reducedDamage);
        if (reducedDamage > 0) {
            attack.combatLog("Rüstung", "Schaden wurde um " + reducedDamage + "(" + ((int)(damageReduction * 10000)/100.0) + "%) verringert.");
        }
    }

    /**
     * This reduction formula is based on the WoW Armor Reduction formula for characters up to level 59.
     * %Reduction = (Armor / ([45 * Attacker_Level] + Armor + 200)) * 100
     * The reduction is always capped at 75% so nobdy can receive 0 damage from armor reduction.
     * <p/>
     * To make things easier we calculate with a enemy level of 60 at all times.
     * BUT you can change this when spawning your creature (e.g. boss).
     * <p/>
     * Since we have about half the armor items (4 opposed to 8) the formula is halfed.
     *
     * @return damage reduction in percent
     */
    public double getDamageReduction(Attack attack, int armor) {

        // default the attacker level to 60
        int attackerLevel;
        CharacterTemplate attacker = attack.getAttacker();
        if (attacker instanceof Hero) {
            attackerLevel = ((Hero) attacker).getPlayerLevel();
        } else {
            attackerLevel = attacker.getAttachedLevel().getLevel();
        }
        double reduction = armor / ((45.0 * attackerLevel) + armor + 200.0);
        // cap reduction at 75%
        if (reduction > 0.75) reduction = 0.75;
        return reduction;
    }

}