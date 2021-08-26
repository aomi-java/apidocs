package tech.aomi.apidocs.demo;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import tech.aomi.apidocs.demo.entity.User;
import tech.aomi.apidocs.demo.form.CreateUserForm;

import javax.validation.Valid;

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
     * @param age     年龄
     * @return 用户信息 {@link org.springframework.data.domain.Page} {@link User}
     */
    @GetMapping
    public Page<User> showAll(@RequestParam String name, String phoneNo, @RequestParam(defaultValue = "18") Integer age) {
        return null;
    }

    /**
     * 获取指定用户
     * 根据ID获取指定用户信息
     *
     * @param id 用户ID
     * @return 用户信息 {@link User}
     */
    @GetMapping("/{id}")
    public User findOne(@PathVariable String id) {
        return null;
    }

    /**
     * 创建用户
     *
     * @param form 用户表单
     * @return 用户信息 {@link User}
     */
    @PostMapping
    public User create(@RequestBody @Valid CreateUserForm form) {
        return null;
    }
}
