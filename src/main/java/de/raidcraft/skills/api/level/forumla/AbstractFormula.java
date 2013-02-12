package de.raidcraft.skills.api.level.forumla;

import de.raidcraft.RaidCraft;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;

/**
 * @author Silthus
 */
public abstract class AbstractFormula implements LevelFormula {

    private final ConfigurationSection config;

    public AbstractFormula(ConfigurationSection config) {

        this.config = config;
    }

    protected void loadParams() {

        if (config == null) {
            return;
        }
        for (Field field : getClass().getDeclaredFields()) {
            loadParam(field);
        }
    }

    private void loadParam(Field field) {

        if (field.isAnnotationPresent(Param.class)) {
            field.setAccessible(true);
            Param param = field.getAnnotation(Param.class);
            if (field.getType().isPrimitive()) {
                if (field.getType() == int.class) {
                    setIntParam(field, param);
                } else if (field.getType() == double.class) {
                    setDoubleParam(field, param);
                }
            }
        }
    }

    private void setIntParam(Field field, Param param) {

        try {
            field.setInt(this, getParamInt(param.value(), field.getInt(this)));
        } catch (IllegalAccessException e) {
            RaidCraft.LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    private void setDoubleParam(Field field, Param param) {

        try {
            field.setDouble(this, getParamDouble(param.value(), field.getDouble(this)));
        } catch (IllegalAccessException e) {
            RaidCraft.LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    private int getParamInt(String name, int def) {

        return config.getInt(name, def);
    }

    private double getParamDouble(String name, double def) {

        return config.getDouble(name, def);
    }
}
