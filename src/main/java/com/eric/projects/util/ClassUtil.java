package com.eric.projects.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

/**
 * This util class includes some common reflection operations for Class
 */
@Slf4j
public final class ClassUtil {

    /**
     * file url protocol
     */
    public static final String FILE_PROTOCOL = "file";

    /**
     * jar url protocol
     */
    public static final String JAR_PROTOCOL = "jar";

    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error", e);
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className) {
        try {
            Class<?> clazz = loadClass(className);
            return (T) clazz.newInstance();
        } catch (Exception e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    public static void setField(Field field, Object target, Object value) {
        setField(field, target, value, true);
    }

    /**
     * sets value to the target's fied
     * @param field
     * @param target
     * @param value
     * @param accessible
     */
    public static void setField(Field field, Object target, Object value, boolean accessible) {
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("setField error", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * extract all classes under the given basePackage
     * @param basePackage
     * @return
     */
    public static Set<Class<?>> getPackageClass(String basePackage) {
        URL resource = getClassLoader().getResource(basePackage.replace(".", "/"));
        if (resource == null) {
            throw new RuntimeException(basePackage + " does not exist");
        }
        try {
            if (resource.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
                File file = new File(resource.getFile());
                Path basePath = file.toPath();
                return Files.walk(basePath)
                        .filter(path -> path.toFile().getName().endsWith(".class"))
                        .map(path -> getClassByPath(path, basePath, basePackage))
                        .collect(Collectors.toSet());
            } else if (resource.getProtocol().equalsIgnoreCase(JAR_PROTOCOL)){
                // Parse each JarEntry if it's a JAR protocol
                JarURLConnection connection = (JarURLConnection) resource.openConnection();
                return connection.getJarFile().
                        stream().
                        filter(jarEntry -> jarEntry.getName().endsWith(".class")).
                        map(jarEntry -> getClassByJar(jarEntry))
                        .collect(Collectors.toSet());
            }
            return Collections.emptySet();
        } catch (IOException e) {
            log.error("load Package error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * extract Class from the given path
     * @param classPath
     * @param basePath
     * @param basePackage
     * @return
     */
    public static Class<?> getClassByPath(Path classPath, Path basePath, String basePackage) {
        String packageName = classPath.toString().replace(basePath.toString(), "");
        String className = (basePackage + packageName)
                .replace("/", ".")
                .replace("\\", ".")
                .replace(".class", "");
        return loadClass(className);
    }

    public static Class<?> getClassByJar(JarEntry jarEntry) {
        String jarEntryName = jarEntry.getName();
        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf('.')).replaceAll("/", ".");
        return loadClass(className);
    }
}
