package de.raidcraft.skills.api.persistance;

import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
public interface HeroData {

    public int getId();

    public UUID getPlayerId();

    public String getSelectedProfession();

    public double getHealth();

    public int getMaxLevel();

    public List<String> getProfessionNames();

    public LevelData getLevelData();

    public LevelData getExpPool();
}
