package tech.aomi.apidocs;

import com.sun.javadoc.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import tech.aomi.apidocs.entity.Api;
import tech.aomi.apidocs.entity.ApiField;
import tech.aomi.apidocs.entity.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Spring MVC Api 文档生成实现
 *
 * @author Sean createAt 2021/8/24
 */
public class SpringMvcApiDocs implements ApiDocs {

    private static final List<String> CONTROLLER_ANNOTATIONS = Arrays.asList(
            "RestController",
            "RequestMapping"
    );

    private static final List<String> METHOD_ANNOTATIONS = Arrays.asList(
            "RequestMapping",
            "GetMapping",
            "PostMapping",
            "PutMapping",
            "DeleteMapping",
            "PatchMapping"
    );

    private static final List<String> PARAMETER_QUERY_STRING_ANNOTATIONS = Arrays.asList(
            "RequestParam",
            "RequestHeader",
            "DateTimeFormat"
    );

    private static final List<String> FIELD_REQUIRED_ANNOTATIONS = Arrays.asList(
            "NotEmpty",
            "NotBlank",
            "NotNull"
    );

    private static final List<String> PARAMETER_PATH_VAR_ANNOTATIONS = Arrays.asList("PathVariable");

    private static final List<String> PARAMETER_BODY_ANNOTATIONS = Arrays.asList("RequestBody");

    private static final Pattern LINK_PATTERN = Pattern.compile("^\\{@link.*}$");

    private final DocsOptions options;

    public SpringMvcApiDocs(DocsOptions options) {
        this.options = options;
    }

    @Override
    @SneakyThrows
    public void generate() {
        List<Document> documents = new ArrayList<>();

        getAllController().forEach(classDoc -> {
            Document document = initDocument(classDoc);
            String basePath = findRequestMappingPath(classDoc.annotations());

            MethodDoc[] methodDocs = classDoc.methods();
            if (null == methodDocs || methodDocs.length == 0) {
                return;
            }
            for (MethodDoc methodDoc : methodDocs) {
                createApi(document, basePath, classDoc, methodDoc);
            }

            documents.add(document);
        });

        options.getBuilderPlugins().forEach(builderPlugin -> builderPlugin.execute(options, documents));
    }

    private void createApi(Document document, String basePath, ClassDoc classDoc, MethodDoc methodDoc) {
        String method = findMethod(methodDoc.annotations());
        if (null == method) {
            return;
        }
        Api api = initApi(document, methodDoc);
        api.setMethod(method);

        String path = findRequestMappingPath(methodDoc.annotations());
        api.setUrl(basePath + path);


        com.sun.javadoc.Parameter[] parameters = methodDoc.parameters();
        for (com.sun.javadoc.Parameter parameter : parameters) {
            createApiField(api, methodDoc, parameter);
        }
        document.getApis().add(api);
    }

    private void createApiField(Api api, MethodDoc methodDoc, com.sun.javadoc.Parameter parameter) {
        AnnotationDesc[] annotationDescs = parameter.annotations();

        if (null == annotationDescs || annotationDescs.length == 0) {
            createApiQueryStringField(api, methodDoc, parameter, null);
            return;
        }

        AnnotationDesc pathAnnotationDesc = Arrays.stream(annotationDescs).filter(desc -> PARAMETER_PATH_VAR_ANNOTATIONS.contains(desc.annotationType().name())).findFirst().orElse(null);
        if (null != pathAnnotationDesc) {
            createApiPathVarField(api, methodDoc, parameter, pathAnnotationDesc);
            return;
        }

        AnnotationDesc bodyAnnotationDesc = Arrays.stream(annotationDescs).filter(desc -> PARAMETER_BODY_ANNOTATIONS.contains(desc.annotationType().name())).findFirst().orElse(null);
        if (null != bodyAnnotationDesc) {
            createApiBodyField(api, methodDoc, parameter, bodyAnnotationDesc);
            return;
        }

        List<AnnotationDesc> queryStringAnnotationDescs = Arrays.stream(annotationDescs).filter(desc -> PARAMETER_QUERY_STRING_ANNOTATIONS.contains(desc.annotationType().name())).collect(Collectors.toList());
        createApiQueryStringField(api, methodDoc, parameter, queryStringAnnotationDescs);

    }

