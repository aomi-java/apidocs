package tech.aomi.apidocs.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sean createAt 2021/8/24
 */
@Getter
@Setter
@ToString
public class ApiField {

    private String name = "";
    /**
     * 字段
     */
    private String type = "";
    /**
     * 是否必填
     * M 强制域(Mandatory)，此域在该消息中必须出现否则将被认为消息格式出错。
     * C 条件域(Conditional)，此域在一定条件下出现在该消息中，具体的条件请参考备注说明。
     * O 选用域(Optional)，此域在该消息中由发送方自选。
     */
    private String required = "O";

    /**
     * 描述
     */
    private String describe = "";

    /**
     * 默认值
     */
    private String defaultValue = "";
}
