package de.raidcraft.skills.api.resource;

import de.raidcraft.skills.api.resource.visual.Health;
import de.raidcraft.skills.api.resource.visual.Stamina;
import de.raidcraft.skills.api.resource.visual.Text;
import de.raidcraft.util.EnumUtils;

/**
 * @author Silthus
 */
public enum VisualResourceType {

    STAMINA(new Stamina()),
    TEXT(new Text()),
    HEALTH(new Health());

    private final VisualResource visualResource;

    private VisualResourceType(VisualResource visualResource) {

        this.visualResource = visualResource;
    }

    public void update(Resource resource) {

        visualResource.update(resource);
    }

    public static VisualResourceType fromString(String str) {

        return EnumUtils.getEnumFromString(VisualResourceType.class, str);
    }
}