    /**
     * 创建Request Body 对应的字段信息
     *
     * @param api
     * @param methodDoc
     * @param parameter
     * @param bodyAnnotationDesc
     */
    private void createApiBodyField(Api api, MethodDoc methodDoc, com.sun.javadoc.Parameter parameter, AnnotationDesc bodyAnnotationDesc) {
//        if (clazz.isAssignableFrom(String.class)) {
//            return;
//        } else if (clazz.isAssignableFrom(byte[].class)) {
//            return;
//        } else if (clazz.isAssignableFrom(Boolean.class)) {
//            return;
//        } else if (clazz.isAssignableFrom(Long.class)) {
//            return;
//        }

//        FieldDoc[] fieldDocs = classDoc.fields(false);
//
//        Field[] fields = clazz.getDeclaredFields();
//        Arrays.stream(fields).forEach(field -> {
//            ApiField apiField = getApiField(field);
//
//            Arrays.stream(fieldDocs).filter(item -> item.name().equals(field.getName())).findFirst().ifPresent(fieldDoc -> {
//                apiField.setDescribe(fieldDoc.commentText());
//            });
//
//            apiFields.add(apiField);
//        });
        if (Arrays.asList("byte[]").contains(parameter.typeName())) {

            return;
        }

        FieldDoc[] fieldDocs = parameter.type().asClassDoc().fields(false);
        Arrays.stream(fieldDocs).forEach(fieldDoc -> {
            if (fieldDoc.isTransient() || fieldDoc.isVolatile() || fieldDoc.isStatic()) {
                return;
            }
            ApiField apiField = new ApiField();
            apiField.setName(fieldDoc.name());
            apiField.setType(getType(fieldDoc.type().typeName()));
            apiField.setDescribe(fieldDoc.commentText());

            AnnotationDesc[] annotationDescs = fieldDoc.annotations();
            boolean required = Arrays.stream(annotationDescs).anyMatch(item -> FIELD_REQUIRED_ANNOTATIONS.contains(item.annotationType().name()));
            if (required) {
                apiField.setRequired("M");
            }


            api.getBody().add(apiField);
        });

    }

    private void createApiPathVarField(Api api, MethodDoc methodDoc, com.sun.javadoc.Parameter parameter, AnnotationDesc pathAnnotationDesc) {
        ApiField apiField = new ApiField();
        apiField.setName(parameter.name());
        apiField.setType(getType(parameter.typeName()));
        apiField.setRequired("M");

        ParamTag[] paramTags = methodDoc.paramTags();

        Arrays.stream(paramTags).filter(item -> item.parameterName().equals(apiField.getName())).findFirst().ifPresent(paramTag -> {
            apiField.setDescribe(paramTag.parameterComment());
        });

        api.getPath().add(apiField);
    }

    private void createApiQueryStringField(Api api, MethodDoc methodDoc, com.sun.javadoc.Parameter parameter, List<AnnotationDesc> annotationDescs) {
        if (parameter.type().qualifiedTypeName().startsWith("javax")) {
            return;
        }
        if (parameter.type().qualifiedTypeName().contains("springframework")) {
            return;
        }

        ApiField apiField = new ApiField();
        apiField.setName(parameter.name());
        apiField.setType(getType(parameter.typeName()));
        apiField.setRequired("O");

        ParamTag[] paramTags = methodDoc.paramTags();

        Arrays.stream(paramTags).filter(item -> item.parameterName().equals(apiField.getName())).findFirst().ifPresent(paramTag -> {
            apiField.setDescribe(paramTag.parameterComment());
        });

        if (null != annotationDescs && annotationDescs.size() > 0) {
            annotationDescs.forEach(annotationDesc -> {
                if ("RequestParam".equals(annotationDesc.annotationType().name())) {
                    apiField.setRequired("M");
                    AnnotationDesc.ElementValuePair[] valuePairs = annotationDesc.elementValues();
                    for (AnnotationDesc.ElementValuePair valuePair : valuePairs) {
                        if ("defaultValue".equals(valuePair.element().name())) {
                            AnnotationValue value = valuePair.value();
                            String v = (String) value.value();
                            apiField.setDefaultValue(v);
                            apiField.setRequired("O");
                        }
                    }
                }
            });

        }

        api.getQueryString().add(apiField);
    }

