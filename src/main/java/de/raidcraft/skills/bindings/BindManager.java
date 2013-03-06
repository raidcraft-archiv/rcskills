package de.raidcraft.skills.bindings;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip
 */
public class BindManager {

    public static BindManager INST = null;

    private BasePlugin plugin;
    private Map<String, List<BoundItem>> boundItems = new HashMap<>();

    public BindManager(BasePlugin plugin) {

        INST = this;
        this.plugin = plugin;
        plugin.registerCommands(BindCommands.class);
        plugin.registerEvents(new BindListener());
    }

    public void loadBoundItems(String player) {
        //TODO implement
    }

    public void unloadBoundItems(String player) {
        boundItems.remove(player);
    }

    public List<BoundItem> getBoundItems(String player) {
        return boundItems.get(player);
    }

    public boolean addBoundItem(Hero hero, Material item, Skill skill) {

        if(!boundItems.containsKey(hero.getName())) {
            boundItems.put(hero.getName(), new ArrayList<BoundItem>());
        }

        for(BoundItem boundItem : boundItems.get(hero.getName())) {
            if(boundItem.getItem() == item) {
                if(boundItem.contains(skill)) {
                    return false;
                }
                boundItem.add(skill);
                return true;
            }
        }
        BoundItem boundItem = new BoundItem(hero, item);
        boundItem.add(skill);
        boundItems.get(hero.getName()).add(boundItem);
        return true;
    }

    public boolean removeBoundItem(Hero hero, Material item) {
        if(boundItems.containsKey(hero.getName())) {
            for(BoundItem boundItem : boundItems.get(hero.getName())) {
                if(boundItem.getItem() == item) {
                    boundItems.get(hero.getName()).remove(boundItem);
                    return true;
                }
            }
        }
        return false;
    }
}
