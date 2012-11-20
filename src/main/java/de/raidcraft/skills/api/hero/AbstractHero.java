package de.raidcraft.skills.api.hero;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.database.Database;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.SkillsComponent;
import de.raidcraft.skills.api.AbstractLevelable;
import de.raidcraft.skills.api.Levelable;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.persistance.LevelData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.skills.PlayerSkillsTable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractHero extends AbstractLevelable implements Hero {

    private final RCPlayer player;
    // holds a list of all special skills the player gained mapped by the id of the skill
    // special skills do not require a profession and can only be given manually
    private final Map<Integer, Skill> specialSkills = new HashMap<>();
    // holds a list of all professions the player ever selected mapped by the string id
    // you still need to check if the profession is currently active or mastered
    private final Map<String, Profession> professions = new HashMap<>();
    // this just tells the client what to display in the experience bar and so on
    private Profession selectedProfession;

    protected AbstractHero(HeroData heroData, LevelData data) {

        super(data);
        this.player = heroData.getPlayer();
        this.selectedProfession = heroData.getSelectedProfession();
    }

    @Override
    public Hero getHero() {
        return this;
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

        saveLevelProgress();
        saveSkills();
    }

    @Override
    public final void saveSkills() {

        for (Skill skill : getSpecialSkills()) {
            if (skill instanceof Levelable) {
                ((Levelable) skill).saveLevelProgress();
            }
        }
    }

    public boolean hasSkill(int id) {

        if (getPlayer().isOnline()) {
            return specialSkills.containsKey(id);
        } else {
            return Database.getTable(PlayerSkillsTable.class).contains(id, getPlayer());
        }
    }

    @Override
    public boolean hasSkill(Skill skill) {

        return hasSkill(skill.getId());
    }

    public Skill getSkill(int id) throws UnknownSkillException {

        Skill skill;
        if (specialSkills.containsKey(id)) {
            skill = specialSkills.get(id);
        } else {
            skill = RaidCraft.getComponent(SkillsComponent.class).getSkillManager().getPlayerSkill(id, getPlayer());
            specialSkills.put(skill.getId(), skill);
        }
        return skill;
    }

    @Override
    public Collection<Skill> getSpecialSkills() {

        return specialSkills.values();
    }

    @Override
    public Collection<Profession> getProfessions() {

        return professions.values();
    }

    @Override
    public Collection<Profession> getActiveProfessions() {

        HashSet<Profession> set = new HashSet<>();
        for (Profession profession : getProfessions()) {
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
    public boolean equals(Object obj) {

        return obj instanceof Hero && ((Hero) obj).getName().equals(getName());
    }
}
