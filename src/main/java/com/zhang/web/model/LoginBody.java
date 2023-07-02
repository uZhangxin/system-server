package com.zhang.web.model;

import lombok.*;

/**
 * @Author: zhangxin
 * @Date: 2022/8/10 14:21
 * @Description: 登录表单内数据对象
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginBody {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String uuid;
}
