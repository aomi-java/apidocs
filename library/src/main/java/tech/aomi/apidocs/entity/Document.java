package tech.aomi.apidocs.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sean createAt 2021/8/25
 */
@Getter
@Setter
public class Document {

    private String filename;

    /**
     * 文档标题
     */
    private String title = "-";

    /**
     *
     */
    private String describe;

    /**
     * 文档中对应的接口
     */
    private List<Api> apis = new ArrayList<>();

}
