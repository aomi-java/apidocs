package tech.aomi.apidocs.demo.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Sean createAt 2021/8/26
 */
@Getter
@Setter
public class User {

    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phoneNo;


    /**
     * 创建时间
     */
    private Date createAt;
}
