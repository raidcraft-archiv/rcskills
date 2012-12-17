package de.raidcraft.skills.api.hero;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.exceptions.InvalidChoiceException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.Equipment;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public interface Hero extends Levelable<Hero>, CharacterTemplate {

    public int getId();

    public Player getPlayer();

    public boolean isDebugging();

    public void setDebugging(boolean debug);

    public void debug(String message);

    public int getDamage();

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

    public void changeProfession(Profession profession);

    public List<Skill> getSkills();

    public List<Profession> getProfessions();

    public Set<Equipment> getEquipment();

    public boolean canChooseProfession(Profession profession) throws InvalidChoiceException;

    public void saveProfessions();

    public void saveSkills();

    public void save();

    public boolean hasSkill(Skill skill);

    public boolean hasSkill(String id);

    public Skill getSkill(String id) throws UnknownSkillException;

    public boolean hasProfession(Profession profession);

    public boolean hasProfession(String id);

    public Profession getProfession(String id) throws UnknownSkillException, UnknownProfessionException;

    public void sendMessage(String... messages);

    public CharacterTemplate getTarget();

    public Location getBlockTarget();
}
