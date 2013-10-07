package de.raidcraft.skills.tabdeco;

import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.tabdeco.api.TabDecoSetting;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public class TabDecoResourceSettings extends TabDecoSetting {

    private static final Pattern RESOURCE_PATTERN = Pattern.compile(".*\\[resource([a-zA-Z0-9]+)\\(([a-zA-Z0-9]+)\\)\\].*");

    private final SkillsPlugin plugin;

    public TabDecoResourceSettings(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getSlotText(Player player, String inputText, String settingName) {

        Hero hero = plugin.getCharacterManager().getHero(player);
        Matcher matcher = RESOURCE_PATTERN.matcher(inputText);
        if (matcher.matches()) {
            String group = matcher.group(2);
            Resource resource;
            if (!group.equalsIgnoreCase("primary") || !group.equalsIgnoreCase("secondary")) {
                resource = hero.getResource(group);
            } else {
                resource = getResource(hero, group.equalsIgnoreCase("primary"));
            }
            // lets get the resource first
            if (resource == null) {
                return "";
            }
            // now check what value we want
            String action = matcher.group(1);
            if (action.equalsIgnoreCase("name")) {
                return resource.getFriendlyName() + ": ";
            } else if (action.equalsIgnoreCase("value")) {
                return resource.getCurrent() + "/" + resource.getMax();
            }
        }
        return "";
    }

    private Resource getResource(Hero hero, boolean primary) {

        for (Resource resource : hero.getResources()) {
            if (resource.getName().equalsIgnoreCase("health")) {
                continue;
            }
            if (!resource.isEnabled() || !resource.getProfession().isActive()) {
                continue;
            }
            if (resource.getProfession().hasChildren() && primary) {
                return resource;
            }
            if (!resource.getProfession().hasChildren() && !primary) {
                return resource;
            }
        }
        return null;
    }
}
