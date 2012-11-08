package de.raidcraft.skills.skills.gathering;

import com.sk89q.worldedit.blocks.BlockID;
import de.raidcraft.rcrpg.api.player.RCPlayer;
import de.raidcraft.skills.api.AbstractLevelableSkill;
import de.raidcraft.skills.api.Passive;
import de.raidcraft.skills.trigger.BlockTrigger;

/**
 * @author Silthus
 */
public class Excavation extends AbstractLevelableSkill implements Passive<BlockTrigger> {

    public static final int[] EXCAVATION_IDS = {BlockID.SAND, BlockID.GRAVEL, BlockID.GRASS, BlockID.DIRT, BlockID.CLAY};

    public Excavation(int id, RCPlayer player) {

        super(id, player);
    }

    @Override
    public void increaseLevel() {
        //TODO: adjust loottables based on the level
    }

    @Override
    public void decreaseLevel() {
        //TODO: adjust loottables based on the level
    }

    @Override
    public void apply(BlockTrigger trigger) {

        for (int i = 0; i < EXCAVATION_IDS.length; i++) {
            if (EXCAVATION_IDS[i] == trigger.getId()) {
                addExp(1);
            }
        }
    }
}
