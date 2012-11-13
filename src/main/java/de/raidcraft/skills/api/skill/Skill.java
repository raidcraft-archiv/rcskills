package de.raidcraft.skills.api.skill;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public interface Skill {

    public enum Type {

        GAINABLE,
        BUYABLE,
        ADMIN;

        public static Type fromString(String name) {

            return EnumUtils.getEnumFromString(Type.class, name);
        }
    }

    public int getId();

    public String getName();

    public String getDescription();

    public String[] getUsage();

    public Type getType();

    public boolean hasUsePermission(RCPlayer player);

    public boolean hasBuyPermission(RCPlayer player);

    public boolean hasGainPermission(RCPlayer player);

    public double getCost();
}
