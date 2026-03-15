package org.example.securityplatform.exception;

import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }

    /**
     * 资源未找到异常
     */
    public static BusinessException notFound(String resourceType, Long id) {
        return new BusinessException(404, resourceType + "不存在，ID: " + id);
    }

    public static BusinessException notFound(String message) {
        return new BusinessException(404, message);
    }

    /**
     * 资源已存在异常
     */
    public static BusinessException alreadyExists(String resourceType, String identifier) {
        return new BusinessException(409, resourceType + "已存在：" + identifier);
    }

    /**
     * 参数校验失败异常
     */
    public static BusinessException invalidParameter(String paramName, String reason) {
        return new BusinessException(400, "参数 " + paramName + " 无效：" + reason);
    }

    /**
     * 操作不支持异常
     */
    public static BusinessException operationNotSupported(String operation) {
        return new BusinessException(400, "不支持的操作：" + operation);
    }

    /**
     * 文件相关异常
     */
    public static BusinessException fileTooLarge(long maxSize) {
        return new BusinessException(413, "文件大小超过限制：" + maxSize + " bytes");
    }

    public static BusinessException fileFormatNotSupported(String format) {
        return new BusinessException(400, "不支持的文件格式：" + format);
    }

    public static BusinessException fileNotFound(String filePath) {
        return new BusinessException(404, "文件不存在：" + filePath);
    }

    /**
     * 分析相关异常
     */
    public static BusinessException analysisFailed(String reason) {
        return new BusinessException(500, "分析失败：" + reason);
    }

    public static BusinessException analysisInProgress(Long artifactId) {
        return new BusinessException(409, "制品正在分析中，请稍后重试，artifactId: " + artifactId);
    }
}