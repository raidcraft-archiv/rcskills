package de.raidcraft.skills.api.hero;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.profession.LevelableProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.skills.PlayerSkillsTable;

import java.util.*;

/**
 * @author Silthus
 */
public abstract class AbstractHero implements Hero {

    private final RCPlayer player;
    private Level<Hero> level;
    // holds a list of all special skills the player gained mapped by the id of the skill
    // special skills do not require a profession and can only be given manually
    private final Map<String, Skill> specialSkills = new HashMap<>();
    // holds a list of all professions the player ever selected mapped by the string id
    // you still need to check if the profession is currently active or mastered
    private final Map<String, LevelableProfession> professions = new HashMap<>();
    // this just tells the client what to display in the experience bar and so on
    private Profession selectedProfession;

    protected AbstractHero(HeroData heroData) {

        this.player = heroData.getPlayer();
        this.selectedProfession = heroData.getSelectedProfession();
    }

    @Override
    public void attachLevel(Level<Hero> level) {

        this.level = level;
    }

    @Override
    public RCPlayer getPlayer() {

        return player;
    }

    @Override
    public String getName() {

        return player.getUserName();
    }

    @Override
    public final void save() {

        saveLevelProgress(level);
        saveSkills();
    }

    @Override
    public final void saveSkills() {

        for (Skill skill : getSpecialSkills()) {
            if (skill instanceof Level) {
                ((Level) skill).saveLevelProgress();
            }
        }
    }

    @Override
    public boolean hasSkill(String id) {

        if (getPlayer().isOnline()) {
            return specialSkills.containsKey(id);
        } else {
            return Database.getTable(PlayerSkillsTable.class).contains(id, getPlayer());
        }
    }

    @Override
    public boolean hasSkill(Skill skill) {

        return hasSkill(skill.getName());
    }

    public Skill getSkill(String id) throws UnknownSkillException {

        Skill skill;
        if (specialSkills.containsKey(id)) {
            skill = specialSkills.get(id);
        } else {
            skill = RaidCraft.getComponent(SkillsPlugin.class).getSkillManager().getPlayerSkill(id, getPlayer());
            specialSkills.put(skill.getName(), skill);
        }
        return skill;
    }

    @Override
    public Collection<Skill> getSpecialSkills() {

        return specialSkills.values();
    }

    @Override
    public Collection<Skill> getGainedSkills() {

        Set<Skill> skills = new HashSet<>();
        for (LevelableProfession profession : getProfessions()) {
            if (profession.isActive()) {
                skills.addAll(profession.getGainedSkills());
            }
        }
        return skills;
    }

    @Override
    public Collection<LevelableProfession> getProfessions() {

        return professions.values();
    }

    @Override
    public Collection<LevelableProfession> getActiveProfessions() {

        HashSet<LevelableProfession> set = new HashSet<>();
        for (LevelableProfession profession : getProfessions()) {
            if (profession.isActive()) {
                set.add(profession);
            }
        }
        return set;
    }

    @Override
    public Profession getSelectedProfession() {

        return selectedProfession;
    }

    @Override
    public Level<Hero> getLevel() {

        return level;
    }

    @Override
    public boolean equals(Object obj) {

        return obj instanceof Hero && ((Hero) obj).getName().equals(getName());
    }
}
