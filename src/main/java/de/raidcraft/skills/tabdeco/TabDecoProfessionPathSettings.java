package de.raidcraft.skills.tabdeco;

import TCB.TabDeco.API.TabDecoSetting;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.profession.Profession;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public class TabDecoProfessionPathSettings extends TabDecoSetting {

    private static final Pattern PROFESSION_PATTERN = Pattern.compile(".*\\[profession([a-zA-Z0-9]+)\\(([a-zA-Z0-9]+)\\)\\].*");

    private final SkillsPlugin plugin;

    public TabDecoProfessionPathSettings(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getSlotText(Player player, String inputText, String settingName) {

        Hero hero = plugin.getCharacterManager().getHero(player);
        Matcher matcher = PROFESSION_PATTERN.matcher(inputText);
        if (matcher.matches()) {
            // lets get the profession first
            Profession profession = getProfession(matcher.group(2), hero.getProfessions());
            if (profession == null) {
                return "N/A";
            }
            // now check what value we want
            String action = matcher.group(1);
            if (action.equalsIgnoreCase("name")) {
                return profession.getFriendlyName();
            } else if (action.equalsIgnoreCase("level")) {
                return profession.getAttachedLevel().getLevel() + "/" + profession.getAttachedLevel().getMaxLevel();
            } else if (action.equalsIgnoreCase("exp")) {
                return profession.getAttachedLevel().getExp() + "/" + profession.getAttachedLevel().getMaxExp();
            }
        }
        return "N/A";
    }

    private Profession getProfession(String pathName, Collection<Profession> professions) {

        for (Profession profession : professions) {
            if (profession.isActive() && profession.getPath().getName().equalsIgnoreCase(pathName)) {
                if (profession.hasChildren()) {
                    Profession child = getProfession(pathName, profession.getChildren());
                    if (child.isActive()) {
                        return child;
                    } else {
                        return profession;
                    }
                } else {
                    return profession;
                }
            }
        }
        return null;
    }
}
