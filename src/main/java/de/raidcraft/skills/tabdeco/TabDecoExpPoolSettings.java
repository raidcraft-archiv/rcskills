package de.raidcraft.skills.tabdeco;

import TCB.TabDeco.API.TabDecoSetting;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.hero.Option;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class TabDecoExpPoolSettings extends TabDecoSetting {

    private final SkillsPlugin plugin;

    public TabDecoExpPoolSettings(SkillsPlugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public String getSlotText(Player player, String inputText, String settingName) {

        Hero hero = plugin.getCharacterManager().getHero(player);
        if (settingName.equalsIgnoreCase("expPool")) {
            return String.valueOf(hero.getExpPool().getExp());
        } else if (settingName.equalsIgnoreCase("expPoolLink") && Option.EXP_POOL_LINK.get(hero) != null) {
            try {
                return hero.getProfession(Option.EXP_POOL_LINK.get(hero)).getFriendlyName();
            } catch (UnknownSkillException | UnknownProfessionException ignored) {
            }
        }
        return "";
    }
}
