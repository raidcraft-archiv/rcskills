package de.raidcraft.skills.api.loader;

import de.raidcraft.RaidCraft;

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

/**
 * A skill loader that loads components from all the jar files in a given folder
 */
public abstract class JarFileLoader<T> extends FileLoader<T> {

    private final File jarDir;

    public JarFileLoader(Class<T> tClass, File jarDir) {

        super(tClass);
        this.jarDir = jarDir;
        if (!jarDir.exists()) {
            jarDir.mkdirs();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Class<? extends T>> loadClasses() {

        final List<Class<? extends T>> skills = new ArrayList<>();

        // Iterate through the files in the jar dirs
        for (final File file : jarDir.listFiles()) {
            if (!file.getName().endsWith(".jar")) continue;
            JarFile jarFile;
            URLClassLoader loader;
            try {
                jarFile = new JarFile(file);
                loader = AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>() {
                    public URLClassLoader run() {

                        try {
                            return new URLClassLoader(new URL[]{file.toURI().toURL()}, getClass().getClassLoader());
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
                String className = formatPath(next.getName());
                try {
                    clazz = Class.forName(className, true, loader);
                } catch (Throwable e) {
                    RaidCraft.LOGGER.severe("ERROR when loading class: " + className);
                    e.printStackTrace();
                }

                if (!isClass(clazz)) continue;

                skills.add((Class<? extends T>) clazz);
            }
        }
        return skills;
    }
}
