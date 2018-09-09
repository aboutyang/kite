package com.aboutyang.modules.sys.service.impl;

import com.aboutyang.common.utils.Constant;
import com.aboutyang.modules.sys.dao.SysMenuDao;
import com.aboutyang.modules.sys.dao.SysUserDao;
import com.aboutyang.modules.sys.dao.SysUserTokenDao;
import com.aboutyang.modules.sys.entity.SysMenuEntity;
import com.aboutyang.modules.sys.entity.SysUserEntity;
import com.aboutyang.modules.sys.entity.SysUserTokenEntity;
import com.aboutyang.modules.sys.service.ShiroService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.aboutyang.config.CacheConfig.PERM_CACHE;
import static com.aboutyang.config.CacheConfig.TOKEN_CACHE;
import static com.aboutyang.config.CacheConfig.USER_CACHE;

@Service
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private SysMenuDao sysMenuDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysUserTokenDao sysUserTokenDao;

    @Cacheable(value = PERM_CACHE, key = "#userId")
    @Override
    public Set<String> getUserPermissions(long userId) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if(userId == Constant.SUPER_ADMIN){
            List<SysMenuEntity> menuList = sysMenuDao.selectList(null);
            permsList = new ArrayList<>(menuList.size());
            for(SysMenuEntity menu : menuList){
                permsList.add(menu.getPerms());
            }
        }else{
            permsList = sysUserDao.queryAllPerms(userId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for(String perms : permsList){
            if(StringUtils.isBlank(perms)){
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }

    @Cacheable(value = TOKEN_CACHE, key = "#token")
    @Override
    public SysUserTokenEntity queryByToken(String token) {
        return sysUserTokenDao.queryByToken(token);
    }

    @CacheEvict(value = TOKEN_CACHE, key = "#userToken.token")
    @Override
    public void refreshToken(SysUserTokenEntity userToken) {
        sysUserTokenDao.updateAllColumnById(userToken);
    }

    @Override
    public void saveUpdateToken(SysUserTokenEntity userToken) {
        sysUserTokenDao.insert(userToken);
    }

    @Cacheable(value = USER_CACHE, key = "#userId")
    @Override
    public SysUserEntity queryUser(Long userId) {
        return sysUserDao.selectById(userId);
    }
}
