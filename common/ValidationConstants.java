package org.example.securityplatform.common;

/**
 * 校验常量
 */
public class ValidationConstants {

    // 文件大小限制 (500MB)
    public static final long MAX_FILE_SIZE = 500 * 1024 * 1024L;

    // 允许的制品类型
    public static final String[] ALLOWED_ARTIFACT_TYPES = {"JAR", "WAR", "ZIP"};

    // 允许的文件扩展名
    public static final String[] ALLOWED_EXTENSIONS = {"jar", "war", "zip"};

    // 制品类型与扩展名映射
    public static final String[][] TYPE_EXTENSION_MAP = {
            {"JAR", "jar"},
            {"WAR", "war"},
            {"ZIP", "zip"}
    };
}