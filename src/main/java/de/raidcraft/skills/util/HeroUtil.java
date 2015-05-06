package de.raidcraft.skills.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.CharacterManager;
import de.raidcraft.skills.Scoreboards;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Attribute;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.trigger.TriggerManager;
import de.raidcraft.skills.api.trigger.Triggered;
import de.raidcraft.util.EntityUtil;
import de.raidcraft.util.TimeUtil;
import de.raidcraft.util.UUIDUtil;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public final class HeroUtil {

    public static final BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public static final BlockFace[] radial = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};

    private HeroUtil() {

    }

    public static void clearCache(final Hero hero) {

        final SkillsPlugin plugin = RaidCraft.getComponent(SkillsPlugin.class);

        // save the hero first
        hero.save();
        hero.clearEffects();
        hero.leaveParty();
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

    public static String createResourceBar(double current, double max) {

        return createResourceBar(current, max, ChatColor.BLUE);
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

    // TODO: use this class more oftern
    public static Hero getHeroFromName(String name) throws UnknownPlayerException {

        Hero hero =  RaidCraft.getComponent(SkillsPlugin.class)
                .getCharacterManager().getHero(UUIDUtil.convertPlayer(name));
        if(hero == null) {
            throw  new UnknownPlayerException("No hero found for: " + name);
        }
        return hero;
    }

    public static Profession getActivePathProfession(Hero hero, Path<Profession> path) {

        for (Profession profession : path.getParents(hero)) {
            if (profession.isActive()) {
                return profession;
            }
        }
        return null;
    }

    public static void maxOutAll(Hero hero) {

        SkillsPlugin.LocalConfiguration config = RaidCraft.getComponent(SkillsPlugin.class).getCommonConfig();
        Set<String> excludedProfessions = config.getExcludedProfessions();
        Set<String> excludedSkills = config.getExcludedSkills();
        for (Profession profession : hero.getProfessions()) {
            if (excludedProfessions.contains(profession.getName().toLowerCase())) {
                continue;
            }
            profession.getAttachedLevel().setLevel(profession.getMaxLevel());
        }
        for (Skill skill : hero.getSkills()) {
            if (excludedProfessions.contains(skill.getProfession().getName().toLowerCase())
                    || excludedSkills.contains(skill.getName().toLowerCase())) {
                continue;
            }
            if (skill.isLevelable()) {
                ((Levelable) skill).getAttachedLevel().setLevel(((Levelable) skill).getMaxLevel());
            }
        }
    }

    public static String getPvPTag(Hero hero, Player viewingPlayer) {

        return getPvPColor(hero, viewingPlayer) + "PvP: " + (hero.isPvPEnabled() ? "an" : "aus");
    }

    public static String getPvPTag(Hero hero) {

        return getPvPTag(hero, null);
    }

    public static ChatColor getPvPColor(Hero hero, Player viewingPlayer) {

        Hero viewer = null;
        if (viewingPlayer != null) {
            try {
                viewer = getHeroFromEntity(viewingPlayer);
            } catch (UnknownPlayerException e) {
                e.printStackTrace();
            }
        }
        if (hero.getParty().getHeroes().size() > 1 && (viewer == null || hero.getParty().contains(viewer))) {
            if (hero.isPvPEnabled()) {
                return ChatColor.DARK_GREEN;
            } else {
                return ChatColor.GREEN;
            }
        } else {
            if (hero.isPvPEnabled()) {
                if (viewer != null && !viewer.isPvPEnabled()) {
                    return ChatColor.GOLD;
                }
                return ChatColor.DARK_RED;
            } else {
                return ChatColor.AQUA;
            }
        }
    }

    /**
     * Gets the horizontal Block Face from a given yaw angle<br>
     * This includes the NORTH_WEST faces
     *
     * @param yaw angle
     *
     * @return The Block Face of the angle
     */
    public static BlockFace yawToFace(float yaw) {

        return yawToFace(yaw, true);
    }

    /**
     * Gets the horizontal Block Face from a given yaw angle
     *
     * @param yaw                      angle
     * @param useSubCardinalDirections setting, True to allow NORTH_WEST to be returned
     *
     * @return The Block Face of the angle
     */
    public static BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {

        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }

    public static List<FancyMessage> getBasicHeroInfo(Hero hero, boolean isTooltip) {

        ArrayList<FancyMessage> messages = new ArrayList<>();
        messages.add(new FancyMessage("Leben: ").color(ChatColor.YELLOW)
                        .then((int) hero.getHealth() + "").color(EntityUtil.getHealthColor(hero.getHealth(), hero.getMaxHealth()))
                        .then("/").color(ChatColor.AQUA)
                        .then((int) hero.getMaxHealth() + "").color(ChatColor.GREEN)
        );
        messages.add(new FancyMessage("Rüstung: ").color(ChatColor.YELLOW)
                        .then(hero.getTotalArmorValue() + "").color(ChatColor.GRAY)
        );
        messages.add(new FancyMessage("EXP Pool: ").color(ChatColor.YELLOW)
                        .then(hero.getExpPool().getExp() + "").color(ChatColor.AQUA)
        );

        messages.add(new FancyMessage("Attribute:").color(ChatColor.YELLOW));
        hero.getAttributes().stream()
                .filter(attribute -> attribute.getCurrentValue() > 0)
                .sorted(Comparator.comparing(Attribute::getFriendlyName)).forEachOrdered(attribute -> {
                    FancyMessage msg = new FancyMessage("| ").color(ChatColor.DARK_PURPLE)
                            .then(attribute.getFriendlyName() + ": ").color(ChatColor.YELLOW)
                            .then(attribute.getCurrentValue() + "");
                    if (attribute.getCurrentValue() > attribute.getBaseValue()) {
                        msg.color(ChatColor.GREEN);
                    } else if (attribute.getCurrentValue() < attribute.getBaseValue()) {
                        msg.color(ChatColor.DARK_RED);
                    } else {
                        msg.color(ChatColor.YELLOW);
                    }
                    messages.add(msg);
                }
        );

        messages.add(new FancyMessage("Resourcen: ").color(ChatColor.YELLOW));
        hero.getResources().stream()
                .filter(Resource::isEnabled)
                .sorted(Comparator.comparing(Resource::getFriendlyName))
                .forEachOrdered(resource -> {
                    FancyMessage msg = new FancyMessage("| ").color(ChatColor.DARK_PURPLE)
                            .then(resource.getFriendlyName() + ": ").color(ChatColor.YELLOW)
                            .then((int) resource.getCurrent() + "").color(EntityUtil.getHealthColor(resource.getCurrent(), resource.getMax()))
                            .then("/").color(ChatColor.YELLOW)
                            .then((int) resource.getMax() + "").color(ChatColor.GREEN);
                    if (resource.getRegenInterval() > 0 && resource.getRegenValue() != 0) {
                        msg.then(" (").color(ChatColor.YELLOW);
                        if (resource.getRegenValue() > 0) {
                            msg.then("+" + resource.getRegenValue()).color(ChatColor.GREEN)
                                    .then("/").color(ChatColor.YELLOW)
                                    .then(TimeUtil.getFormattedTime(TimeUtil.ticksToSeconds(resource.getRegenInterval())))
                                    .color(ChatColor.AQUA);
                        } else {
                            msg.then("-" + -resource.getRegenValue()).color(ChatColor.DARK_RED)
                                    .then("/").color(ChatColor.YELLOW)
                                    .then(TimeUtil.getFormattedTime(TimeUtil.ticksToSeconds(resource.getRegenInterval())))
                                    .color(ChatColor.AQUA);
                        }
                    }
                    messages.add(msg);
                });
        return messages;
    }

    public static List<FancyMessage> getHeroTooltip(Hero hero, Player viewer, boolean isTooltip) {

        ArrayList<FancyMessage> messages = new ArrayList<>();
        messages.add(new FancyMessage("[").color(ChatColor.YELLOW)
                .then(hero.getPlayerLevel() + "").color(ChatColor.AQUA)
                .then("]").color(ChatColor.YELLOW)
                .then(" ").then(hero.getName()).color(getPvPColor(hero, viewer)));

        messages.addAll(getBasicHeroInfo(hero, isTooltip));

        messages.add(new FancyMessage("Klassen & Berufe:").color(ChatColor.YELLOW));
        messages.addAll(hero.getProfessions().stream()
                .map(profession -> new FancyMessage("| ").color(ChatColor.DARK_PURPLE)
                    .then("[").color(ChatColor.YELLOW)
                    .then(profession.getTotalLevel() + "").color(ChatColor.AQUA)
                    .then("]").color(ChatColor.YELLOW)
                    .then(" ").then(profession.getFriendlyName()).color(profession.isActive() ? ChatColor.GREEN : ChatColor.GRAY))
                .collect(Collectors.toList()));

        return messages;
    }
}
