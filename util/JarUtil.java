package org.example.securityplatform.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Jar包工具类
 */
public class JarUtil {

    /**
     * 获取Jar包中的所有条目
     *
     * @param jarPath Jar包路径
     * @return 条目列表
     * @throws IOException 读取异常
     */
    public static List<JarEntry> getJarEntries(String jarPath) throws IOException {
        List<JarEntry> entries = new ArrayList<>();
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                entries.add(enumeration.nextElement());
            }
        }
        return entries;
    }

    /**
     * 获取Jar包中的所有Class文件
     *
     * @param jarPath Jar包路径
     * @return Class文件条目列表
     * @throws IOException 读取异常
     */
    public static List<JarEntry> getClassEntries(String jarPath) throws IOException {
        List<JarEntry> classEntries = new ArrayList<>();
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                    classEntries.add(entry);
                }
            }
        }
        return classEntries;
    }

    /**
     * 获取Jar包中的所有配置文件
     *
     * @param jarPath Jar包路径
     * @return 配置文件条目列表
     * @throws IOException 读取异常
     */
    public static List<JarEntry> getConfigEntries(String jarPath) throws IOException {
        List<JarEntry> configEntries = new ArrayList<>();
        String[] extensions = {"yml", "yaml", "properties", "xml"};

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                if (!entry.isDirectory()) {
                    String name = entry.getName();
                    for (String ext : extensions) {
                        if (name.endsWith("." + ext)) {
                            configEntries.add(entry);
                            break;
                        }
                    }
                }
            }
        }
        return configEntries;
    }

    /**
     * 获取Jar包中的POM文件
     *
     * @param jarPath Jar包路径
     * @return POM文件条目列表
     * @throws IOException 读取异常
     */
    public static List<JarEntry> getPomEntries(String jarPath) throws IOException {
        List<JarEntry> pomEntries = new ArrayList<>();
        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                if (!entry.isDirectory() && entry.getName().contains("META-INF") && entry.getName().endsWith(".xml")) {
                    pomEntries.add(entry);
                }
            }
        }
        return pomEntries;
    }

    /**
     * 解压Jar包到指定目录
     *
     * @param jarPath   Jar包路径
     * @param targetDir 目标目录
     * @throws IOException 解压异常
     */
    public static void extractJar(String jarPath, String targetDir) throws IOException {
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> enumeration = jarFile.entries();
            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                File file = new File(targetDir + File.separator + entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    java.io.InputStream is = jarFile.getInputStream(entry);
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    is.close();
                }
            }
        }
    }
}