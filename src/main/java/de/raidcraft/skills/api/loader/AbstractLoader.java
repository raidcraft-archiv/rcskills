/*
 * CommandBook
 * Copyright (C) 2012 sk89q <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.raidcraft.skills.api.loader;

import java.util.logging.Logger;

/**
 * A parent class that contains several useful component loader helper methods
 */
public abstract class AbstractLoader<T> implements Loader<T> {

    private final Logger logger;
    private final Class<T> tClass;
    
    protected AbstractLoader(Class<T> tClass, Logger logger) {

        this.logger = logger;
        this.tClass = tClass;
    }

    @Override
    public Class<T> getRequiredClass() {

        return tClass;
    }

    @Override
    public boolean isClass(Class<?> clazz) {

        return clazz != null && getClass().isAssignableFrom(clazz);
    }

    protected Logger getLogger() {
        return logger;
    }
}