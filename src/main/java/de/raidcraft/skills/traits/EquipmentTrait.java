package de.raidcraft.skills.traits;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.traits.CharacterTrait;
import de.raidcraft.skills.api.traits.CharacterTraitInformation;

@CharacterTraitInformation("equipment")
public class EquipmentTrait extends CharacterTrait<CharacterTemplate> {
    protected EquipmentTrait() {
        super("equipment");
    }


}
