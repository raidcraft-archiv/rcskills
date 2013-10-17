package de.raidcraft.skills.api.persistance;

import java.util.List;

/**
 * @author Silthus
 */
public interface HeroData {

    public int getId();

    public String getName();

    public String getSelectedProfession();

    public double getHealth();

    public int getMaxLevel();

    public List<String> getProfessionNames();

    public LevelData getLevelData();

    public LevelData getExpPool();
}
