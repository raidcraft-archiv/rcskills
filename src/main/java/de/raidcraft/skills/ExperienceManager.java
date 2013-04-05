package de.raidcraft.skills;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.action.Attack;
import de.raidcraft.skills.api.events.RCExpGainEvent;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.level.ExpPool;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Silthus
 */
public final class ExperienceManager implements Listener {

    private final SkillsPlugin plugin;

    protected ExperienceManager(SkillsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {

        CharacterTemplate character = plugin.getCharacterManager().getCharacter(event.getEntity());
        Attack attack = character.getLastDamageCause();

        AttachedLevel<Hero> expPool;
        if (attack.getSource() instanceof Hero) {
            expPool = ((Hero) attack.getSource()).getExpPool();
        } else if (attack.getSource() instanceof Skill) {
            expPool = ((Skill) attack.getSource()).getHero().getExpPool();
        } else {
            return;
        }
        expPool.addExp(plugin.getExperienceConfig().getEntityExperienceFor(event.getEntityType()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {

        Hero hero = plugin.getCharacterManager().getHero(event.getPlayer());
        hero.getExpPool().addExp(plugin.getExperienceConfig().getBlockExperienceFor(event.getBlock().getTypeId()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraftItem(CraftItemEvent event) {

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
                    profession.getAttachedLevel().addExp(event.getGainedExp());
                    event.setCancelled(true);
                } catch (UnknownSkillException | UnknownProfessionException e) {
                    RaidCraft.LOGGER.warning(e.getMessage());
                }
            }
        }
    }
}
