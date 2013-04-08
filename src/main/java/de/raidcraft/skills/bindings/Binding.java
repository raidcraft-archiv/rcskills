package de.raidcraft.skills.bindings;

import com.sk89q.minecraft.util.commands.CommandContext;
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
    private final CommandContext args;

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

        return args;
    }
}
