package de.raidcraft.skills.api.hero;

import de.raidcraft.api.InvalidTargetException;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.combat.Callback;
import de.raidcraft.skills.api.combat.RangedCallback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.Equipment;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public interface Hero extends Levelable<Hero>, RCPlayer {

    public int getId();

    public Player getBukkitPlayer();

    public int getDamage();

    public int getHealth();

    public void setHealth(int health);

    public int getMaxHealth();

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

    public List<Skill> getUnlockedSkills();

    public List<Profession> getProfessions();

    public Set<Equipment> getEquipment();

    public boolean canChoose(Profession profession);

    public void saveProfessions();

    public void saveSkills();

    public void save();

    public void kill(LivingEntity attacker);

    public void kill();

    public boolean hasSkill(Skill skill);

    public boolean hasSkill(String id);

    public Skill getSkill(String id) throws UnknownSkillException;

    public boolean hasProfession(Profession profession);

    public boolean hasProfession(String id);

    public boolean isInCombat();

    public void setInCombat(boolean inCombat);

    public Profession getProfession(String id) throws UnknownSkillException, UnknownProfessionException;

    public void damageEntity(LivingEntity target, int damage) throws CombatException;

    public void damageEntity(LivingEntity target, int damage, Callback callback) throws CombatException;

    public void castRangeAttack(RangedCallback callback) throws CombatException;

    public void runSkill(Skill skill) throws CombatException, InvalidTargetException;
}
