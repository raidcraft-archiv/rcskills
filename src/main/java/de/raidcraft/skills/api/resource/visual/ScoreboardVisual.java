package de.raidcraft.skills.api.resource.visual;

import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.resource.VisualResource;
import de.raidcraft.skills.api.ui.UserInterface;

/**
 * @author Silthus
 */
public class ScoreboardVisual implements VisualResource {

    @Override
    public void update(Resource resource) {

        UserInterface userInterface = resource.getHero().getUserInterface();
        if (userInterface != null) {
            userInterface.refresh();
        }
    }
}
