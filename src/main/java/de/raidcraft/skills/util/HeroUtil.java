package de.raidcraft.skills.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

/**
 * @author Silthus
 */
public final class HeroUtil {

    private HeroUtil() {}

    public static String createManaBar(double mana, double maxMana) {

        StringBuilder manaBar = new StringBuilder(String.valueOf(ChatColor.RED) + "[" + ChatColor.BLUE);
        int percent = (int)((mana / maxMana) * 100.0);
        int progress = percent / 2;
        for (int i = 0; i < progress; i++) {
            manaBar.append('|');
        }
        manaBar.append(ChatColor.DARK_RED);
        for (int i = 0; i < 50 - progress; i++) {
            manaBar.append('|');
        }
        manaBar.append(ChatColor.RED).append(']');
        return String.valueOf(manaBar) + " - " + ChatColor.BLUE + percent + "%";
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
}
