package de.raidcraft.skills.api.persistance;

import java.util.List;

/**
 * @author Silthus
 */
public interface HeroData {

    public int getId();

    public String getName();

    public int getMaxLevel();

    public List<String> getProfessionNames();

    public LevelData getLevelData();
}
