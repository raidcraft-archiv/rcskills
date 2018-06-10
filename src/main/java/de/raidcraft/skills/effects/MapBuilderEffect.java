package de.raidcraft.skills.effects;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.AbstractEffect;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import de.raidcraft.skills.api.trigger.TriggerHandler;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.skills.skills.MapBuilder;
import de.raidcraft.skills.trigger.*;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;

/**
 * @author mdoering
 */
@EffectInformation(
        name = "Map Builder",
        description = "Toggles the gamemode and prevents the dropping of items and more.",
        global = true
)
public class MapBuilderEffect extends AbstractEffect<MapBuilder> implements Triggered {

    public MapBuilderEffect(MapBuilder source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

        renew(target);
    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

        Player player = getSource().getHolder().getPlayer();
        player.setGameMode(GameMode.CREATIVE);

        Permission permissions = RaidCraft.getPermissions();
        for (String permission : getSource().getPermissions()) {
            permissions.playerAdd(player, permission);
        }
    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

        Player player = getSource().getHolder().getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        for (PotionEffect potionEffect : new ArrayList<>(player.getActivePotionEffects())) {
            player.removePotionEffect(potionEffect.getType());
        }

        Permission permissions = RaidCraft.getPermissions();
        for (String permission : getSource().getPermissions()) {
            permissions.playerRemove(player, permission);
        }
    }

    @TriggerHandler
    public void onAttack(AttackTrigger trigger) {

        warn("Du kannst im Map Builder Modus nicht angreifen!");
        trigger.setCancelled(true);
    }

    @TriggerHandler
    public void onDamage(DamageTrigger trigger) {

        trigger.setCancelled(true);
    }

    @TriggerHandler
    public void onSkillCast(PlayerCastSkillTrigger trigger) {

        if (trigger.getAction().getSkill().equals(getSource())) return;
        warn("Du kannst im Map Builder Modus keine Skills casten!");
        trigger.setCancelled(true);
    }

    @TriggerHandler
    public void onItemPickup(ItemPickupTrigger trigger) {

        trigger.getEvent().setCancelled(true);
    }

    @TriggerHandler
    public void onItemDrop(ItemDropTrigger trigger) {

        warn("Du kannst im Map Builder Modus keine Items droppen!");
        trigger.getEvent().setCancelled(true);
    }

    @TriggerHandler
    public void onInteract(PlayerInteractTrigger trigger) {

        PlayerInteractEvent event = trigger.getEvent();
        if (event.getClickedBlock().getState() instanceof InventoryHolder) {
            warn("Du kannst im Map Builder Modus keine Kisten o.ä. öffnen.");
            event.setCancelled(true);
        }
    }

    @TriggerHandler
    public void onNPCRightClick(NPCRightClickTrigger trigger) {

        warn("Du kannst im Map Builder Modus nicht mit NPCs interagieren.");
        trigger.getEvent().setCancelled(true);
    }
}
