package de.raidcraft.skills.bindings;

import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.Material;

/**
 * @author Philip
 */
public class Binding {

    private final Hero hero;
    private final Material material;
    private final Skill skill;
    private CommandContext args;

    public Binding(Hero hero, Material material, Skill skill, CommandContext args) {

        this.hero = hero;
        this.material = material;
        this.skill = skill;
        this.args = args;
    }

    public Hero getHero() {

        return hero;
    }

    public Material getMaterial() {

        return material;
    }

    public Skill getSkill() {

        return skill;
    }

    public CommandContext getArgs() {

        if (args == null) {
            try {
                args = new CommandContext("");
            } catch (CommandException ignored) {
            }
        }
        return args;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Binding binding = (Binding) o;

        return hero.equals(binding.hero) && material == binding.material && skill.equals(binding.skill);

    }

    @Override
    public int hashCode() {

        int result = hero.hashCode();
        result = 31 * result + material.hashCode();
        result = 31 * result + skill.hashCode();
        return result;
    }
}
