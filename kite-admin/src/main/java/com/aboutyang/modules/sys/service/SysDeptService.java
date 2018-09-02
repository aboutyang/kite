package com.aboutyang.modules.sys.service;


import com.aboutyang.modules.sys.entity.SysDeptEntity;
import com.baomidou.mybatisplus.service.IService;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 部门管理
 *
 */
public interface SysDeptService extends IService<SysDeptEntity> {

	List<SysDeptEntity> queryList(Map<String, Object> map);

	public List<SysDeptEntity> queryTree(Map<String, Object> params);

	/**
	 * 查询子部门ID列表
	 * @param parentId  上级部门ID
	 */
	List<Long> queryDetpIdList(Long parentId);

	/**
	 * 获取子部门ID，用于数据过滤
	 */
	List<Long> getSubDeptIdList(Long deptId);

}
