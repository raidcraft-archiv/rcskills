package de.raidcraft.skills.loader;

import de.raidcraft.skills.api.skill.Skill;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * A skill loader that loads components from all the jar files in a given folder
 */
public abstract class JarFilesSkillLoader extends FileSkillLoader {

    private final File jarDir;

    public JarFilesSkillLoader(Logger logger, File jarDir) {

        super(logger);
        this.jarDir = jarDir;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Class<? extends Skill>> loadSkillClasses() {

        final List<Class<? extends Skill>> skills = new ArrayList<>();

        // Iterate through the files in the jar dirs
        for (final File file : jarDir.listFiles()) {
            if (!file.getName().endsWith(".jar")) continue;
            JarFile jarFile;
            ClassLoader loader;
            try {
                jarFile = new JarFile(file);
                loader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                    public ClassLoader run() {
                        try {
                            return new URLClassLoader(new URL[] {file.toURI().toURL()}, getClass().getClassLoader());
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (IOException e) {
                continue;
            }

            // And then the files in the jar
            for (Enumeration<JarEntry> en = jarFile.entries(); en.hasMoreElements(); ) {
                JarEntry next = en.nextElement();
                // Make sure it's a class
                if (!next.getName().endsWith(".class")) continue;

                Class<?> clazz = null;
                try {
                    clazz = Class.forName(formatPath(next.getName()), true, loader);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (!isSkillClass(clazz)) continue;

                skills.add((Class<? extends Skill>) clazz);
            }
        }
        return skills;
    }
}
