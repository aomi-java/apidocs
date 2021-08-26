package tech.aomi.apidocs.plugins;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import tech.aomi.apidocs.DocsOptions;
import tech.aomi.apidocs.entity.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sean createAt 2021/8/25
 */
public class DocsifyBuilderPlugin implements BuilderPlugin {

    private final static String TEMPLATE = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <title>Document</title>\n" +
            "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\" />\n" +
            "  <meta name=\"description\" content=\"Description\">\n" +
            "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0\">\n" +
            "  <link rel=\"stylesheet\" href=\"//cdn.jsdelivr.net/npm/docsify@4/lib/themes/vue.css\">\n" +
            "</head>\n" +
            "<body>\n" +
            "  <div id=\"app\"></div>\n" +
            "  <script>\n" +
            "    window.$docsify = {\n" +
            "      name: '',\n" +
            "      repo: '',\n" +
            "      loadSidebar: true,\n" +
            "    }\n" +
            "  </script>\n" +
            "  <!-- Docsify v4 -->\n" +
            "  <script src=\"//cdn.jsdelivr.net/npm/docsify@4\"></script>\n" +
            "</body>\n" +
            "</html>\n";


    @Override
    @SneakyThrows
    public void execute(DocsOptions options, List<Document> documents) {
        File docsDir = new File(options.getDocsPath());
        if (!docsDir.isDirectory()) {
            boolean result = docsDir.mkdirs();
        }
        File readme = new File(docsDir.getAbsolutePath() + File.separator + "README.md");
        FileUtils.write(readme, "# API Docs", "utf-8");

        File nojekyll = new File(docsDir.getAbsolutePath() + File.separator + ".nojekyll");
        FileUtils.write(nojekyll, "", "utf-8");

        File indexHtml = new File(docsDir.getAbsolutePath() + File.separator + "index.html");
        FileUtils.write(indexHtml, TEMPLATE, "utf-8");

        File sidebar = new File(docsDir.getAbsolutePath() + File.separator + "_sidebar.md");

        List<String> lines = new ArrayList<>();

        documents.forEach(document -> {
            lines.add("* " + document.getTitle());

            document.getApis().forEach(api -> {
                String path = "/" + document.getFilename() + "/" + api.getTitle().replaceAll(" ", "%20") + ".md";
                lines.add("  * [" + api.getTitle() + "](" + path + ")");
            });
        });

        FileUtils.writeLines(sidebar, lines);
    }

}
