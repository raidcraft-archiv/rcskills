package de.raidcraft.skills.api.hero;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.group.Group;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.ui.UserInterface;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
public interface Hero extends Levelable<Hero>, CharacterTemplate {

    public int getId();

    public Player getPlayer();

    public UserInterface getUserInterface();

    public Group getGroup();

    public boolean isInGroup(Group group);

    public void joinGroup(Group group);

    public void leaveGroup(Group group);

    public boolean isDebugging();

    public void setDebugging(boolean debug);

    public void debug(String message);

    public boolean isCombatLogging();

    public void setCombatLogging(boolean logging);

    public void combatLog(String message);

    public void reset();

    public int getMana();

    public void setMana(int mana);

    public boolean isManaRegenEnabled();

    public void setManaRegenEnabled(boolean enabled);

    public int getMaxMana();

    public int getStamina();

    public void setStamina(int stamina);

    public boolean isStaminaRegenEnabled();

    public void setStaminaRegenEnabled(boolean enabled);

    public int getMaxStamina();

    public Profession getSelectedProfession();

    public void setSelectedProfession(Profession profession);

    public Profession getPrimaryProfession();

    public Profession getSecundaryProfession();

    public Profession getVirtualProfession();

    public void changeProfession(Profession profession);

    public List<Skill> getSkills();

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
