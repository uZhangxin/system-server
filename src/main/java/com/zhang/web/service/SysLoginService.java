package com.zhang.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhang.web.common.AjaxResult;
import com.zhang.web.common.RedisCache;
import com.zhang.web.common.constant.CacheConstants;
import com.zhang.web.common.constant.ConstantInfo;
import com.zhang.web.common.constant.HttpStatus;
import com.zhang.web.entity.SysUser;
import com.zhang.web.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * @Author: zhangxin
 * @Date: 2022/8/10 10:18
 * @Description:
 */
@Component
public class SysLoginService {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录验证
     *
     * @param username
     * @param password
     * @param code
     * @param uuid
     * @return
     */
    public AjaxResult login(String username, String password, String code, String uuid) {
        AjaxResult ajaxResult = new AjaxResult();
        Integer validateResult = validateCaptcha(code,uuid);
        // 验证码过期或错误
        if(validateResult != 1){
            ajaxResult.put("code", HttpStatus.UNAUTHORIZED);
            if(validateResult == -1){
                ajaxResult.put("msg","验证码过期");
            }
            if(validateResult == 0){
                ajaxResult.put("msg","验证码错误");
            }
            return ajaxResult;
        }
        // 验证用户名、密码
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", username)
                .eq("password", password);
        SysUser user = userService.getOne(queryWrapper);
        if (user == null) {
            ajaxResult.put("code", HttpStatus.UNAUTHORIZED);
            ajaxResult.put("msg", "用户名密码不正确");
            return ajaxResult;
        }
        ajaxResult = AjaxResult.success();
        ajaxResult.put("msg","登录成功！");

        // token包含内容
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        // 生成token令牌
        String token = tokenService.createToken(loginUser);
        ajaxResult.put(ConstantInfo.TOKEN,token);
        return ajaxResult;
    }

    /**
     * 验证码验证
     *
     * @param code
     * @param uuid
     * @return -1验证码过期，0验证码错误，1验证码正确
     */
    public Integer validateCaptcha(String code, String uuid) {
        // 拼接验证码key
        String codeKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
        // 获取redis中验证码
        String redisCode = redisCache.getCacheObject(codeKey);
        // 验证码过期
        if (redisCode == null) {
            return -1;
        } else {
            redisCache.deleteObject(codeKey);
            // 验证码正确
            if(code.equals(redisCode)){
                return 1;
            }
            else {
                return 0;
            }
        }
    }

}
