package org.example.securityplatform.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文件工具类
 */
public class FileUtil {

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名
     */
    public static String getExtension(String filename) {
        return FilenameUtils.getExtension(filename);
    }

    /**
     * 获取文件名（不含扩展名）
     *
     * @param filename 文件名
     * @return 文件名（不含扩展名）
     */
    public static String getBaseName(String filename) {
        return FilenameUtils.getBaseName(filename);
    }

    /**
     * 获取文件大小（字节）
     *
     * @param file 文件
     * @return 文件大小
     */
    public static long getFileSize(File file) {
        return file.length();
    }

    /**
     * 递归获取目录下所有文件
     *
     * @param directory 目录路径
     * @return 文件列表
     * @throws IOException 读取异常
     */
    public static List<Path> listFilesRecursively(String directory) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            return paths
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
    }

    /**
     * 按扩展名过滤文件
     *
     * @param directory 目录路径
     * @param extensions 扩展名数组（不含点）
     * @return 文件列表
     * @throws IOException 读取异常
     */
    public static List<Path> listFilesByExtension(String directory, String... extensions) throws IOException {
        List<Path> allFiles = listFilesRecursively(directory);
        return allFiles.stream()
                .filter(path -> {
                    String ext = getExtension(path.getFileName().toString());
                    for (String extension : extensions) {
                        if (extension.equalsIgnoreCase(ext)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    /**
     * 创建目录（如果不存在）
     *
     * @param directory 目录路径
     * @throws IOException 创建目录异常
     */
    public static void createDirectoryIfNotExists(String directory) throws IOException {
        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /**
     * 删除目录及其内容
     *
     * @param directory 目录路径
     * @throws IOException 删除异常
     */
    public static void deleteDirectory(String directory) throws IOException {
        File dir = new File(directory);
        if (dir.exists()) {
            FileUtils.deleteDirectory(dir);
        }
    }

    /**
     * 读取文件内容为字符串
     *
     * @param filePath 文件路径
     * @return 文件内容
     * @throws IOException 读取异常
     */
    public static String readFileAsString(String filePath) throws IOException {
        return FileUtils.readFileToString(new File(filePath), "UTF-8");
    }
}