package com.aboutyang.modules.sys.controller;

import com.aboutyang.common.annotation.SysLog;
import com.aboutyang.common.exception.KiteException;
import com.aboutyang.common.utils.Constant;
import com.aboutyang.common.utils.R;
import com.aboutyang.modules.sys.entity.SysMenuEntity;
import com.aboutyang.modules.sys.service.SysMenuService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统菜单
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends AbstractController {
    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 导航菜单
     */
    @RequestMapping("/nav")
    public R nav() {
        List<SysMenuEntity> menuList = sysMenuService.getUserMenuList(getUserId());
        return R.ok().put("menuList", menuList);
    }

    /**
     * 所有菜单列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:menu:list")
    public List<SysMenuEntity> list() {
        List<SysMenuEntity> menuList = sysMenuService.queryAllMenu();

        Map<Long, String> menuIdNames = Maps.newHashMap();
        menuList.stream().forEach(menu -> {
            menuIdNames.put(menu.getMenuId(), menu.getName());
        });
        for (SysMenuEntity sysMenuEntity : menuList) {
            sysMenuEntity.setParentName(menuIdNames.get(sysMenuEntity.getParentId()));
        }
        return menuList;
    }

    /**
     * 所有菜单列表
     */
    @RequestMapping("/tree")
    @RequiresPermissions("sys:menu:list")
    public R tree() {
        List<SysMenuEntity> menuList = sysMenuService.queryAllMenu();

        // menuList 已经存储， 不能再增减操作。
        List<SysMenuEntity> copyList = Lists.newArrayList();
        copyList.addAll(menuList);

        List<SysMenuEntity> data = buildTree(0L, null, copyList);
        return R.ok().put("treeMenu", data);
    }

    public List<SysMenuEntity> buildTree(Long parentId, String parentName, List<SysMenuEntity> list) {
        List<SysMenuEntity> children = list.stream().filter(sysMenuEntity -> {
            if (sysMenuEntity.getParentId() == parentId) {
                sysMenuEntity.setParentName(parentName);
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        if(!children.isEmpty()){
            list.removeAll(children);
            children.stream().forEach(sysMenuEntity -> {
                List<SysMenuEntity> sub = buildTree(sysMenuEntity.getMenuId(), sysMenuEntity.getName(), list);
                if(!sub.isEmpty()){
                    sysMenuEntity.setChildren(sub);
                }
            });
        }
        return children;
    }

    /**
     * 选择菜单(添加、修改菜单)
     */
    @RequestMapping("/select")
    @RequiresPermissions("sys:menu:select")
    public R select() {
        //查询列表数据
        List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList();

        List<SysMenuEntity> copyList = Lists.newArrayList();
        copyList.addAll(menuList);

        List<SysMenuEntity> data = buildTree(0L, null, copyList);

        return R.ok().put("menuList", data);
    }

    /**
     * 菜单信息
     */
    @RequestMapping("/info/{menuId}")
    @RequiresPermissions("sys:menu:info")
    public R info(@PathVariable("menuId") Long menuId) {
        SysMenuEntity menu = sysMenuService.selectById(menuId);
        return R.ok().put("menu", menu);
    }

    /**
     * 保存
     */
    @SysLog("保存菜单")
    @RequestMapping("/save")
    @RequiresPermissions("sys:menu:save")
    public R save(@RequestBody SysMenuEntity menu) {
        //数据校验
        verifyForm(menu);

        sysMenuService.save(menu);

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改菜单")
    @RequestMapping("/update")
    @RequiresPermissions("sys:menu:update")
    public R update(@RequestBody SysMenuEntity menu) {
        //数据校验
        verifyForm(menu);

        sysMenuService.update(menu);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除菜单")
    @RequestMapping("/delete")
    @RequiresPermissions("sys:menu:delete")
    public R delete(long menuId) {
        if (menuId <= 31) {
            return R.error("系统菜单，不能删除");
        }

        //判断是否有子菜单或按钮
        List<SysMenuEntity> menuList = sysMenuService.queryListParentId(menuId);
        if (menuList.size() > 0) {
            return R.error("请先删除子菜单或按钮");
        }

        sysMenuService.delete(menuId);

        return R.ok();
    }

    /**
     * 验证参数是否正确
     */
    private void verifyForm(SysMenuEntity menu) {
        if (StringUtils.isBlank(menu.getName())) {
            throw new KiteException("菜单名称不能为空");
        }

        if (menu.getParentId() == null) {
            throw new KiteException("上级菜单不能为空");
        }

        //菜单
        if (menu.getType() == Constant.MenuType.MENU.getValue()) {
            if (StringUtils.isBlank(menu.getUrl())) {
                throw new KiteException("菜单URL不能为空");
            }
        }

        //上级菜单类型
        int parentType = Constant.MenuType.CATALOG.getValue();
        if (menu.getParentId() != 0) {
            SysMenuEntity parentMenu = sysMenuService.selectById(menu.getParentId());
            parentType = parentMenu.getType();
        }

        //目录、菜单
        if (menu.getType() == Constant.MenuType.CATALOG.getValue() ||
                menu.getType() == Constant.MenuType.MENU.getValue()) {
            if (parentType != Constant.MenuType.CATALOG.getValue()) {
                throw new KiteException("上级菜单只能为目录类型");
            }
            return;
        }

        //按钮
        if (menu.getType() == Constant.MenuType.BUTTON.getValue()) {
            if (parentType != Constant.MenuType.MENU.getValue()) {
                throw new KiteException("上级菜单只能为菜单类型");
            }
            return;
        }
    }
}
