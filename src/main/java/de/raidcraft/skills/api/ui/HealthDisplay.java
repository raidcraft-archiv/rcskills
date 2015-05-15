package de.raidcraft.skills.api.ui;

import de.raidcraft.skills.api.character.CharacterTemplate;

/**
 * @author Silthus
 */
public interface HealthDisplay {

    CharacterTemplate getCharacter();

    void refresh();

    void remove();
}
