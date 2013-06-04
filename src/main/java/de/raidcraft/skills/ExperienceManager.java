package de.raidcraft.skills;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.combat.action.EffectDamage;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.events.RCEntityDeathEvent;
import de.raidcraft.skills.api.events.RCExpGainEvent;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.ExpPool;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.effects.Summoned;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Silthus
 */
public final class ExperienceManager implements Listener {

    private final ProtocolManager protocolManager;
    private final SkillsPlugin plugin;
    private final WrappedDataWatcher batWatcher;

    protected ExperienceManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.batWatcher = getDefaultWatcher(plugin.getServer().getWorlds().get(0), EntityType.BAT);
        plugin.registerEvents(this);
    }

    public void sendPacket(Player p, Entity dead, int exp) {

        PacketContainer newPacket = new PacketContainer(24);

        newPacket.getIntegers().
                write(0, 500).
                write(1, (int) EntityType.BAT.getTypeId()).
                write(2, (int) (dead.getLocation().getX() * 32)).
                write(3, (int) ((dead.getLocation().getY() + 0.5) * 32)).
                write(4, (int) (dead.getLocation().getZ() * 32));

        // batWatcher.setObject(0, (byte) 0x20);
        batWatcher.setObject(5, ChatColor.GREEN + "+" + String.valueOf(exp) + " EXP");
        batWatcher.setObject(6, (byte) 1);
        newPacket.getDataWatcherModifier().write(0, batWatcher);

        try {
            protocolManager.sendServerPacket(p, newPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public WrappedDataWatcher getDefaultWatcher(World world, EntityType type) {

        Entity entity = world.spawnEntity(new Location(world, 0, 256, 0), type);
        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();

        entity.remove();
        return watcher;
    }

    @SuppressWarnings("unchecked")
    @EventHandler
    public void onEntityDeath(RCEntityDeathEvent event) {

        CharacterTemplate character = event.getCharacter();
        Attack attack = character.getLastDamageCause();

        if (attack == null || character.hasEffect(Summoned.class)) {
            return;
        }

        Hero hero;
        if (attack.getSource() instanceof Hero) {
            hero = ((Hero) attack.getSource());
        } else if (attack.getSource() instanceof Skill) {
            hero = ((Skill) attack.getSource()).getHolder();
        } else if (attack instanceof EffectDamage) {
            hero = ((Effect<Skill>) attack.getSource()).getSource().getHolder();
        } else {
            return;
        }
        AttachedLevel<Hero> expPool = hero.getExpPool();
        int exp = plugin.getExperienceConfig().getEntityExperienceFor(character.getEntity().getType());
        expPool.addExp(exp);
        // lets do some visual magic tricks and let the player see the exp
        sendPacket(hero.getPlayer(), character.getEntity(), exp);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Hero hero = plugin.getCharacterManager().getHero(event.getPlayer());
        hero.getExpPool().addExp(plugin.getExperienceConfig().getBlockExperienceFor(event.getBlock().getTypeId()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraftItem(CraftItemEvent event) {

        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Hero hero = plugin.getCharacterManager().getHero((Player) event.getWhoClicked());
        ItemStack result = event.getRecipe().getResult();
        int exp = plugin.getExperienceConfig().getCraftingExperienceFor(result.getTypeId()) * result.getAmount();
        hero.getExpPool().addExp(plugin.getExperienceConfig().getCraftingExperienceFor(exp));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onExpGain(RCExpGainEvent event) {

        double expBoost = plugin.getExperienceConfig().getExpRate();
        if (expBoost == 0.0) {
            return;
        }
        // modify the gained exp by the boost factor
        event.setGainedExp((int) (event.getGainedExp() * expBoost));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void expPoolRedirectExpGain(RCExpGainEvent event) {

        if (event.getAttachedLevel() instanceof ExpPool) {
            // redirect the exp directly to the profession if linked
            Hero hero = (Hero) event.getAttachedLevel().getLevelObject();
            String linkedProf = Option.EXP_POOL_LINK.get(hero);
            if (linkedProf != null) {
                try {
                    Profession profession = plugin.getProfessionManager().getProfession(hero, linkedProf);
                    // dont redirect exp into a mastered profession
                    if (profession.isMastered()) {
                        Option.EXP_POOL_LINK.set(hero, null);
                        hero.sendMessage(ChatColor.RED + "Die Verbindung von deinem EXP Pool mit der "
                                + profession.getPath().getFriendlyName() + " Spezialisierung " + profession.getFriendlyName()
                                + " wurde aufgehoben, da die Spezialisierung bereits das maximale Level erreicht hat.");
                        return;
                    }
                    profession.getAttachedLevel().addExp(event.getGainedExp());
                    event.setCancelled(true);
                } catch (UnknownSkillException | UnknownProfessionException e) {
                    RaidCraft.LOGGER.warning(e.getMessage());
                }
            }
        }
    }
}
