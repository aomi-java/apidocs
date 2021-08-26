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

#### 响应结果

根据方法注释生成响应结果说明.

1. 第一种写法

```java
/**
 * 我的标题
 * 我是概括我是概括我是概括我是概括我是概括我是概括
 * 我是概括我是概括我是概括我是概括我是概括我是概括
 */
public class Controller {

    /**
     * 我是接口标题
     * 我是接口说明
     *
     * @return 我是响应结果说明 {@link User} {@link Order}
     */
    // 自动识别@return 中的link标签中的对象作为响应结果
    public String hello() {

    }

} 
```

2. 第二种写法

支持Spring Page 分页结果展示. 当`@return`中的第一个`link`标签是`{@link org.springframework.data.domain.Page}`时，自动生成分页相关对象信息

```java
/**
 * 用户服务
 * 提供用户的信息的增删改查接口
 *
 * @author Sean createAt 2021/8/26
 */
@RestController
@RequestMapping("/users")
public class UserController {


    /**
     * 查询全部用户信息
     * 根据查询条件查询用户信息
     *
     * @param name    姓名
     * @param phoneNo 手机号
     * @return 用户信息 {@link org.springframework.data.domain.Page} {@link User}
     */
    @GetMapping
    public Page<User> showAll(String name, String phoneNo) {
        return null;
    }

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
