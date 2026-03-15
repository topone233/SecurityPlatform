package org.example.securityplatform.analyzer.api;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JAX-RS API提取器
 */
@Slf4j
public class JaxRsApiExtractor implements ApiExtractorStrategy {

    private static final String PATH = "Path";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String PATCH = "PATCH";

    @Override
    public boolean supports(String filePath) {
        return filePath != null && filePath.endsWith(".java");
    }

    @Override
    public List<ApiModel> extract(String filePath) {
        List<ApiModel> apis = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            JavaParser parser = new JavaParser();
            Optional<CompilationUnit> result = parser.parse(fis).getResult();

            if (result.isPresent()) {
                CompilationUnit cu = result.get();
                String className = cu.getPrimaryTypeName().orElse("Unknown");

                // 获取类级别的@Path
                String classLevelPath = "";
                Optional<ClassOrInterfaceDeclaration> classOpt = cu.getClassByName(className);
                if (classOpt.isPresent()) {
                    classLevelPath = extractAnnotationValue(classOpt.get().getAnnotations(), PATH);
                }

                // 遍历方法提取API
                if (classOpt.isPresent()) {
                    for (MethodDeclaration method : classOpt.get().getMethods()) {
                        List<ApiModel> methodApis = extractMethodApi(method, className, classLevelPath, filePath);
                        apis.addAll(methodApis);
                    }
                }
            }

        } catch (Exception e) {
            log.error("解析Java文件失败: {}", filePath, e);
        }

        return apis;
    }

    /**
     * 提取方法的API信息
     */
    private List<ApiModel> extractMethodApi(MethodDeclaration method, String className, String classLevelPath, String filePath) {
        List<ApiModel> apis = new ArrayList<>();
        List<AnnotationExpr> annotations = method.getAnnotations();

        String methodPath = extractAnnotationValue(annotations, PATH);

        // 检查HTTP方法注解
        String[] httpMethods = {GET, POST, PUT, DELETE, PATCH};
        for (String httpMethod : httpMethods) {
            if (hasAnnotation(annotations, httpMethod)) {
                String fullPath = classLevelPath + methodPath;
                fullPath = fullPath.replace("//", "/");

                ApiModel api = new ApiModel();
                api.setUrl(fullPath);
                api.setHttpMethod(httpMethod);
                api.setControllerClass(className);
                api.setMethodName(method.getNameAsString());
                api.setFramework("JAX_RS");

                // 提取参数信息
                String params = extractParameters(method);
                api.setParameters(params);

                apis.add(api);
            }
        }

        return apis;
    }

    /**
     * 检查是否有指定注解
     */
    private boolean hasAnnotation(List<AnnotationExpr> annotations, String annotationName) {
        return annotations.stream()
                .anyMatch(a -> a.getNameAsString().equals(annotationName));
    }

    /**
     * 提取注解值
     */
    private String extractAnnotationValue(List<AnnotationExpr> annotations, String annotationName) {
        for (AnnotationExpr annotation : annotations) {
            if (annotation.getNameAsString().equals(annotationName)) {
                if (annotation instanceof SingleMemberAnnotationExpr) {
                    SingleMemberAnnotationExpr single = (SingleMemberAnnotationExpr) annotation;
                    return single.getMemberValue().toString().replace("\"", "");
                } else if (annotation instanceof NormalAnnotationExpr) {
                    NormalAnnotationExpr normal = (NormalAnnotationExpr) annotation;
                    for (MemberValuePair pair : normal.getPairs()) {
                        if (pair.getNameAsString().equals("value")) {
                            return pair.getValue().toString().replace("\"", "");
                        }
                    }
                }
            }
        }
        return "";
    }

    /**
     * 提取方法参数
     */
    private String extractParameters(MethodDeclaration method) {
        StringBuilder params = new StringBuilder();
        method.getParameters().forEach(param -> {
            if (params.length() > 0) {
                params.append(", ");
            }
            params.append(param.getType().toString()).append(" ").append(param.getNameAsString());
        });
        return params.toString();
    }
}