package tech.aomi.apidocs.plugins;

import tech.aomi.apidocs.DocsOptions;
import tech.aomi.apidocs.entity.Document;

import java.util.List;

/**
 * 文档构建插件
 *
 * @author Sean createAt 2021/8/26
 */
public interface BuilderPlugin {

    /**
     * 执行文档生成工作
     *
     * @param options   文档构建选项
     * @param documents 文档接口信息
     */
    void execute(DocsOptions options, List<Document> documents);

}
