package de.raidcraft.skills.bindings;

import com.sk89q.minecraft.util.commands.CommandContext;
import de.raidcraft.api.database.Database;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip
 */
public class BindManager {

    public static BindManager INST = null;

    private SkillsPlugin plugin;
    private Map<String, List<BoundItem>> boundItems = new HashMap<>();

    public BindManager(SkillsPlugin plugin) {

        INST = this;
        this.plugin = plugin;
        plugin.registerCommands(BindCommands.class);
        plugin.registerEvents(new BindListener());
        plugin.registerTable(BindingsTable.class, new BindingsTable());
    }

    public void loadBoundItems(Player player) {

        Hero hero = plugin.getCharacterManager().getHero(player);
        List<Binding> bindings = Database.getTable(BindingsTable.class).getBindings(hero);
        for (Binding binding : bindings) {
            addBinding(binding.getHero(), binding.getMaterial(), binding.getSkill(), binding.getArgs(), false);
        }
    }

    public void unloadBoundItems(String player) {

        boundItems.remove(player);
    }

    public List<BoundItem> getBoundItems(String player) {

        return boundItems.get(player);
    }

    public boolean addBinding(Hero hero, Material item, Skill skill) {

        return addBinding(hero, item, skill, null, true);
    }

    public boolean addBinding(Hero hero, Material item, Skill skill, CommandContext args) {

        return addBinding(hero, item, skill, args);
    }

    public boolean addBinding(Hero hero, Material item, Skill skill, CommandContext args, boolean save) {

        if (!boundItems.containsKey(hero.getName())) {
            boundItems.put(hero.getName(), new ArrayList<BoundItem>());
        }

        boolean found = false;
        for (BoundItem boundItem : boundItems.get(hero.getName())) {
            if (boundItem.getItem() == item) {
                if (boundItem.contains(skill)) {
                    return false;
                }
                boundItem.add(skill);
                found = true;
            }
        }
        if (!found) {
            BoundItem boundItem = new BoundItem(hero, item);
            boundItem.add(skill);
            boundItems.get(hero.getName()).add(boundItem);
        }
        if (save) {
            Database.getTable(BindingsTable.class).saveBinding(new Binding(hero, item, skill, args));
        }
        return true;
    }

    public boolean removeBindings(Hero hero, Material item) {

        if (boundItems.containsKey(hero.getName())) {
            for (BoundItem boundItem : boundItems.get(hero.getName())) {
                if (boundItem.getItem() == item) {
                    for (Skill skill : boundItem.getBindings()) {
                        Database.getTable(BindingsTable.class).deleteBinding(hero, item, skill);
                    }
                    boundItems.get(hero.getName()).remove(boundItem);
                    return true;
                }
            }
        }
        return false;
    }
}
