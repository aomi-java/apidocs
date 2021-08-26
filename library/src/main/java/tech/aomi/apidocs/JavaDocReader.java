package tech.aomi.apidocs;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Sean createAt 2021/8/25
 */
public class JavaDocReader {

    private static RootDoc root;

    public static class Doclet {

        public Doclet() {
        }

        public static boolean start(RootDoc root) {
            JavaDocReader.root = root;
            return true;
        }
    }

    public static ClassDoc[] read(List<String> filepath) {
        // 调用com.sun.tools.javadoc.Main执行javadoc,参见 参考资料3
        // javadoc的调用参数，参见 参考资料1
        // -doclet 指定自己的docLet类名
        // -classpath 参数指定 源码文件及依赖库的class位置，不提供也可以执行，但无法获取到完整的注释信息(比如annotation)
        // -encoding 指定源码文件的编码格式

        List<String> options = new ArrayList<>();

        options.add("-doclet");
        options.add(Doclet.class.getName());

        options.add("-docletpath");
        options.add(Objects.requireNonNull(Doclet.class.getResource("/")).getPath());

        options.add("-classpath");
        options.add(System.getProperty("java.class.path"));

        options.add("-encoding");
        options.add("utf-8");

        options.addAll(filepath);

        com.sun.tools.javadoc.Main.execute(options.toArray(new String[0]));

        return root.classes();
    }
}
