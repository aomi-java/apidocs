# Java Api Docs

本项目宗旨： 无代码侵入,使用原生javadoc注释生成API接口文档。

## 注释编写规则

#### 文档标题

根据`class`注释生成；`class`注释的第一行为标题. 例如:

```java

/**
 * 我的标题
 */
public class Controller {

}
```

#### 文档概括

根据`class`注释生成；`class` 注释的第二行起为文档概括.例如:

```java
/**
 * 我的标题
 * 我是概括我是概括我是概括我是概括我是概括我是概括
 * 我是概括我是概括我是概括我是概括我是概括我是概括
 */
public class Controller {

}
```

#### 接口列表

接口列表根据`class`中的方法生成.

## Spring Web MVC Api 文档生成

自动根据`Spring Web`项目生成API接口文档

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
