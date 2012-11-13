package de.raidcraft.skills.api.skill;

import com.sk89q.commandbook.CommandBook;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.SkillsComponent;
import de.raidcraft.skills.api.Levelable;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.tables.SkillsTable;
import org.bukkit.ChatColor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class SkillFactory {

    private final SkillsComponent component;
    private final SkillsTable table;

    private final Map<Integer, Constructor<? extends Skill>> cachedSkills = new HashMap<>();

    protected SkillFactory(SkillsComponent component) {

        this.component = component;
        this.table = Database.getTable(SkillsTable.class);
    }

    /**
     * Will create a template skill based on the given ID.
     * A {@link TemplateSkill} is only used for information
     * and cannot be attached to a player.
     *
     * @param id of the skill
     *
     * @return {@link Skill}
     *
     * @throws UnknownSkillException
     */
    protected Skill createTemplateSkill(int id) throws UnknownSkillException {

        if (table.contains(id)) {
            return new TemplateSkill(id);
        }
        throw new UnknownSkillException("Es gibt keinen Skill mit der ID: " + ChatColor.AQUA + id);
    }

    /**
     * Will try to create an instance of the skill via reflection based on the
     * class name provided in the database.
     *
     * @param id     of the skill
     * @param player that owned the skill
     *
     * @return {@link Skill}
     *
     * @throws UnknownSkillException
     */
    protected Skill createSkill(int id, RCPlayer player) throws UnknownSkillException {

        try {
            if (table.contains(id)) {
                // lets check the cache first
                Constructor<? extends Skill> sConstructor;
                // lets get the class via reflection from cache or the database via name
                if (cachedSkills.containsKey(id)) {
                    sConstructor = cachedSkills.get(id);
                } else {
                    // okay we dont have it cached so lets do some reflection
                    Class<? extends Skill> sClass = table.getSkillClass(id);
                    // lets check how many parameters our skill takes
                    if (Levelable.class.isAssignableFrom(sClass)) {
                        // levelable skills are bound to a player and take the ID and RCPlayer
                        sConstructor = sClass.getConstructor(int.class, RCPlayer.class);
                    } else {
                        // we have a skill that only takes an id as argument
                        sConstructor = sClass.getConstructor(int.class);
                    }
                    cachedSkills.put(id, sConstructor);
                }
                sConstructor.setAccessible(true);
                switch (sConstructor.getParameterTypes().length) {
                    // lets create the instance based on the count of params
                    // if the skills constructor signature does not match an exception will be thrown
                    case 1:
                        return sConstructor.newInstance(id);
                    case 2:
                        return sConstructor.newInstance(id, player);
                }
            }
        } catch (NoSuchMethodException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        } catch (InstantiationException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            CommandBook.logger().severe(e.getMessage());
            e.printStackTrace();
        }
        throw new UnknownSkillException(
                "Es gibt keinen Skill mit der ID " + ChatColor.AQUA + id + ChatColor.RED + " für den Spieler " + ChatColor.AQUA + player);
    }
}
