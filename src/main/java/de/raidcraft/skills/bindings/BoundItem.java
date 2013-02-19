package de.raidcraft.skills.bindings;

import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Silthus
 */
public class BoundItem implements Iterator<Skill>, Iterable<Skill> {

    private final Hero hero;
    private final Material item;
    private final List<Skill> skills = new ArrayList<>();
    private int index = 0;

    public BoundItem(Hero hero, Material item) {

        this.hero = hero;
        this.item = item;
    }

    public Hero getHero() {

        return hero;
    }

    public Material getItem() {

        return item;
    }

    public void use() {

        Skill skill = skills.get(index);
        try {
            new SkillAction(skill).run();
        } catch (CombatException | CommandException e) {
            getHero().sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    @Override
    public boolean hasNext() {

        return skills.size() > 0;
    }

    @Override
    public Skill next() {

        if (skills.size() < 1) {
            return null;
        }
        Skill skill = skills.get(index);
        if (index < skills.size() - 1) {
            index++;
        } else {
            index = 0;
        }
        return skill;
    }

    @Override
    public void remove() {

        skills.remove(index);
    }

    @Override
    public Iterator<Skill> iterator() {

        return this;
    }
}
