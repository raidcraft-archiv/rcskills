package de.raidcraft.skills.api.persistance;

import java.util.List;

/**
 * @author Silthus
 */
public interface HeroData {

    public int getId();

    public String getName();

    public String getSelectedProfession();

    public int getHealth();

    public int getMana();

    public int getStamina();

    public boolean isDebugging();

    public boolean isCombatLogging();

    public int getMaxLevel();

    public List<String> getProfessionNames();

    public LevelData getLevelData();
}
