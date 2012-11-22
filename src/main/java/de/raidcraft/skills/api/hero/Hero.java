package de.raidcraft.skills.api.hero;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Hero extends Levelable<Hero>, RCPlayer {

    public int getId();

    public Profession getSelectedProfession();

    public Collection<Skill> getSkills();

    public Collection<Skill> getUnlockedSkills();

    public Collection<Profession> getProfessions();

    public void saveSkills();

    public void save();

    public boolean hasSkill(Skill skill);

    public boolean hasSkill(String id);

    public Skill getSkill(String id) throws UnknownSkillException;

    public boolean hasProfession(Profession profession);

    public boolean hasProfession(String id);

    public Profession getProfession(String id) throws UnknownSkillException, UnknownProfessionException;
}