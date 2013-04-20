package de.raidcraft.skills.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Silthus
 */
public final class HeroUtil {

    private HeroUtil() {

    }

    public static void clearCache(final Hero hero) {

        final SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);

        // save the hero first
        hero.save();
        hero.clearEffects();
        // destroy all resources
        for (Resource resource : hero.getResources()) {
            resource.destroy();
        }
        // we also need to unregister all skill listeners
        for (Skill skill : plugin.getSkillManager().getAllSkills(hero)) {
            if (skill instanceof Triggered) {
                TriggerManager.unregisterListeners((Triggered) skill);
            }
        }

        Scoreboards.removeScoreboard(hero.getPlayer());

        // we clear the cache later to avoid events beeing triggered
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {

                plugin.getProfessionManager().clearProfessionCache(hero.getName());
                plugin.getSkillManager().clearSkillCache(hero.getName());
                plugin.getCharacterManager().clearCacheOf(hero);
            }
        }, 5L);
    }

    public static Collection<CharacterTemplate> toCharacters(Collection<LivingEntity> entities) {

        List<CharacterTemplate> characters = new ArrayList<>();
        CharacterManager manager = RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager();
        for (LivingEntity entity : entities) {
            characters.add(manager.getCharacter(entity));
        }
        return characters;
    }

    public static String createResourceBar(double current, double max, ChatColor color) {

        StringBuilder resourceBar = new StringBuilder(String.valueOf(ChatColor.RED) + "[" + color);
        int percent = (int) ((current / max) * 100.0);
        int progress = percent / 2;
        for (int i = 0; i < progress; i++) {
            resourceBar.append('|');
        }
        resourceBar.append(ChatColor.DARK_RED);
        for (int i = 0; i < 50 - progress; i++) {
            resourceBar.append('|');
        }
        resourceBar.append(ChatColor.RED).append(']');
        return String.valueOf(resourceBar) + " - " + color + percent + "%";
    }

    public static String createResourceBar(double current, double max) {

        return createResourceBar(current, max, ChatColor.BLUE);
    }

    @SuppressWarnings("unchecked")
    public static <V> V getEntityMetaData(LivingEntity entity, String key, V def) {

        List<MetadataValue> metadata = entity.getMetadata(key);
        if (metadata == null || metadata.size() < 1) return def;
        for (MetadataValue value : metadata) {
            if (value.getOwningPlugin().equals(RaidCraft.getComponent(SkillsPlugin.class))) {
                return (V) value.value();
            }
        }
        return def;
    }

    public static <V> void setEntityMetaData(LivingEntity entity, String key, V value) {

        entity.setMetadata(key, new FixedMetadataValue(RaidCraft.getComponent(SkillsPlugin.class), value));
    }

    public static Hero getHeroFromEntity(LivingEntity entity) throws UnknownPlayerException {

        CharacterTemplate character = RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getCharacter(entity);
        return getHeroFromCharacter(character);
    }

    public static Hero getHeroFromCharacter(CharacterTemplate character) throws UnknownPlayerException {

        if (character instanceof Hero) {
            return (Hero) character;
        }
        throw new UnknownPlayerException(character.getName() + " ist kein Spieler!");
    }

    public static Hero getHeroFromName(String name) throws UnknownPlayerException {

        return RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().getHero(name);
    }

    public static void maxOutAll(Hero hero) {

        hero.getAttachedLevel().setLevel(hero.getMaxLevel());
        for (Profession profession : hero.getProfessions()) {
            profession.getAttachedLevel().setLevel(profession.getMaxLevel());
        }
        for (Skill skill : hero.getSkills()) {
            if (skill instanceof Levelable) {
                ((Levelable) skill).getAttachedLevel().setLevel(((Levelable) skill).getMaxLevel());
            }
        }
    }

    public static final BlockFace[] axis = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
    public static final BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    /**
     * Gets the horizontal Block Face from a given yaw angle<br>
     * This includes the NORTH_WEST faces
     *
     * @param yaw angle
     * @return The Block Face of the angle
     */
    public static BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, true);
    }

    /**
     * Gets the horizontal Block Face from a given yaw angle
     *
     * @param yaw angle
     * @param useSubCardinalDirections setting, True to allow NORTH_WEST to be returned
     * @return The Block Face of the angle
     */
    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }
}
