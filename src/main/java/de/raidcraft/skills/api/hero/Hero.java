package de.raidcraft.skills.api.hero;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.InvalidChoiceException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.api.ui.UserInterface;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
public interface Hero extends Levelable<Hero>, CharacterTemplate {

    public int getId();

    public Player getPlayer();

    public UserInterface getUserInterface();

    public boolean isDebugging();

    public void setDebugging(boolean debug);

    public void debug(String message);

    public void reset();

    public int getMana();

    public void setMana(int mana);

    public int getMaxMana();

    public int getStamina();

    public void setStamina(int stamina);

    public int getMaxStamina();

    public Profession getSelectedProfession();

    public void setSelectedProfession(Profession profession);

    public Profession getPrimaryProfession();

    public Profession getSecundaryProfession();

    public Profession getVirtualProfession();

    public void changeProfession(Profession profession);

    public List<Skill> getSkills();

    public List<Profession> getProfessions();

    public boolean canChooseProfession(Profession profession) throws InvalidChoiceException;

    public void saveProfessions();

    public void saveSkills();

    public void save();

    public boolean hasSkill(Skill skill);

    public boolean hasSkill(String id);

    public boolean hasProfession(Profession profession);

    public boolean hasProfession(String id);

    public Profession getProfession(String id) throws UnknownSkillException, UnknownProfessionException;

    public void sendMessage(String... messages);

    public CharacterTemplate getTarget() throws InvalidTargetException;

    public Location getBlockTarget();

    public void addSkill(Skill skill);

    public void removeSkill(Skill skill);
}
