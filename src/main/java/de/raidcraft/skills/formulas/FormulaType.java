package de.raidcraft.skills.formulas;

import de.raidcraft.RaidCraft;
import de.raidcraft.skills.api.level.forumla.LevelFormula;
import de.raidcraft.util.EnumUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Silthus
 */
public enum FormulaType {

    WOW("wow", WoWFormula.class),
    MCMMO("mcmmo", MCMMOFormula.class),
    LINEAR("linear", LinearFormula.class),
    STATIC("static", StaticFormula.class);

    private final String name;
    private final Class<? extends LevelFormula> fClass;
    private Constructor<? extends LevelFormula> constructor;

    private FormulaType(String name, Class<? extends LevelFormula> fClass) {

        this.name = name;
        this.fClass = fClass;
    }

    public static FormulaType fromName(String name) {

        FormulaType type = EnumUtils.getEnumFromString(FormulaType.class, name);
        if (type == null) {
            for (FormulaType formulaType : FormulaType.values()) {
                if (formulaType.name.equalsIgnoreCase(name)) {
                    return formulaType;
                }
            }
        }
        return type;
    }

    public LevelFormula create(ConfigurationSection config) {

        try {
            if (constructor == null) {
                this.constructor = fClass.getConstructor(ConfigurationSection.class);
                constructor.setAccessible(true);
            }
            return constructor.newInstance(config);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            RaidCraft.LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
