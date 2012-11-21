package de.raidcraft.skills.api.hero;

import com.avaje.ebean.Ebean;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.bukkit.BukkitPlayer;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.level.Level;
import de.raidcraft.skills.api.level.Levelable;
import de.raidcraft.skills.api.persistance.HeroData;
import de.raidcraft.skills.api.profession.Profession;
import de.raidcraft.skills.api.skill.Skill;
import de.raidcraft.skills.tables.THero;
import de.raidcraft.skills.tables.THeroSkill;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractHero extends BukkitPlayer implements Hero {

    private final int id;
    private Level<Hero> level;
    private final Map<String, Skill> skills = new HashMap<>();
    private final Map<String, Profession> professions = new HashMap<>();
    // this just tells the client what to display in the experience bar and so on
    private Profession selectedProfession;

    protected AbstractHero(HeroData data) {

        super(data.getName());
        this.id = data.getId();
        this.selectedProfession = data.getSelectedProfession();
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public void attachLevel(Level<Hero> level) {

        this.level = level;
    }

    @Override
    public final void save() {

        saveLevelProgress(level);
        saveSkills();
    }

    @Override
    public final void saveSkills() {

        for (Skill skill : getSkills()) {
            if (skill instanceof Levelable) {
                ((Levelable) skill).getLevel().saveLevelProgress();
            }
        }
    }

    @Override
    public boolean hasSkill(String id) {

        if (isOnline()) {
            return skills.containsKey(id);
        } else {
            List<THeroSkill> skills = Ebean.find(THero.class, getId()).getSkills();
            for (THeroSkill skill : skills) {
                if (skill.getName().equalsIgnoreCase(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasSkill(Skill skill) {

        return hasSkill(skill.getName());
    }

    public Skill getSkill(String id) {

        Skill skill;
        if (skills.containsKey(id)) {
            skill = skills.get(id);
        } else {
            skill = RaidCraft.getComponent(SkillsPlugin.class).getSkillManager().getSkill(this, id);
            skills.put(skill.getName(), skill);
        }
        return skill;
    }

    @Override
    public Collection<Skill> getSkills() {

        return skills.values();
    }

    @Override
    public Collection<Profession> getProfessions() {

        return professions.values();
    }

    @Override
    public Profession getSelectedProfession() {

        return selectedProfession;
    }

    @Override
    public Level<Hero> getLevel() {

        return level;
    }

    @Override
    public Profession getProfession(String id) {

        Profession profession;
        if (professions.containsKey(id)) {
            profession = professions.get(id);
        } else {
            profession = RaidCraft.getComponent(SkillsPlugin.class).getProfessionManager().getProfession(this, id);
            professions.put(id, profession);
        }
        return profession;
    }

    @Override
    public boolean hasProfession(String id) {

        return professions.containsKey(id);
    }

    @Override
    public boolean hasProfession(Profession profession) {

        return hasProfession(profession.getName());
    }
}
