package de.raidcraft.skills.api.resource.visual;

import de.raidcraft.skills.api.combat.action.MagicalAttack;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.resource.Resource;
import de.raidcraft.skills.api.resource.VisualResource;

/**
 * @author Silthus
 */
public class Health implements VisualResource {

    @Override
    public void update(Resource resource) {

        try {
            if (resource.getHero().getHealth() > resource.getCurrent()) {
                new MagicalAttack(resource.getHero(), resource.getHero(), resource.getCurrent()).run();
            } else {
                resource.getHero().heal(resource.getCurrent() - resource.getHero().getHealth());
            }
        } catch (CombatException ignored) {

        }
    }
}
