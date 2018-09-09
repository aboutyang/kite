package com.aboutyang.modules.sys.controller;


import com.aboutyang.common.annotation.SysLog;
import com.aboutyang.common.utils.PageUtils;
import com.aboutyang.common.utils.R;
import com.aboutyang.common.validator.Assert;
import com.aboutyang.common.validator.ValidatorUtils;
import com.aboutyang.common.validator.group.AddGroup;
import com.aboutyang.common.validator.group.UpdateGroup;
import com.aboutyang.modules.sys.entity.SysUserEntity;
import com.aboutyang.modules.sys.service.SysUserRoleService;
import com.aboutyang.modules.sys.service.SysUserService;
import com.aboutyang.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 系统用户
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController extends AbstractController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    /**
     * 所有用户列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:user:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = sysUserService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 获取登录的用户信息
     */
    @RequestMapping("/info")
    public R info() {
        return R.ok().put("user", getUser());
    }

    /**
     * 修改登录用户密码
     */
    @SysLog("修改密码")
    @RequestMapping("/password")
    public R password(String password, String newPassword) {
        Assert.isBlank(newPassword, "新密码不为能空");

        //原密码
        password = ShiroUtils.sha256(password, getUser().getSalt());
        //新密码
        newPassword = ShiroUtils.sha256(newPassword, getUser().getSalt());

        //更新密码
        boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
        if (!flag) {
            return R.error("原密码不正确");
        }

        return R.ok();
    }

    /**
     * 用户信息
     */
    @RequestMapping("/info/{userId}")
    @RequiresPermissions("sys:user:info")
    public R info(@PathVariable("userId") Long userId) {
        SysUserEntity user = sysUserService.selectById(userId);

        //获取用户所属的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
        user.setRoleIdList(roleIdList);

        return R.ok().put("user", user);
    }

    /**
     * 保存用户
     */
    @SysLog("保存用户")
    @RequestMapping("/save")
    @RequiresPermissions("sys:user:save")
    public R save(@RequestBody SysUserEntity user) {
        ValidatorUtils.validateEntity(user, UpdateGroup.class);
        sysUserService.save(user);

        return R.ok();
    }

    @SysLog("生成用户")
    @RequestMapping("/generator")
    @RequiresPermissions("sys:user:save")
    public R generator() {
        String basename = RandomStringUtils.randomAlphabetic(8);
        IntStream.range(1, 100).mapToObj(i -> {
            SysUserEntity user = new SysUserEntity();
            user.setUsername(basename + "_" + i);
            user.setPassword("test");
            user.setStatus(1);
            user.setMobile(basename);
            user.setEmail(basename + "_" + i + "@163.com");
            user.setDeptId(1L);
            return user;
        }).forEach(user -> sysUserService.save(user));
        return R.ok();
    }

    /**
     * 修改用户
     */
    @SysLog("修改用户")
    @RequestMapping("/update")
    @RequiresPermissions("sys:user:update")
    public R update(@RequestBody SysUserEntity user) {
        ValidatorUtils.validateEntity(user, UpdateGroup.class);

        sysUserService.update(user);

        return R.ok();
    }

    /**
     * 删除用户
     */
    @SysLog("删除用户")
    @RequestMapping("/delete")
    @RequiresPermissions("sys:user:delete")
    public R delete(@RequestBody Long[] userIds) {
        if (ArrayUtils.contains(userIds, 1L)) {
            return R.error("系统管理员不能删除");
        }

        if (ArrayUtils.contains(userIds, getUserId())) {
            return R.error("当前用户不能删除");
        }

        sysUserService.deleteBatchIds(Arrays.asList(userIds));

        return R.ok();
    }


    /**
     * 启用 禁用用户
     */
    @SysLog("禁用用户")
    @RequestMapping("/status/{status}")
    @RequiresPermissions("sys:user:save")
    public R changeStatus(@RequestBody Long[] userIds,@PathVariable(name = "status") String status) {
        if(StringUtils.equals("lock", status)){
            sysUserService.batchUpdateStatus(userIds, 0);
        }else if(StringUtils.equals("unlock", status)){
            sysUserService.batchUpdateStatus(userIds, 1);
        }else {
            throw new UnsupportedOperationException(status);
        }
        return R.ok();
    }
}
