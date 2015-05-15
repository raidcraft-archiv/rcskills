package de.raidcraft.skills.api.persistance;

import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
public interface HeroData {

    int getId();

    UUID getPlayerId();

    String getSelectedProfession();

    double getHealth();

    int getMaxLevel();

    List<String> getProfessionNames();

    LevelData getLevelData();

    LevelData getExpPool();
}
