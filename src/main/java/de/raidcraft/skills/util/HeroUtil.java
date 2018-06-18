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
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.*;

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

    public static void removeEntityMetaData(LivingEntity entity, String key) {

        entity.removeMetadata(key, RaidCraft.getComponent(SkillsPlugin.class));
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

    public static boolean isCachedHero(UUID uuid) {

        return RaidCraft.getComponent(SkillsPlugin.class).getCharacterManager().isPlayerCached(uuid);
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

    public static List<BaseComponent> getBasicHeroInfo(Hero hero, boolean isTooltip) {

        ArrayList<BaseComponent> messages = new ArrayList<>();
        messages.addAll(Arrays.asList(new ComponentBuilder("Leben: ").color(ChatColor.YELLOW.asBungee())
                .append((int) hero.getHealth() + "").color(EntityUtil.getHealthColor(hero.getHealth(), hero.getMaxHealth()).asBungee())
                .append("/").color(ChatColor.AQUA.asBungee())
                .append((int) hero.getMaxHealth() + "").color(ChatColor.GREEN.asBungee()).create())
        );
        messages.addAll(Arrays.asList(new ComponentBuilder("Rüstung: ").color(ChatColor.YELLOW.asBungee())
                .append(hero.getTotalArmorValue() + "").color(ChatColor.GRAY.asBungee()).create())
        );
        messages.addAll(Arrays.asList(new ComponentBuilder("EXP Pool: ").color(ChatColor.YELLOW.asBungee())
                .append(hero.getExpPool().getExp() + "").color(ChatColor.AQUA.asBungee()).create())
        );

        messages.addAll(Arrays.asList(new ComponentBuilder("Attribute:").color(ChatColor.YELLOW.asBungee()).create()));
        hero.getAttributes().stream()
                .filter(attribute -> attribute.getCurrentValue() > 0)
                .sorted(Comparator.comparing(Attribute::getFriendlyName)).forEachOrdered(attribute -> {
            ComponentBuilder msg = new ComponentBuilder("| ").color(ChatColor.DARK_PURPLE.asBungee())
                    .append(attribute.getFriendlyName() + ": ").color(ChatColor.YELLOW.asBungee())
                    .append(attribute.getCurrentValue() + "");
                    if (attribute.getCurrentValue() > attribute.getBaseValue()) {
                        msg.color(ChatColor.GREEN.asBungee());
                    } else if (attribute.getCurrentValue() < attribute.getBaseValue()) {
                        msg.color(ChatColor.DARK_RED.asBungee());
                    } else {
                        msg.color(ChatColor.YELLOW.asBungee());
                    }
            messages.addAll(Arrays.asList(msg.create()));
                }
        );

        messages.addAll(Arrays.asList(new ComponentBuilder("Resourcen: ").color(ChatColor.YELLOW.asBungee()).create()));
        hero.getResources().stream()
                .filter(Resource::isEnabled)
                .sorted(Comparator.comparing(Resource::getFriendlyName))
                .forEachOrdered(resource -> {
                    ComponentBuilder msg = new ComponentBuilder("| ").color(ChatColor.DARK_PURPLE.asBungee())
                            .append(resource.getFriendlyName() + ": ").color(ChatColor.YELLOW.asBungee())
                            .append((int) resource.getCurrent() + "").color(EntityUtil.getHealthColor(resource.getCurrent(), resource.getMax()).asBungee())
                            .append("/").color(ChatColor.YELLOW.asBungee())
                            .append((int) resource.getMax() + "").color(ChatColor.GREEN.asBungee());
                    if (resource.getRegenInterval() > 0 && resource.getRegenValue() != 0) {
                        msg.append(" (").color(ChatColor.YELLOW.asBungee());
                        if (resource.getRegenValue() > 0) {
                            msg.append("+" + resource.getRegenValue()).color(ChatColor.GREEN.asBungee())
                                    .append("/").color(ChatColor.YELLOW.asBungee())
                                    .append(TimeUtil.getFormattedTime(TimeUtil.ticksToSeconds(resource.getRegenInterval())))
                                    .color(ChatColor.AQUA.asBungee());
                        } else {
                            msg.append("-" + -resource.getRegenValue()).color(ChatColor.DARK_RED.asBungee())
                                    .append("/").color(ChatColor.YELLOW.asBungee())
                                    .append(TimeUtil.getFormattedTime(TimeUtil.ticksToSeconds(resource.getRegenInterval())))
                                    .color(ChatColor.AQUA.asBungee());
                        }
                    }
                    messages.addAll(Arrays.asList(msg.create()));
                });
        return messages;
    }

    public static List<BaseComponent> getHeroTooltip(Player hero, Player viewer, boolean isTooltip) {

        return getHeroTooltip(RaidCraft.getComponent(CharacterManager.class).getHero(hero), viewer, isTooltip);
    }

    public static List<BaseComponent> getHeroTooltip(Hero hero, Player viewer, boolean isTooltip) {

        ArrayList<BaseComponent> messages = new ArrayList<>();
        messages.addAll(Arrays.asList(new ComponentBuilder("[").color(ChatColor.YELLOW.asBungee())
                .append(hero.getPlayerLevel() + "").color(ChatColor.AQUA.asBungee())
                .append("]").color(ChatColor.YELLOW.asBungee())
                .append(" ").append(hero.getName()).color(getPvPColor(hero, viewer).asBungee()).create()));

        messages.addAll(getBasicHeroInfo(hero, isTooltip));

        messages.addAll(Arrays.asList(new ComponentBuilder("Klassen & Berufe:").color(ChatColor.YELLOW.asBungee()).create()));

        hero.getProfessions().stream()
                .filter(Profession::isActive)
                .map(profession -> new ComponentBuilder("| ").color(ChatColor.DARK_PURPLE.asBungee())
                        .append("[").color(ChatColor.YELLOW.asBungee())
                        .append(profession.getTotalLevel() + "").color(ChatColor.AQUA.asBungee())
                        .append("]").color(ChatColor.YELLOW.asBungee())
                        .append(" ").append(profession.getFriendlyName()).color(profession.isActive() ? ChatColor.GREEN.asBungee() : ChatColor.GRAY.asBungee()).create())
                .forEach(baseComponents -> messages.addAll(Arrays.asList(baseComponents)));

        return messages;
    }

    public static void sendActionBar(Player player, String message){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }
}
