package org.example.securityplatform.analyzer.component;

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
 * 依赖扫描器 - 使用ASM扫描Class文件中的依赖
 */
@Slf4j
public class DependencyScanner {

    /**
     * 从Jar包中扫描依赖
     *
     * @param jarPath Jar包路径
     * @return 依赖类名列表
     */
    public List<String> scanDependencies(String jarPath) throws IOException {
        List<String> dependencies = new ArrayList<>();

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class") && !entry.isDirectory()) {
                    try (InputStream is = jarFile.getInputStream(entry)) {
                        ClassReader classReader = new ClassReader(is);
                        DependencyVisitor visitor = new DependencyVisitor();
                        classReader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                        dependencies.addAll(visitor.getDependencies());
                    } catch (IOException e) {
                        log.warn("解析class文件失败: {}", entry.getName(), e);
                    }
                }
            }
        }

        return dependencies;
    }

    /**
     * 依赖访问器 - 使用ASM访问类依赖
     */
    private static class DependencyVisitor extends ClassVisitor {
        private final List<String> dependencies = new ArrayList<>();
        private String className;

        public DependencyVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            this.className = name;
            addDependency(superName);
            for (String iface : interfaces) {
                addDependency(iface);
            }
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            return new MethodDependencyVisitor(dependencies);
        }

        @Override
        public void visitEnd() {
            // 清理类名中的路径分隔符
            if (className != null) {
                dependencies.add(className.replace('/', '.'));
            }
        }

        private void addDependency(String className) {
            if (className != null && !className.startsWith("java/") && !className.startsWith("javax/")) {
                dependencies.add(className.replace('/', '.'));
            }
        }

        public List<String> getDependencies() {
            return dependencies;
        }
    }

    /**
     * 方法依赖访问器
     */
    private static class MethodDependencyVisitor extends MethodVisitor {
        private final List<String> dependencies;

        public MethodDependencyVisitor(List<String> dependencies) {
            super(Opcodes.ASM9);
            this.dependencies = dependencies;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            if (owner != null && !owner.startsWith("java/") && !owner.startsWith("javax/")) {
                dependencies.add(owner.replace('/', '.'));
            }
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            if (owner != null && !owner.startsWith("java/") && !owner.startsWith("javax/")) {
                dependencies.add(owner.replace('/', '.'));
            }
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            if (type != null && !type.startsWith("java/") && !type.startsWith("javax/")) {
                dependencies.add(type.replace('/', '.'));
            }
        }
    }
}