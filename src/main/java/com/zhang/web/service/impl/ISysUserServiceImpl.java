package com.zhang.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhang.web.mapper.SysUserMapper;
import com.zhang.web.entity.SysUser;
import com.zhang.web.service.ISysUserService;
import org.springframework.stereotype.Service;

/**
 * @Author: zhangxin
 * @Date: 2022/8/10 9:36
 * @Description:
 */
@Service
public class ISysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
}
