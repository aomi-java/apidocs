package tech.aomi.apidocs.demo.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * @author Sean createAt 2021/8/26
 */
@Getter
@Setter
public class CreateUserForm {


    /**
     * 姓名
     */
    @NotEmpty
    private String name;

    /**
     * 手机号
     */
    @NotBlank
    private String phoneNo;

    private String nickname;

    private String age;
}
