package de.raidcraft.skills.tabdeco;

import TCB.TabDeco.API.TabDecoSetting;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Attribute;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public class TabDecoAttributeSettings extends TabDecoSetting {

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(".*\\[attribute\\(([a-zA-Z0-9]+)\\)\\].*");

    private final SkillsPlugin plugin;

    public TabDecoAttributeSettings(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getSlotText(Player player, String inputText, String settingName) {

        Attribute attribute = getAttribute(player, inputText);
        if (attribute != null) {
            return String.valueOf(attribute.getCurrentValue());
        }
        return "";
    }

    public Attribute getAttribute(Player player, String input) {

        Hero hero = plugin.getCharacterManager().getHero(player);
        Matcher matcher = ATTRIBUTE_PATTERN.matcher(input);
        if (matcher.matches()) {
            return hero.getAttribute(matcher.group(1));
        }
        return null;
    }
}
