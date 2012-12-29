package de.raidcraft.skills.api.combat;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.Effect;
import de.raidcraft.skills.api.hero.Hero;
import de.raidcraft.skills.api.skill.Skill;

/**
 * @author Silthus
 */
public enum AttackSource {

    HERO,
    CREATURE,
    ENVIRONMENT,
    EFFECT,
    SKILL;

    public static <S> AttackSource fromObject(S source) {

        if (source instanceof Hero) {
            return HERO;
        }
        if (source instanceof CharacterTemplate) {
            return CREATURE;
        }
        if (source instanceof Skill) {
            return SKILL;
        }
        if (source instanceof Effect) {
            return EFFECT;
        }
        // default return environment
        return ENVIRONMENT;
    }
}