    private String getType(String type) {
        switch (type) {
            case "Integer":
            case "Long":
                return "number";
            case "BigDecimal":
                return "money";
            case "String":
                return "string";
            case "Date":
                return "date";
            case "Map":
                return "object";
            default:
                return type;
        }
    }

    private List<ClassDoc> getAllController() {
        List<String> files = getAllFile();

        return Arrays.stream(JavaDocReader.read(files))
                .filter(classDoc -> {
                    if (null == classDoc || classDoc.annotations().length == 0) {
                        return false;
                    }
                    return Arrays.stream(classDoc.annotations()).anyMatch(item -> CONTROLLER_ANNOTATIONS.contains(item.annotationType().name()));
                })
                .collect(Collectors.toList());
    }

    private List<String> getAllFile() {
        List<String> files = new ArrayList<>();
        File root = new File(options.getSrcPath());
        findFile(files, root);
        return files;
    }

    private void findFile(List<String> files, File file) {
        Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(item -> {
            if (item.isDirectory()) {
                findFile(files, item);
                return;
            }
            if (item.getName().endsWith(".java")) {
                files.add(item.getAbsolutePath());
            }
        });
    }

    /**
     * 初始化API信息
     *
     * @param document  api所在的文档
     * @param methodDoc api对应的处理方法
     * @return api
     */
    private Api initApi(Document document, MethodDoc methodDoc) {
        Api api = new Api();
        String commentText = methodDoc.commentText();

        String[] tmp = commentText.split("\n", 2);

        try {
            api.setTitle(tmp[0]);
            api.setDescribe(tmp[1]);
        } catch (Exception ignored) {
        }
        if (StringUtils.isEmpty(api.getTitle())) {
            api.setTitle(methodDoc.name());
        }

        Tag[] tags = methodDoc.tags();
        Arrays.stream(tags).filter(tag -> tag.name().equals("@return")).findFirst().ifPresent((tag) -> {
            String text = tag.text();
//                Matcher matcher = LINK_PATTERN.matcher(text);
//                if(matcher.find()){
//                    String s = matcher.group();
//                    System.out.println(s);
//                }

            api.setResponseDescribe(text);
        });

        return api;
    }

    /**
     * 初始化文档
     *
     * @param classDoc Controller classDoc
     * @return 文档基本信息
     */
    private Document initDocument(ClassDoc classDoc) {
        Document document = new Document();
        if (null == classDoc) {
//                LOGGER.error("读取注释失败 classDoc null");
            return document;
        }
        document.setFilename(classDoc.name());

        String[] tmp = classDoc.commentText().split("\n", 2);

        try {
            document.setTitle(tmp[0]);
            document.setDescribe(tmp[1]);
        } catch (Exception ignored) {
        }
        if (StringUtils.isEmpty(document.getTitle())) {
            document.setTitle(classDoc.name());
        }
        return document;
    }


    private String findRequestMappingPath(AnnotationDesc[] annotationDescs) {
        AnnotationDesc annotationDesc = Arrays.stream(annotationDescs).filter(desc -> METHOD_ANNOTATIONS.contains(desc.annotationType().name())).findFirst().orElse(null);
        if (null == annotationDesc) {
            return "";
        }
        AnnotationDesc.ElementValuePair[] elementValues = annotationDesc.elementValues();
        if (null == elementValues || elementValues.length == 0) {
            return "";
        }
        AnnotationDesc.ElementValuePair pair = elementValues[0];

        AnnotationValue[] values = (AnnotationValue[]) pair.value().value();
        if (null == values || values.length == 0) {
            return "";
        }
        AnnotationValue value = values[0];

        return StringUtils.trimToEmpty((String) value.value());
    }

    private String findMethod(AnnotationDesc[] annotationDescs) {
        AnnotationDesc annotationDesc = Arrays.stream(annotationDescs).filter(desc -> METHOD_ANNOTATIONS.contains(desc.annotationType().name())).findFirst().orElse(null);
        if (null == annotationDesc) {
            return null;
        }

        String name = annotationDesc.annotationType().name();
        switch (name) {
            case "PostMapping":
                return "POST";
            case "PutMapping":
                return "PUT";
            case "DeleteMapping":
                return "DELETE";
            case "PatchMapping":
                return "PATCH";
            default:
                return "GET";
        }
    }
}
