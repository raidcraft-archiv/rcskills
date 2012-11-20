package de.raidcraft.skills.professions;

import de.raidcraft.skills.api.persistance.ProfessionData;
import de.raidcraft.skills.api.profession.AbstractProfession;

/**
 * @author Silthus
 */
public final class TemplateProfession extends AbstractProfession {

    protected TemplateProfession(ProfessionData data) {

        super(null, null, data);
    }

    @Override
    public void saveLevelProgress() {
        // do nothing, this is just a template to display information
    }
}
