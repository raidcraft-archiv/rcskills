package de.raidcraft.skills.api;

import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.Location;

/**
 * @author Silthus
 */
public interface AreaAttack extends Active<Location> {

    @Override
    public Skill.Result run(Hero hero, Location location) throws CombatException;
}
