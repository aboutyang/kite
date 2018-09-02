package com.aboutyang.modules.sys.service.impl;


import com.aboutyang.common.utils.Constant;
import com.aboutyang.common.utils.MapUtils;
import com.aboutyang.modules.sys.dao.SysMenuDao;
import com.aboutyang.modules.sys.entity.SysMenuEntity;
import com.aboutyang.modules.sys.service.SysMenuService;
import com.aboutyang.modules.sys.service.SysRoleMenuService;
import com.aboutyang.modules.sys.service.SysUserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.aboutyang.config.CacheConfig.MENU_CACHE;


@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuDao, SysMenuEntity> implements SysMenuService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList) {
        List<SysMenuEntity> menuList = queryListParentId(parentId);
        if (menuIdList == null) {
            return menuList;
        }

        List<SysMenuEntity> userMenuList = new ArrayList<>();
        for (SysMenuEntity menu : menuList) {
            if (menuIdList.contains(menu.getMenuId())) {
                userMenuList.add(menu);
            }
        }
        return userMenuList;
    }

    @Override
    public List<SysMenuEntity> queryListParentId(Long parentId) {
        return baseMapper.queryListParentId(parentId);
    }

    @Override
    public List<SysMenuEntity> queryNotButtonList() {
        return baseMapper.queryNotButtonList();
    }

    @Cacheable(value = MENU_CACHE)
    @Override
    public List<SysMenuEntity> queryAllMenu() {
        return baseMapper.selectList(null);
    }

    @Cacheable(value = MENU_CACHE, key = "#userId")
    @Override
    public List<SysMenuEntity> getUserMenuList(Long userId) {
        //系统管理员，拥有最高权限
        if (userId == Constant.SUPER_ADMIN) {
            return getAllMenuList(null);
        }

        //用户菜单列表
        List<Long> menuIdList = sysUserService.queryAllMenuId(userId);
        return getAllMenuList(menuIdList);
    }

    @CacheEvict(value = MENU_CACHE, allEntries = true)
    @Override
    public void delete(Long menuId) {
        //删除菜单
        this.deleteById(menuId);
        //删除菜单与角色关联
        sysRoleMenuService.deleteByMap(new MapUtils().put("menu_id", menuId));
    }

    /**
     * 获取所有菜单列表
     */
    private List<SysMenuEntity> getAllMenuList(List<Long> menuIdList) {
        //查询根菜单列表
        List<SysMenuEntity> menuList = queryListParentId(0L, menuIdList);
        //递归获取子菜单
        getMenuTreeList(menuList, menuIdList);

        return menuList;
    }

    /**
     * 递归
     */
    private List<SysMenuEntity> getMenuTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList) {
        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();

        for (SysMenuEntity entity : menuList) {
            //目录
            if (entity.getType() == Constant.MenuType.CATALOG.getValue()) {
                entity.setChildren(getMenuTreeList(queryListParentId(entity.getMenuId(), menuIdList), menuIdList));
            }
            subMenuList.add(entity);
        }

        return subMenuList;
    }

    @CacheEvict(value = MENU_CACHE, allEntries = true)
    @Override
    public void save(SysMenuEntity sysMenuEntity) {
        insert(sysMenuEntity);
    }

    @CacheEvict(value = MENU_CACHE, allEntries = true)
    @Override
    public void update(SysMenuEntity sysMenuEntity) {
        updateById(sysMenuEntity);
    }
}
