package de.raidcraft.skills.api.ability;

import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public interface Ability<T extends CharacterTemplate> {

    public T getHolder();

    public AbilityProperties getProperties();

    void load(ConfigurationSection data);

    String getName();

    String getFriendlyName();

    String getDescription();

    String[] getUsage();

    boolean canUseInCombat();

    boolean canUseOutOfCombat();

    Set<EffectType> getTypes();

    void addTypes(EffectType... effectTypes);

    boolean isOfType(EffectType type);

    Set<EffectElement> getElements();

    void addElements(EffectElement... effectElements);

    boolean isOfElement(EffectElement element);

    int getTotalDamage();

    double getTotalCastTime();

    int getTotalRange();

    double getTotalCooldown();

    void setCooldown(double cooldown);

    double getRemainingCooldown();

    boolean isOnCooldown();

    void setLastCast(long time);

    boolean matches(String name);

    public List<AmbientEffect> getAmbientEffects(AbilityEffectStage stage);

    /**
     * Applies the skill to the {@link de.raidcraft.skills.api.hero.Hero}. Is called when the skill is first added to the hero.
     */
    void apply();

    /**
     * Removes the skill from the {@link de.raidcraft.skills.api.hero.Hero}. Is called when the skill was removed from the hero.
     */
    void remove();
}
