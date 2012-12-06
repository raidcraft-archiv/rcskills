package de.raidcraft.skills.api.hero;

import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.combat.Callback;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.exceptions.UnknownProfessionException;
import de.raidcraft.skills.api.exceptions.UnknownSkillException;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
public interface Hero extends Levelable<Hero>, RCPlayer {

    public int getId();

    public Player getBukkitPlayer();

    public int getHealth();

    public void setHealth(int health);

    public int getMaxHealth();

    public Profession getSelectedProfession();

    public Profession getPrimaryProfession();

    public Profession getSecundaryProfession();

    public List<Skill> getSkills();

    public List<Skill> getUnlockedSkills();

    public List<Profession> getProfessions();

    public boolean canChoose(Profession profession);

    public void saveSkills();

    public void save();

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

    public Skill getSkillFromArg(String input) throws CommandException;
}
