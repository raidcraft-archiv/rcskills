package de.raidcraft.skills;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.effect.common.SunderingArmor;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.TriggerPriority;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.config.CustomConfig;
import de.raidcraft.skills.items.ArmorPiece;
import de.raidcraft.skills.items.ArmorType;
import de.raidcraft.skills.trigger.DamageTrigger;
import de.raidcraft.util.ItemUtils;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Silthus
 */
public final class ArmorManager implements Triggered {

    private final SkillsPlugin plugin;
    private final Map<Integer, Integer> defaultArmorValue = new HashMap<>();

    protected ArmorManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        TriggerManager.registerListeners(this);
        load();
    }

    private void load() {

        defaultArmorValue.clear();
        CustomConfig config = CustomConfig.getConfig("armor");
        Set<String> keys = config.getKeys(false);
        if (keys == null || keys.size() < 1) {
            plugin.getLogger().warning("No armor pieces in custom armor.yml config configured!");
            return;
        }
        for (String key : keys) {
            Material item = ItemUtils.getItem(key);
            if (item != null && ArmorType.fromItemId(item.getId()) != null) {
                defaultArmorValue.put(item.getId(), config.getInt(key, 0));
            } else {
                plugin.getLogger().warning("Wrong armor item configured in custom config: " + config.getName() + " - " + key);
            }
        }
    }

    public void reload() {

        load();
    }

    @TriggerHandler(ignoreCancelled = true, priority = TriggerPriority.MONITOR)
    public void onDamage(DamageTrigger trigger) {

        Attack<?,CharacterTemplate> attack = trigger.getAttack();
        if (attack.isOfAttackType(EffectType.IGNORE_ARMOR) || !attack.isOfAttackType(EffectType.PHYSICAL)) {
            return;
        }

        int totalArmor = 0;
        for (ArmorPiece armorPiece : attack.getTarget().getArmor()) {

            totalArmor += armorPiece.getArmorValue();
        }
        // lets check if sunder armor effect is active
        if (attack.getTarget().hasEffect(SunderingArmor.class)) {
            totalArmor = (int) (totalArmor - totalArmor * attack.getTarget().getEffect(SunderingArmor.class).getArmorReduction());
        }

        double damageReduction = getDamageReduction(attack, totalArmor);
        int reducedDamage = (int) (attack.getDamage() * damageReduction);
        attack.setDamage(attack.getDamage() - reducedDamage);
        if (reducedDamage > 0) {
            attack.combatLog("Rüstung", "Schaden wurde um " + reducedDamage + "(" + (damageReduction * 100) + ") verringert.");
        }
    }

    public int getDefaultArmorValue(int itemId) {

        return defaultArmorValue.get(itemId);
    }

    /**
     * This reduction formula is based on the WoW Armor Reduction formula for characters up to level 59.
     * %Reduction = (Armor / ([45 * Enemy_Level] + Armor + 200)) * 100
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

        // default the level to 60
        int level = 60;
        // we dont want to get the level of heroes and only calculate with mob level if applicable
        if (attack.getSource() instanceof Levelable && !(attack.getSource() instanceof Hero)) {
            level = ((Levelable) attack.getSource()).getAttachedLevel().getLevel();
        }
        double reduction = armor / ((45.0 * level) + armor + 200.0);
        // cap reduction at 75%
        if (reduction > 0.75) reduction = 0.75;
        return reduction;
    }

}
