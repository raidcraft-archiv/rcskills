package de.raidcraft.skills;

import com.comphenix.packetwrapper.Packet1ASpawnExperienceOrb;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.events.RCEntityDeathEvent;
import de.raidcraft.skills.api.events.RCExpGainEvent;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.skills.api.level.ExpPool;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.effects.Summoned;
import de.raidcraft.skills.util.ExpUtil;
import de.raidcraft.util.LocationUtil;
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
import java.util.HashSet;

/**
 * @author Silthus
 */
public final class ExperienceManager implements Listener {

    private final ProtocolManager protocolManager;
    private final SkillsPlugin plugin;

    protected ExperienceManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        plugin.registerEvents(this);
    }

    public void sendPacket(final Player player, Entity dead, short exp) {

        if (!player.isOnline()) {
            return;
        }

        Packet1ASpawnExperienceOrb experienceOrb = new Packet1ASpawnExperienceOrb();
        experienceOrb.setX(dead.getLocation().getX());
        experienceOrb.setY(dead.getLocation().getY() + 1);
        experienceOrb.setZ(dead.getLocation().getZ());
        experienceOrb.setCount(exp);

        try {
            protocolManager.sendServerPacket(player, experienceOrb.getHandle());
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

        CharacterTemplate attacker = attack.getAttacker();
        if (attacker == null || !(attacker instanceof Hero)) {
            return;
        }
        Hero hero = (Hero) attacker;
        if (plugin.getCommonConfig().getIgnoredWorlds().contains(hero.getPlayer().getWorld().getName())) {
            return;
        }
        int highestPlayerLevel = 0;
        int totalPlayerLevel = 0;
        HashSet<Hero> heroesToAddExp = new HashSet<>();
        for (Hero partyHero : hero.getParty().getHeroes()) {
            if (LocationUtil.getBlockDistance(partyHero.getEntity().getLocation(), character.getEntity().getLocation()) < plugin.getCommonConfig().party_exp_range) {
                heroesToAddExp.add(partyHero);
                if (partyHero.getPlayerLevel() > highestPlayerLevel) {
                    highestPlayerLevel = partyHero.getPlayerLevel();
                }
                totalPlayerLevel += partyHero.getPlayerLevel();
            }
        }
        if (heroesToAddExp.isEmpty()) return;
        int exp;
        if (character.getEntity().hasMetadata("RC_CUSTOM_MOB")) {
            exp = (int) ExpUtil.getPartyMobXPFull(
                    ((Hero) attacker).getPlayerLevel(),
                    highestPlayerLevel,
                    totalPlayerLevel,
                    character.getAttachedLevel().getLevel(),
                    character.getEntity().hasMetadata("ELITE"),
                    // TODO: maybe add rested EXP
                    0);
        } else {
            exp = plugin.getExperienceConfig().getEntityExperienceFor(character.getEntity().getType()) / heroesToAddExp.size();
        }
        if (exp > 0) {
            // lets actually give out the exp
            for (Hero expToAdd : heroesToAddExp) {
                expToAdd.getExpPool().addExp(exp);
                // lets do some visual magic tricks and let the player see the exp
                sendPacket(expToAdd.getPlayer(), character.getEntity(), (short) exp);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (plugin.getCommonConfig().getIgnoredWorlds().contains(event.getPlayer().getWorld().getName())) {
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
        if (plugin.getCommonConfig().getIgnoredWorlds().contains(event.getWhoClicked().getWorld().getName())) {
            return;
        }
        Hero hero = plugin.getCharacterManager().getHero((Player) event.getWhoClicked());
        ItemStack result = event.getRecipe().getResult();
        int exp = plugin.getExperienceConfig().getCraftingExperienceFor(result.getTypeId()) * result.getAmount();
        hero.getExpPool().addExp(plugin.getExperienceConfig().getCraftingExperienceFor(exp));
    }

    @EventHandler(ignoreCancelled = true)
    public void cancelCreativeModeExp(RCExpGainEvent event) {

        Levelable object = event.getAttachedLevel().getLevelObject();
        Player player = null;
        if (object instanceof Hero) {
            player = ((Hero) object).getPlayer();
        } else if (object instanceof Profession) {
            player = ((Profession) object).getHero().getPlayer();
        } else if (object instanceof Skill) {
            player = ((Skill) object).getHolder().getPlayer();
        }
        if (player != null && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
        }
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
            if (plugin.getCommonConfig().getIgnoredWorlds().contains(hero.getPlayer().getWorld().getName())) {
                return;
            }
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
