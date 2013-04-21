package de.raidcraft.skills.api.effect.common;

import de.raidcraft.skills.api.character.CharacterTemplate;
import de.raidcraft.skills.api.effect.DiminishingReturnType;
import de.raidcraft.skills.api.effect.EffectInformation;
import de.raidcraft.skills.api.effect.types.ExpirableEffect;
import de.raidcraft.skills.api.exceptions.CombatException;
import de.raidcraft.skills.api.persistance.EffectData;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Silthus
 */
@EffectInformation(
        name = "Diminishing Returns",
        description = "Provides protection for stun locks and more.",
        priority = 1.0
)
public class DiminishingReturns<S> extends ExpirableEffect<S> {

    private static final Map<DiminishingReturnType, Map<Integer, Double>> reduction = new HashMap<>();
    private final Map<DiminishingReturnType, Integer> stacks = new HashMap<>();

    public DiminishingReturns(S source, CharacterTemplate target, EffectData data) {

        super(source, target, data);
    }

    @Override
    public void load(ConfigurationSection data) {

        if (!reduction.isEmpty()) {
            return;
        }
        for (String key : data.getKeys(false)) {
            DiminishingReturnType type = DiminishingReturnType.fromString(key);
            if (type != null) {
                ConfigurationSection section = data.getConfigurationSection(key);
                for (String subKey : section.getKeys(false)) {
                    try {
                        int stack = Integer.parseInt(subKey);
                        if (!reduction.containsKey(type)) {
                            reduction.put(type, new HashMap<Integer, Double>());
                        }
                        reduction.get(type).put(stack, section.getDouble(subKey));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
    }

    public int getStacks(DiminishingReturnType type) {

        return stacks.get(type);
    }

    public void increase(DiminishingReturnType type) {

        stacks.put(type, stacks.get(type) + 1);
    }

    public int remove(DiminishingReturnType type) {

        return stacks.remove(type);
    }

    public double getReduction(DiminishingReturnType type) {

        if (!reduction.containsKey(type)) {
            return 0;
        }
        return reduction.get(type).get(getStacks(type));
    }

    @Override
    protected void apply(CharacterTemplate target) throws CombatException {

    }

    @Override
    protected void remove(CharacterTemplate target) throws CombatException {

    }

    @Override
    protected void renew(CharacterTemplate target) throws CombatException {

    }
}
