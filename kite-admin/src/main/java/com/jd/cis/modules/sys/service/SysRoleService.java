package com.jd.cis.modules.sys.service;


import com.baomidou.mybatisplus.service.IService;
import com.jd.cis.common.utils.PageUtils;
import com.jd.cis.modules.sys.entity.SysRoleEntity;

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
