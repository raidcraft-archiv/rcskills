package de.raidcraft.skills.items;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.items.CustomItemException;
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
    public void loadAttachment(ConfigurationSection data) {

        this.requiredLevel = data.getInt("level", 1);
    }

    @Override
    public void applyAttachment(Player player) throws CustomItemException {


    }

    @Override
    public void removeAttachment(Player player) throws CustomItemException {


    }

    @Override
    public String getName() {

        return "level";
    }

    @Override
    public boolean isRequirementMet(Player player) {

        return requiredLevel <= characterManager.getHero(player).getPlayerLevel();
    }

    @Override
    public String getItemText() {

        return "Benötigt mind. Level " + requiredLevel;
    }

    @Override
    public String getErrorMessage() {

        return "Du benötigst mindestens Level " + requiredLevel + " um dieses Item zu tragen.";
    }
}