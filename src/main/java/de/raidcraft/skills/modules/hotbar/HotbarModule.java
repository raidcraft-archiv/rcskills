package de.raidcraft.skills.modules.hotbar;

import de.raidcraft.api.config.Comment;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.skills.SkillsPlugin;
import de.raidcraft.skills.api.modules.SkillModule;
import de.raidcraft.skills.modules.hotbar.tables.THotbar;
import de.raidcraft.skills.modules.hotbar.tables.THotbarSlot;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class HotbarModule extends SkillModule {

    private final List<Listener> listeners = new ArrayList<>();
    @Getter
    private LocalConfiguration config;
    @Getter
    private HotbarManager hotbarManager;

    public HotbarModule(SkillsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        config = getPlugin().configure(new LocalConfiguration(getPlugin()));
        listeners.add(new HotbarListener(this));
    }

    @Override
    public void reload() {
        boolean previsousState = getConfig().enabled;

        getConfig().reload();

        if (getConfig().enabled != previsousState) {
            if (getConfig().enabled) {
                enable();
            } else {
                disable();
            }
        }
    }

    @Override
    public void enable() {
        if (!getConfig().enabled) return;
        hotbarManager = new HotbarManager(this);
        listeners.forEach(listener -> getPlugin().registerEvents(listener));
    }

    @Override
    public void disable() {
        listeners.forEach(listener -> getPlugin().unregisterEvents(listener));
        hotbarManager = null;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        ArrayList<Class<?>> classes = new ArrayList<>();
        classes.add(THotbar.class);
        classes.add(THotbarSlot.class);
        return classes;
    }

    public static class LocalConfiguration extends ConfigurationBase<SkillsPlugin> {

        @Setting("enabled")
        public boolean enabled = true;
        @Setting("menu.enabled")
        @Comment("Disables or enables the special hotbar menu.")
        public boolean enableMenuItem = true;
        @Setting("menu.item")
        @Comment("The name of the item that should be placed in the inventory of the player to open the hotbar menu.")
        public String menuItem = "minecraft:nether_star";
        @Setting("menu.items-slot")
        @Comment("The slot the menu item should be placed in. See https://minecraft.gamepedia.com/Inventory for the slot ids.")
        public int menuItemSlot = 8;

        public LocalConfiguration(SkillsPlugin plugin) {

            super(plugin, "hotbar.module.yml");
        }
    }
}
