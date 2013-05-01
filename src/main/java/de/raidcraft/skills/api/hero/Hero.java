package de.raidcraft.skills.api.hero;

import de.raidcraft.skills.api.character.SkilledCharacter;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.AttachedLevel;
import de.raidcraft.skills.api.party.Party;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.ui.UserInterface;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public interface Hero extends SkilledCharacter<Skill> {

    public int getId();

    public Player getPlayer();

    public AttachedLevel<Hero> getExpPool();

    public UserInterface getUserInterface();

    public Collection<Attribute> getAttributes();

    public Attribute getAttribute(String attribute);

    public Party getPendingPartyInvite();

    public void setPendingPartyInvite(Party partyInvite);

    public int getAttributeValue(String attribute);

    public void setAttributeValue(String attribute, int value);

    public HeroOptions getOptions();

    public void debug(String message);

    public void combatLog(String message);

    public void combatLog(Object o, String message);

    public void reset();

    public Resource getResource(String name);

    public boolean hasResource(String name);

    public void attachResource(Resource resource);

    public Resource detachResource(String name);

    public Set<Resource> getResources();

    public Profession getSelectedProfession();

    public void setSelectedProfession(Profession profession);

    public Profession getVirtualProfession();

    public Set<Path<Profession>> getPaths();

    public void changeProfession(Profession profession);

    public List<Skill> getSkills();

    public Skill getSkill(String name) throws UnknownSkillException;

    public List<Profession> getProfessions();

    public void saveProfessions();

    public void saveSkills();

    public void save();

    public boolean hasSkill(Skill skill);

    public boolean hasSkill(String id);

    public boolean hasProfession(Profession profession);

    public boolean hasProfession(String id);

    public Profession getProfession(String id) throws UnknownSkillException, UnknownProfessionException;

    public void sendMessage(String... messages);

    public void addSkill(Skill skill);

    public void removeSkill(Skill skill);

}
