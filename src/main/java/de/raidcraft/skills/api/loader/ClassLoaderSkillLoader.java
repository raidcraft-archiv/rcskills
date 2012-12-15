/*
 * CommandBook
 * Copyright (C) 2011 sk89q <http://www.sk89q.com>
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

import de.raidcraft.skills.api.skill.Skill;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.logging.Logger;

/**
 * A component loader that loads components from a directory of classes.
 */
public abstract class ClassLoaderSkillLoader extends FileSkillLoader {
    private final URLClassLoader loader;
    private final File classDir;

    public ClassLoaderSkillLoader(final Logger logger, final File classDir) {

        super(logger);
        this.classDir = classDir;
        this.loader = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
            public URLClassLoader run() {
                try {
                    return new URLClassLoader(new URL[]{classDir.toURI().toURL()}, getClass().getClassLoader());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Class<? extends Skill>> loadSkillClasses() {

        final List<Class<? extends Skill>> skills = new ArrayList<>();

        for (String string : getClassNames()) {
            Class<?> clazz = null;
            try {
                clazz = loader.loadClass(string);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (!isSkillClass(clazz)) continue;

            skills.add((Class<? extends Skill>) clazz);
        }
        return skills;
    }
    
    public Set<String> getClassNames() {
        return recursiveGetClasses(classDir, "");
    }
    
    public Set<String> recursiveGetClasses(File dir, String parentName) {

        Set<String> classNames = new HashSet<>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                classNames.addAll(recursiveGetClasses(file, parentName + file.getName() + "."));
            } else if (file.getName().endsWith(".class")) {
                classNames.add(parentName + formatPath(file.getName()));
            }
        }
        return classNames;
    }
}
