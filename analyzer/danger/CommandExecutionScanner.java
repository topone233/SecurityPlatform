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
 * 命令执行扫描器 - 检测Runtime.exec和ProcessBuilder
 */
@Slf4j
public class CommandExecutionScanner extends DangerFunctionScanner {

    private static final String RUNTIME_EXEC = "java/lang/Runtime.exec";
    private static final String PROCESSBUILDER_START = "java/lang/ProcessBuilder.start";

    public CommandExecutionScanner(String jarPath) {
        super(jarPath);
    }

    @Override
    public List<DangerFunctionModel> scan() throws IOException {
        List<DangerFunctionModel> results = new ArrayList<>();

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                    try (InputStream is = jarFile.getInputStream(entry)) {
                        ClassReader classReader = new ClassReader(is);
                        CommandExecutionVisitor visitor = new CommandExecutionVisitor(entry.getName());
                        classReader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                        results.addAll(visitor.getDangers());
                    } catch (IOException e) {
                        log.warn("解析class文件失败: {}", entry.getName(), e);
                    }
                }
            }
        }

        return results;
    }

    @Override
    public String getDangerType() {
        return "COMMAND_EXECUTION";
    }

    @Override
    public String getRiskLevel() {
        return "HIGH";
    }

    /**
     * 命令执行访问器
     */
    private static class CommandExecutionVisitor extends ClassVisitor {
        private final String filePath;
        private String className;
        private String currentMethod;
        private int lineNumber;
        private final List<DangerFunctionModel> dangers = new ArrayList<>();

        public CommandExecutionVisitor(String filePath) {
            super(Opcodes.ASM9);
            this.filePath = filePath;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className = name.replace('/', '.');
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            this.currentMethod = name;
            return new MethodVisitor(Opcodes.ASM9) {
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                    String methodKey = owner + "." + name;

                    if (methodKey.contains("Runtime") && name.equals("exec")) {
                        addDanger("Runtime.exec", "HIGH");
                    } else if (methodKey.contains("ProcessBuilder") && name.equals("start")) {
                        addDanger("ProcessBuilder.start", "HIGH");
                    }
                }

                @Override
                public void visitLineNumber(int line, org.objectweb.asm.Label start) {
                    lineNumber = line;
                }
            };
        }

        private void addDanger(String functionName, String riskLevel) {
            DangerFunctionModel danger = new DangerFunctionModel();
            danger.setFunctionName(functionName);
            danger.setFunctionType("COMMAND_EXECUTION");
            danger.setClassName(className);
            danger.setMethodName(currentMethod);
            danger.setLineNumber(lineNumber);
            danger.setFilePath(filePath);
            danger.setRiskLevel(riskLevel);
            dangers.add(danger);
        }

        public List<DangerFunctionModel> getDangers() {
            return dangers;
        }
    }
}