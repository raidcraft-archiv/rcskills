package de.raidcraft.skills.api.resource;

import de.raidcraft.skills.api.resource.visual.ExperienceVisual;
import de.raidcraft.skills.api.resource.visual.ScoreboardVisual;
import de.raidcraft.skills.api.resource.visual.StaminaVisual;
import de.raidcraft.skills.api.resource.visual.TextVisual;
import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum VisualResourceType {

    STAMINA(new StaminaVisual()),
    TEXT(new TextVisual()),
    SCOREBOARD(new ScoreboardVisual()),
    EXPERIENCE(new ExperienceVisual());

    private final VisualResource visualResource;

    private VisualResourceType(VisualResource visualResource) {

        this.visualResource = visualResource;
    }

    public VisualResource getVisualResource() {

        return visualResource;
    }

    public void update(Resource resource) {

        if (resource.getHero().getPlayer() == null) {
            return;
        }
        visualResource.update(resource);
    }

    public static VisualResourceType fromString(String str) {

        return EnumUtils.getEnumFromString(VisualResourceType.class, str);
    }
}
