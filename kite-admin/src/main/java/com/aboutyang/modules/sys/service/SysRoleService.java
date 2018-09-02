package com.aboutyang.modules.sys.service;


import com.aboutyang.common.utils.PageUtils;
import com.aboutyang.modules.sys.entity.SysRoleEntity;
import com.baomidou.mybatisplus.service.IService;

import java.util.Map;


/**
 * 角色
 *
 */
public interface SysRoleService extends IService<SysRoleEntity> {

	PageUtils queryPage(Map<String, Object> params);

	void save(SysRoleEntity role);

	void update(SysRoleEntity role);
	
	void deleteBatch(Long[] roleIds);

}
