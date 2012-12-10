package de.raidcraft.skills;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;

import java.io.File;

/**
 * @author Silthus
 */
public abstract class DefaultConfiguration extends ConfigurationBase {

    public DefaultConfiguration(BasePlugin plugin, File file) {

        super(plugin, file);
    }

    @Override
    public int getInt(String path, int def) {

        if (!isSet(path)) {
            set(path, def);
            save();
        } else {
            return super.getInt(path, def);
        }
        return getInt(path);
    }

    @Override
    public String getString(String path, String def) {

        if (!isSet(path)) {
            set(path, def);
            save();
        } else {
            return super.getString(path, def);
        }
        return getString(path);
    }

    @Override
    public boolean getBoolean(String path, boolean def) {

        if (!isSet(path)) {
            set(path, def);
            save();
        } else {
            return super.getBoolean(path, def);
        }
        return getBoolean(path);
    }

    @Override
    public double getDouble(String path, double def) {

        if (!isSet(path)) {
            set(path, def);
            save();
        } else {
            return super.getDouble(path, def);
        }
        return getDouble(path);
    }

    @Override
    public long getLong(String path, long def) {

        if (!isSet(path)) {
            set(path, def);
            save();
        } else {
            return super.getLong(path, def);
        }
        return getLong(path);
    }
}
