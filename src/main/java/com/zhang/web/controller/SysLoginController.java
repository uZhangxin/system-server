package com.zhang.web.controller;

import com.google.code.kaptcha.Producer;
import com.zhang.web.common.AjaxResult;
import com.zhang.web.common.RedisCache;
import com.zhang.web.common.constant.CacheConstants;
import com.zhang.web.common.constant.ConstantInfo;
import com.zhang.web.model.LoginBody;

import com.zhang.web.service.ISysUserService;
import com.zhang.web.service.SysLoginService;
import com.zhang.web.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangxin
 * @Date: 2022/8/10 9:58
 * @Description:
 */
@RestController
public class SysLoginController {
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    SysLoginService sysLoginService;


    /**
     * 验证登录
     *
     * @param loginBody
     * @return
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        AjaxResult ajaxResult = sysLoginService.login(loginBody.getUserName(), loginBody.getPassword(), loginBody.getCode(), loginBody.getUuid());
        return ajaxResult;
    }

    /**
     * @param codeType 验证码类型，0:运算，1:文本
     * @return
     */
    @GetMapping("/captchaImage")
    public AjaxResult getCodeImg(Integer codeType) {
        AjaxResult ajaxResult = AjaxResult.success();
        // 生成redis key
        String uuid = IdUtils.simpleUUID();
        String codeKey = CacheConstants.CAPTCHA_CODE_KEY + uuid;
        // 表达式图片流
        BufferedImage image = null;
        // 验证码文本
        String capText = null;
        // 验证码结果
        String result = null;
        // 生成运算式验证码
        if (codeType == 0) {
            // 生成运算式验证码文本，6-1=?@5
            capText = captchaProducerMath.createText();
            // 获取运算表达式
            String capStr = capText.substring(0, capText.lastIndexOf("@"));
            // 获取运算结果
            result = capText.substring(capText.lastIndexOf("@") + 1);
            // 生成运算式验证码图片
            image = captchaProducerMath.createImage(capStr);
        }
        // 生成文本验证码
        if (codeType == 1) {
            // 生成文本式验证码文本
            result = captchaProducer.createText();
            // 生成文本式验证码图片
            image = captchaProducer.createImage(result);
        }
        // 将验证码值存入Redis并设置有效时间为
        redisCache.setCacheObject(codeKey, result, ConstantInfo.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }
        ajaxResult.put("uuid", uuid);
        ajaxResult.put("img", os.toByteArray());
        return ajaxResult;
    }
}
