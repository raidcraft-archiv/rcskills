package de.raidcraft.skills.trigger;

import com.sk89q.worldedit.BlockWorldVector;
import de.raidcraft.api.player.RCPlayer;
import de.raidcraft.skills.api.trigger.AbstractTrigger;
import org.bukkit.event.block.Action;

/**
 * @author Silthus
 */
public class InteractTrigger extends AbstractTrigger {

    private final BlockWorldVector block;
    private final int id;
    private final short data;
    private final Action action;

    public InteractTrigger(BlockWorldVector block, int id, short data, RCPlayer player, Action action) {

        super(player);
        this.block = block;
        this.id = id;
        this.data = data;
        this.action = action;
    }

    public BlockWorldVector getBlock() {

        return block;
    }

    public int getId() {

        return id;
    }

    public short getData() {

        return data;
    }

    public Action getAction() {

        return action;
    }
}
