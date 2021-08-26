package tech.aomi.apidocs;

import lombok.Getter;
import tech.aomi.apidocs.plugins.BuilderPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sean createAt 2021/8/26
 */
@Getter
public class DocsOptions {

    /**
     * 项目路径
     */
    private final String srcPath;

    private final String docsPath;

    private final List<BuilderPlugin> builderPlugins;

    private DocsOptions(Builder builder) {
        this.srcPath = builder.srcPath;
        this.docsPath = builder.docsPath;
        this.builderPlugins = builder.builderPlugins;
    }

    public static class Builder {

        private String srcPath = "src/main/java";

        private String docsPath = "apidocs";

        private List<BuilderPlugin> builderPlugins = new ArrayList<>();

        public DocsOptions build() {
            return new DocsOptions(this);
        }

        public Builder srcPath(String srcPath) {
            if (null == srcPath) {
                throw new IllegalArgumentException("srcPath not be null.");
            }
            this.srcPath = srcPath;
            return this;
        }

        public Builder docsPath(String docsPath) {
            if (null == docsPath) {
                throw new IllegalArgumentException("docsPath not be null");
            }
            this.docsPath = docsPath;
            return this;
        }

        public Builder builderPlugins(List<BuilderPlugin> plugins) {
            if (null == plugins) {
                throw new IllegalArgumentException("builderPlugins not be null");
            }
            this.builderPlugins = plugins;
            return this;
        }

        public Builder builderPlugin(BuilderPlugin builderPlugin) {
            if (null == builderPlugin) {
                throw new IllegalArgumentException("BuilderPlugin not be null");
            }
            this.builderPlugins.add(builderPlugin);
            return this;
        }
    }
}
