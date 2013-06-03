package de.raidcraft.skills.api.resource.visual;

import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.resource.VisualResource;

/**
 * @author Silthus
 */
public class ScoreboardVisual implements VisualResource {

    @Override
    public void update(Resource resource) {

        resource.getHero().getUserInterface().refresh();
    }
}
