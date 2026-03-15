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
 * Spring MVC API提取器
 */
@Slf4j
public class SpringMvcApiExtractor implements ApiExtractorStrategy {

    private static final String REQUEST_MAPPING = "RequestMapping";
    private static final String GET_MAPPING = "GetMapping";
    private static final String POST_MAPPING = "PostMapping";
    private static final String PUT_MAPPING = "PutMapping";
    private static final String DELETE_MAPPING = "DeleteMapping";
    private static final String PATCH_MAPPING = "PatchMapping";

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

                // 获取类级别的RequestMapping
                String classLevelPath = "";
                Optional<ClassOrInterfaceDeclaration> classOpt = cu.getClassByName(className);
                if (classOpt.isPresent()) {
                    classLevelPath = extractAnnotationValue(classOpt.get().getAnnotations(), REQUEST_MAPPING);
                }

                // 遍历方法提取API
                if (classOpt.isPresent()) {
                    for (MethodDeclaration method : classOpt.get().getMethods()) {
                        ApiModel api = extractMethodApi(method, className, classLevelPath, filePath);
                        if (api != null) {
                            apis.add(api);
                        }
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
    private ApiModel extractMethodApi(MethodDeclaration method, String className, String classLevelPath, String filePath) {
        List<AnnotationExpr> annotations = method.getAnnotations();

        String httpMethod = null;
        String path = null;

        for (AnnotationExpr annotation : annotations) {
            String annotationName = annotation.getNameAsString();

            if (GET_MAPPING.equals(annotationName)) {
                httpMethod = "GET";
                path = extractAnnotationValue(annotations, GET_MAPPING);
            } else if (POST_MAPPING.equals(annotationName)) {
                httpMethod = "POST";
                path = extractAnnotationValue(annotations, POST_MAPPING);
            } else if (PUT_MAPPING.equals(annotationName)) {
                httpMethod = "PUT";
                path = extractAnnotationValue(annotations, PUT_MAPPING);
            } else if (DELETE_MAPPING.equals(annotationName)) {
                httpMethod = "DELETE";
                path = extractAnnotationValue(annotations, DELETE_MAPPING);
            } else if (PATCH_MAPPING.equals(annotationName)) {
                httpMethod = "PATCH";
                path = extractAnnotationValue(annotations, PATCH_MAPPING);
            } else if (REQUEST_MAPPING.equals(annotationName)) {
                httpMethod = extractRequestMappingMethod(annotation);
                path = extractAnnotationValue(annotations, REQUEST_MAPPING);
            }

            if (httpMethod != null) {
                break;
            }
        }

        if (httpMethod != null && path != null) {
            String fullPath = classLevelPath + path;
            fullPath = fullPath.replace("//", "/");

            ApiModel api = new ApiModel();
            api.setUrl(fullPath);
            api.setHttpMethod(httpMethod);
            api.setControllerClass(className);
            api.setMethodName(method.getNameAsString());
            api.setFramework("SPRING_MVC");

            // 提取参数信息
            String params = extractParameters(method);
            api.setParameters(params);

            return api;
        }

        return null;
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
                        if (pair.getNameAsString().equals("value") || pair.getNameAsString().equals("path")) {
                            return pair.getValue().toString().replace("\"", "");
                        }
                    }
                }
            }
        }
        return "";
    }

    /**
     * 提取RequestMapping的method属性
     */
    private String extractRequestMappingMethod(AnnotationExpr annotation) {
        if (annotation instanceof NormalAnnotationExpr) {
            NormalAnnotationExpr normal = (NormalAnnotationExpr) annotation;
            for (MemberValuePair pair : normal.getPairs()) {
                if (pair.getNameAsString().equals("method")) {
                    String method = pair.getValue().toString();
                    if (method.contains("GET")) return "GET";
                    if (method.contains("POST")) return "POST";
                    if (method.contains("PUT")) return "PUT";
                    if (method.contains("DELETE")) return "DELETE";
                    if (method.contains("PATCH")) return "PATCH";
                }
            }
        }
        return "GET"; // 默认GET
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