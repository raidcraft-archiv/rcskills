package de.raidcraft.skills.api.ui;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.hero.Hero;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

/**
 * @author Silthus
 */
public class PartyHealthDisplay implements HealthDisplay {

    private final Hero holder;
    private final CharacterTemplate characterTemplate;
    private final OfflinePlayer scoreName;

    public PartyHealthDisplay(Hero holder, CharacterTemplate characterTemplate) {

        this.holder = holder;
        this.characterTemplate = characterTemplate;
        this.scoreName = Bukkit.getOfflinePlayer(ChatColor.BLUE + characterTemplate.getName());
        holder.attachHealthDisplay(this);
    }

    public Hero getHolder() {

        return holder;
    }

    @Override
    public CharacterTemplate getCharacter() {

        return characterTemplate;
    }

    @Override
    public void refresh() {

        getHolder().getUserInterface().updateSidebarScore(scoreName, (int) getCharacter().getHealth());
    }

    @Override
    public void remove() {

        getHolder().getUserInterface().removeSidebarScore(scoreName);
        getHolder().removeHealthDisplay(this);
    }
}
