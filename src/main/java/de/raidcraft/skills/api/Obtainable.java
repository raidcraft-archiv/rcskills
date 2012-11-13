package de.raidcraft.skills.api;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.util.EnumUtils;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Obtainable {

    public enum Type {

        GAINABLE,
        BUYABLE,
        ADMIN;

        public static Type fromString(String name) {

            return EnumUtils.getEnumFromString(Type.class, name);
        }
    }

    public Type getType();

    public boolean hasBuyPermission(RCPlayer player);

    public boolean hasGainPermission(RCPlayer player);

    public double getCost();

    public int getNeededLevel();

    public Collection<Profession> getNeededProfessions();

    public boolean areAllProfessionsRequired();
}
