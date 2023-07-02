package com.zhang.web.common;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.util.Random;

/**
 * @Author: zhangxin
 * @Date: 2022/8/23 19:20
 * @Description:
 */
public class KaptchaTextCreator extends DefaultTextCreator {
    /**
     * 生成文本表达式
     *
     * @return
     */
    @Override
    public String getText() {
        Random random = new Random();
        // 返回式子文本结果
        StringBuilder resultText = new StringBuilder();
        // 式子计算结果
        Integer result = 0;
        // 随机生成两个[1,9]范围内的整数，用于计算
        Integer x = random.nextInt(9) + 1;
        Integer y = random.nextInt(9) + 1;
        // 随机生成对应操作运算符,0减法，1乘法，2减法
        Integer operate = random.nextInt(3);
        if (operate == 1) {
            // 生成乘法式子
            result = x * y;
            resultText.append(x + "*" + y);
        } else if (operate == 2) {
            // 生成加法式子
            result = x + y;
            resultText.append(x + "+" + y);
        } else {
            // 生成减法式子
            if (x >= y) {
                result = x - y;
                resultText.append(x + "-" + y);
            } else {
                result = y - x;
                resultText.append(y + "-" + x);
            }
        }
        resultText.append("=?@" + result);
        return resultText.toString();
    }
}
