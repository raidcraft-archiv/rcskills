package de.raidcraft.skills.api.ui;

import de.raidcraft.skills.api.character.CharacterTemplate;

/**
 * @author Silthus
 */
public interface HealthDisplay {

    public CharacterTemplate getCharacter();

    public void refresh();

    public void remove();
}
