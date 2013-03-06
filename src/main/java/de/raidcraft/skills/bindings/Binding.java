package de.raidcraft.skills.bindings;

import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.Material;

/**
 * @author Philip
 */
public class Binding {

    private Hero hero;
    private Material material;
    private Skill skill;

    public Binding(Hero hero, Material material, Skill skill) {

        this.hero = hero;
        this.material = material;
        this.skill = skill;
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
}
