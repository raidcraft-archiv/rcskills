package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.tables.THeroProfession;

import java.util.List;

/**
 * @author Silthus
 */
public interface HeroData {

    public int getId();

    public String getName();

    public int getMaxLevel();

    public THeroProfession getSelectedProfession();

    public List<String> getProfessionNames();

    public List<String> getSkillNames();

    public LevelData getLevelData();
}
