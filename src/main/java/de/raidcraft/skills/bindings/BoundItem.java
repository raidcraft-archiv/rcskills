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

/**
 * @author Silthus
 */
public class BoundItem implements Iterator<Binding>, Iterable<Binding> {

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
            if (currentBinding.getSkill().getName().equalsIgnoreCase(skill.getName())) {
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
        try {
            new SkillAction(binding.getSkill(), binding.getArgs()).run();
            getHero().sendMessage(ChatColor.DARK_GRAY + "Skill ausgeführt: " + binding.getSkill().getFriendlyName());
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
    public void remove() {

        bindings.remove(index);
    }

    @Override
    public Iterator<Binding> iterator() {

        return this;
    }
}
