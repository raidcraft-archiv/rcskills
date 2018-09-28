package de.raidcraft.skills.api.hero;

import de.raidcraft.api.items.ArmorType;
import de.raidcraft.api.items.ItemAttribute;
import de.raidcraft.api.items.WeaponType;
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
import de.raidcraft.skills.binds.BindManager;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public interface Hero extends SkilledCharacter<Hero> {

    int getId();

    Player getPlayer();

    boolean isOnline();

    AttachedLevel<Hero> getExpPool();

    UserInterface getUserInterface();

    Collection<Attribute> getAttributes();

    Attribute getAttribute(String attribute);

    Attribute getAttribute(ItemAttribute attribute);

    boolean isAllowedWeapon(WeaponType type);

    boolean isAllowedArmor(ArmorType type);

    void checkWeapons();

    void checkArmor();

    Party getPendingPartyInvite();

    void setPendingPartyInvite(Party partyInvite);

    int getAttributeValue(String attribute);

    void setAttributeValue(String attribute, int value);

    HeroOptions getOptions();

    void debug(String message);

    void combatLog(String message);

    void combatLog(Object o, String message);

    boolean isPvPEnabled();

    void setPvPEnabled(boolean enablePvP);

    long getLastCombatAction();

    Resource getResource(String name);

    boolean hasResource(String name);

    void attachResource(Resource resource);

    Resource detachResource(String name);

    Set<Resource> getResources();

    Profession getHighestRankedProfession();

    Profession getSelectedProfession();

    Profession getVirtualProfession();

    int getPlayerLevel();

    boolean hasPath(Path path);

    boolean hasPath(String path);

    Set<Path<Profession>> getPaths();

    Path<Profession> getPath(String name);

    void changeProfession(Profession profession);

    List<Skill> getSkills();

    Skill getSkill(String name) throws UnknownSkillException;

    List<Profession> getProfessions();

    void saveProfessions();

    void saveSkills();

    void save();

    boolean hasSkill(Skill skill);

    boolean hasSkill(String id);

    boolean hasProfession(Profession profession);

    boolean hasProfession(String id);

    Profession getProfession(String id) throws UnknownSkillException, UnknownProfessionException;

    void sendMessage(String... messages);

    void addSkill(Skill skill);

    void removeSkill(Skill skill);

    void updatePermissions();

    BindManager getBindings();
}
