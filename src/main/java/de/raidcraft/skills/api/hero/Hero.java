package de.raidcraft.skills.api.hero;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Collection;

/**
 * @author Silthus
 */
public interface Hero extends Levelable {

    public RCPlayer getPlayer();

    public String getName();

    public boolean hasSkill(Skill skill);

    public Profession getSelectedProfession();

    public Collection<Profession> getActiveProfessions();

    public Collection<Profession> getProfessions();

    public Collection<Skill> getSpecialSkills();

    public void saveSkills();

    public void save();
}
