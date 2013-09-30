package de.raidcraft.skills.items;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
import de.raidcraft.api.items.CustomItemStack;
import de.raidcraft.api.items.attachments.RequiredItemAttachment;
import de.raidcraft.skills.CharacterManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class LevelRequirementAttachment implements RequiredItemAttachment {

    private final CharacterManager characterManager;
    private int requiredLevel;

    public LevelRequirementAttachment() {

        this.characterManager = RaidCraft.getComponent(CharacterManager.class);
    }

    @Override
    public String getName() {

        return "level";
    }

    @Override
    public boolean isRequirementMet(Player player) {

        return requiredLevel < characterManager.getHero(player).getPlayerLevel();
    }

    @Override
    public String getItemText(Player player) {

        return "Benötigt mind. Level " + requiredLevel;
    }

    @Override
    public String getErrorMessage(Player player) {

        return "Du benötigst mindestens Level " + requiredLevel + " um dieses Item zu tragen.";
    }

    @Override
    public void applyAttachment(CustomItemStack item, Player player, ConfigurationSection args) throws CustomItemException {

        this.requiredLevel = args.getInt("level", 1);
    }

    @Override
    public void removeAttachment(CustomItemStack item, Player player, ConfigurationSection args) throws CustomItemException {


    }
}
