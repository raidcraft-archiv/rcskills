package de.raidcraft.skills.api.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RE_PlayerStatusChangedEvent extends Event {

    @Getter
    @Setter
    private Player player;

    public RE_PlayerStatusChangedEvent(Player player) {
        this.player = player;
    }

    // Bukkit stuff
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

}
