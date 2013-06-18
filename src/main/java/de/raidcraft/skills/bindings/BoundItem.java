package de.raidcraft.skills.bindings;

import de.raidcraft.skills.api.combat.action.SkillAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Silthus
 */
public class BoundItem implements ListIterator<Binding>, Iterable<Binding> {

    private final Hero hero;
    private final Material item;
    private final List<Binding> bindings = new ArrayList<>();
    private int index = 0;

    public BoundItem(Hero hero, Material item) {

        this.hero = hero;
        this.item = item;
    }

    public boolean containsSkill(Skill skill) {

        for (Binding currentBinding : bindings) {
            if (skill == null && currentBinding.getSkill() == null) {
                return true;
            }
            if (skill != null && currentBinding.getSkill().getName().equalsIgnoreCase(skill.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Binding binding) {

        for (Binding currentBinding : bindings) {
            if (currentBinding.equals(binding)) {
                return true;
            }
        }
        return false;
    }

    public void add(Binding skill) {

        bindings.add(skill);
    }

    public List<Binding> getBindings() {

        return bindings;
    }

    public Hero getHero() {

        return hero;
    }

    public Material getItem() {

        return item;
    }

    public void use() {

        Binding binding = bindings.get(index);
        if (binding.getSkill() == null) {
            // its the playerholder
            // dont send message und quietly return
            return;
        }
        try {
            new SkillAction(binding.getSkill(), binding.getArgs()).run();
        } catch (CombatException e) {
            // dont spam the player with global cooldown
            if (e.getType() == CombatException.Type.ON_GLOBAL_COOLDOWN) {
                return;
            }
            getHero().sendMessage(ChatColor.RED + e.getMessage());
        }
    }

    public Binding getCurrent() {

        return bindings.get(index);
    }

    @Override
    public boolean hasNext() {

        return bindings.size() > 0;
    }

    @Override
    public Binding next() {

        if (bindings.size() < 1) {
            return null;
        }
        Binding skill = bindings.get(index);
        if (index < bindings.size() - 1) {
            index++;
        } else {
            index = 0;
        }
        return skill;
    }

    @Override
    public boolean hasPrevious() {

        return bindings.size() > 0;
    }

    @Override
    public Binding previous() {

        if (bindings.size() < 1) {
            return null;
        }
        Binding skill = bindings.get(index);
        if (index - 1 > -1) {
            index--;
        } else {
            index = bindings.size() - 1;
        }
        return skill;
    }

    @Override
    public int nextIndex() {

        return index < bindings.size() - 1 ? index + 1 : 0;
    }

    @Override
    public int previousIndex() {

        return index - 1 > -1 ? index - 1 : bindings.size() - 1;
    }

    @Override
    public void remove() {

        bindings.remove(index);
    }

    @Override
    public void set(Binding binding) {

        index = bindings.indexOf(binding);
    }

    @Override
    public Iterator<Binding> iterator() {

        return this;
    }
}
