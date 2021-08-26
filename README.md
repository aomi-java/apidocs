# Java Api Docs

自动根据`Spring MVC`生成`Markdown`格式的API文档. 同时生成`docsify`网站

例如:

```java

```

### 如何使用

#### Spring MVC 项目api文档生成

```java
import org.junit.Test;
import tech.aomi.apidocs.DocsOptions;
import tech.aomi.apidocs.SpringMvcApiDocs;
import tech.aomi.apidocs.plugins.DocsifyBuilderPlugin;
import tech.aomi.apidocs.plugins.MarkdownBuilderPlugin;

public class DocsTest {

    @Test
    public void generate() {
        new SpringMvcApiDocs(new DocsOptions.Builder()
                .srcPath("src/main/java") // 源代码路径
                .docsPath("apidocs") // 生成的文档目录
                .builderPlugin(new MarkdownBuilderPlugin()) // 生成 Markdown 文件
                .builderPlugin(new DocsifyBuilderPlugin()) // 生成Docsify 文档网站
                .build()
        ).generate();

    }
}
```
