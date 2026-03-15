package org.example.securityplatform.analyzer.danger;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 危险函数扫描器基类
 */
@Slf4j
public abstract class DangerFunctionScanner {

    protected final String jarPath;

    public DangerFunctionScanner(String jarPath) {
        this.jarPath = jarPath;
    }

    /**
     * 扫描危险函数
     *
     * @return 危险函数模型列表
     */
    public abstract List<DangerFunctionModel> scan() throws IOException;

    /**
     * 获取危险函数类型
     */
    public abstract String getDangerType();

    /**
     * 获取风险等级
     */
    public abstract String getRiskLevel();
}