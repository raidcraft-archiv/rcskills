package de.raidcraft.skills.api.ability;

import de.raidcraft.api.ambient.AmbientEffect;
import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.combat.EffectElement;
import de.raidcraft.skills.api.combat.EffectType;
import de.raidcraft.skills.api.combat.action.AbilityAction;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.AbilityProperties;
import de.raidcraft.skills.api.skill.AbilityEffectStage;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Instant;
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

    void checkUsage(AbilityAction<T> action) throws CombatException;

    boolean canUseAbility();

    void substractUsageCost(AbilityAction<T> action);

    Set<EffectType> getTypes();

    void addTypes(EffectType... effectTypes);

    boolean isOfType(EffectType type);

    Set<EffectElement> getElements();

    void addElements(EffectElement... effectElements);

    boolean isOfElement(EffectElement element);

    double getTotalDamage();

    double getTotalCastTime();

    int getTotalRange();

    long getConfiguredCooldown();

    double getCooldown();

    void setCooldown(double cooldown);

    double getRemainingCooldown();

    boolean isOnCooldown();

    boolean isLevelable();

    Instant getLastCast();

    void setLastCast(Instant time);

    boolean matches(String name);

    List<AmbientEffect> getAmbientEffects(AbilityEffectStage stage);

    List<AmbientEffect> getAmbientEffects(AbilityEffectStage stage, CharacterTemplate target);

    void executeAmbientEffects(AbilityEffectStage stage, Location location);

    /**
     * Applies the skill to the {@link de.raidcraft.skills.api.hero.Hero}. Is called when the skill is first added to the hero.
     */
    void apply();

    /**
     * Removes the skill from the {@link de.raidcraft.skills.api.hero.Hero}. Is called when the skill was removed from the hero.
     */
    void remove();
}
