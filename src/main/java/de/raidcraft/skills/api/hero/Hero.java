package de.raidcraft.skills.api.hero;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.level.LevelObject;
import de.raidcraft.skills.api.profession.LevelableProfession;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Hero extends LevelObject<Hero> {

    public RCPlayer getPlayer();

    public String getName();

    public boolean hasSkill(Skill skill);

    public boolean hasSkill(String id);

    public Profession getSelectedProfession();

    public Collection<LevelableProfession> getActiveProfessions();

    public Collection<LevelableProfession> getProfessions();

    public Collection<Skill> getSpecialSkills();

    public Collection<Skill> getGainedSkills();

    public void saveSkills();

    public void save();
}
