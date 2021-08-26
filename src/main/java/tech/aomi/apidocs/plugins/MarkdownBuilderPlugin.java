package tech.aomi.apidocs.plugins;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import tech.aomi.apidocs.Column;
import tech.aomi.apidocs.DocsOptions;
import tech.aomi.apidocs.entity.ApiField;
import tech.aomi.apidocs.entity.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sean createAt 2021/8/25
 */
public class MarkdownBuilderPlugin implements BuilderPlugin {

    private DocsOptions options;

    @Override
    public void execute(DocsOptions options, List<Document> documents) {
        this.options = options;
        documents.parallelStream().forEach(this::handler);
    }

    @SneakyThrows
    public void handler(Document document) {
        File docsDir = new File(this.options.getDocsPath() + File.separator + document.getFilename());
        if (!docsDir.isDirectory()) {
            boolean result = docsDir.mkdirs();
//            LOGGER.debug("文件创建结果: {}", result);
        }

        document.getApis().forEach(api -> {
            File file = new File(docsDir.getAbsolutePath() + File.separator + api.getTitle() + ".md");
            List<String> lines = new ArrayList<>();
            lines.add("#" + " " + document.getTitle());
            lines.add(document.getDescribe());

            lines.add("\n###" + " " + api.getTitle());
            lines.add(api.getDescribe());

            lines.add("\n#### Request Method&Request Url");
            lines.add("```");
            lines.add(String.join(";", api.getMethods()) + " " + String.join(";", api.getUrls()));
            lines.add("```");

            if (null != api.getPath() && api.getPath().size() > 0) {
                lines.add("\n#### Request Path Variable");
                handleField(lines, Column.HEADER_COLUMNS, api.getPath());
            }

            if (null != api.getHeaders() && api.getHeaders().size() > 0) {
                lines.add("\n#### Request Headers");
                handleField(lines, Column.HEADER_COLUMNS, api.getHeaders());
            }

            if (null != api.getQueryString() && api.getQueryString().size() > 0) {
                lines.add("\n#### Request Query String");
                handleField(lines, Column.HEADER_COLUMNS, api.getQueryString());
            }

            if (null != api.getBody() && api.getBody().size() > 0) {
                lines.add("\n#### Request Body");
                handleField(lines, Column.HEADER_COLUMNS, api.getBody());
            }

            lines.add("\n#### Response");
            lines.add(api.getResponseDescribe());
            if (null != api.getResponseHeaders() && api.getResponseHeaders().size() > 0) {
                lines.add("\n#### Response Headers");
                handleField(lines, Column.HEADER_COLUMNS, api.getResponseHeaders());
            }
            if (null != api.getResponseBody() && api.getResponseBody().size() > 0) {
                lines.add("\n#### Response Body");
                handleField(lines, Column.HEADER_COLUMNS, api.getBody());
            }

            lines.add("\n");

            try {
                FileUtils.writeLines(file, lines);
            } catch (IOException ignored) {
            }
        });

    }

    private void handleField(List<String> lines, String[] columns, List<ApiField> apiFields) {
        handleHeader(lines, columns);
        apiFields.forEach(field -> {
            String[] values = new String[]{
                    field.getName(),
                    field.getType(),
                    field.getRequired(),
                    field.getDescribe(),
                    field.getDefaultValue()
            };
            String valueStr = String.join("|", values);
            valueStr = valueStr.replaceAll("\n", "<br />");
            lines.add("|" + valueStr + "|");
        });
    }

    private void handleHeader(List<String> lines, String[] columns) {
        lines.add("|" + String.join("|", columns) + "|");
        String[] line = new String[columns.length];
        Arrays.fill(line, ":-------:");
        lines.add("|" + String.join("|", line) + "|");
    }

}
