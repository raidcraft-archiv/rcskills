package de.raidcraft.skills.api.hero;

import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.group.Group;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.path.Path;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.ui.UserInterface;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public interface Hero extends Levelable<Hero>, CharacterTemplate {

    public int getId();

    public RCPlayer getRCPlayer();

    public Player getPlayer();

    public Level<Hero> getExpPool();

    public UserInterface getUserInterface();

    public Group getGroup();

    public HeroOptions getOptions();

    public boolean isInGroup(Group group);

    public void joinGroup(Group group);

    public void leaveGroup(Group group);

    public void debug(String message);

    public void combatLog(String message);

    public void combatLog(Object o, String message);

    public void reset();

    public Resource getResource(String type);

    public Set<Resource> getResources();

    public Set<Resource> getResources(Profession profession);

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

    public Material getItemTypeInHand();
}
