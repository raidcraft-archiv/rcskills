package de.raidcraft.skills.api.persistance;

import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;

import java.util.Set;

/**
 * @author Silthus
 */
public interface HeroData {

    public int getId();

    public String getName();

    public Set<Skill> getSkills();

    public Set<Profession> getProfessions();

    public Profession getSelectedProfession();
}
