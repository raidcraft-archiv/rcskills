package de.raidcraft.skills.api.skill;

import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.RCHero;
import de.raidcraft.skills.SkillsComponent;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.tables.skills.PlayerSkillsTable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
public final class SkillManager {

    private final SkillsComponent component;
    private final SkillFactory factory;
    /**
     * This map contains all informational skills that are not attached to a player.
     */
    private final Map<Integer, Skill> skills = new HashMap<>();
    /**
     * This map only contains player skills that the player does not currently have.
     * The cache of this map will be cleared when the player logs out.
     */
    private final Map<String, Map<Integer, Skill>> playerSkills = new HashMap<>();

    public SkillManager(SkillsComponent component) {

        this.component = component;
        this.factory = new SkillFactory(component);
    }

    /**
     * If asking for a {@link Skill} with the ID only it should always be
     * a template skill you want because there is no way of knowing
     * what player the skill is attached to.
     * <p/>
     * {@link TemplateSkill} is a skill that is for information only
     * like before buying the skill and cannot be interacted with.
     *
     * @param id of the skill
     *
     * @return {@link TemplateSkill}
     *
     * @throws de.raidcraft.skills.api.exceptions.UnknownSkillException is thrown if the skill is not configured
     */
    public Skill getSkill(int id) throws UnknownSkillException {

        Skill skill;
        if (skills.containsKey(id)) {
            skill = skills.get(id);
        } else {
            skill = factory.createTemplateSkill(id);
            skills.put(id, skill);
        }
        return skill;
    }

    /**
     * This will try to get a {@link Skill} linked to a {@link de.raidcraft.skills.RCHero}, but not currently
     * owned by the player. This can happen when the player dropped a {@link de.raidcraft.skills.api.Levelable} skill but
     * still has saved progress for that skill in the database.
     *
     * @param id     of the skill
     * @param player that Skill was attached to
     *
     * @return {@link Skill} that is most likely a sub skill of {@link de.raidcraft.skills.api.Levelable}
     *
     * @throws UnknownSkillException is thrown if the skill is not registered with the player
     */
    public Skill getSkill(int id, RCPlayer player) throws UnknownSkillException {

        Skill skill;
        RCHero hero = player.getComponent(RCHero.class);
        // lets check the players skills first
        if (hero.hasSkill(id)) {
            skill = hero.getSkill(id);
        } else {
            // okay the player does not seem to have the skill at the moment
            // but he could still have bought it one time and dropped it again
            // the exp and level of the skill still would be accessible
            if (playerSkills.containsKey(player.getUserName())
                    && playerSkills.get(player.getUserName()).containsKey(id)) {
                // wonderful the skill is cached, so lets return it
                skill = playerSkills.get(player.getUserName()).get(id);
            } else {
                // skill is not cached so we need to create it from the database
                // if the skill does not exist an exception will be thrown
                skill = factory.createSkill(id, player);
                // lets add it to the cache
                if (!playerSkills.containsKey(player.getUserName())) {
                    playerSkills.put(player.getUserName(), new HashMap<Integer, Skill>());
                }
                playerSkills.get(player.getUserName()).put(id, skill);
            }
        }
        return skill;
    }


    public Skill getPlayerSkill(int id, RCPlayer player) throws UnknownSkillException {

        Skill skill;
        RCHero hero = player.getComponent(RCHero.class);
        if (hero.hasSkill(id)) {
            skill = hero.getSkill(id);
        } else {
            // lets look into the database if the player has the skill
            if (Database.getTable(PlayerSkillsTable.class).contains(id, player)) {
                skill = factory.createSkill(id, player);
            } else {
                throw new UnknownSkillException("Der Spieler " + player.getUserName() + " hat keinen Skill mit der ID: " + id);
            }
        }
        return skill;
    }

    public Collection<Skill> getAllSkills() {

        return skills.values();
    }
}
