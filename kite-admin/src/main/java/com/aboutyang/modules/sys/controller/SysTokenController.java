package com.aboutyang.modules.sys.controller;

import com.aboutyang.common.annotation.SysLog;
import com.aboutyang.common.utils.PageUtils;
import com.aboutyang.common.utils.R;
import com.aboutyang.modules.sys.service.SysUserTokenService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author aboutyang
 */
@RestController
@RequestMapping("/sys/token")
public class SysTokenController {

    @Autowired
    private SysUserTokenService sysUserTokenService;

    /**
     * 所有用户列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:user:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysUserTokenService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 删除用户
     */
    @SysLog("踢出用户")
    @RequestMapping("/logout")
    @RequiresPermissions("sys:user:delete")
    public R forceLogout(@RequestParam String token) {
        sysUserTokenService.forceLogout(token);
        return R.ok();
    }

}
