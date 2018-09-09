package com.aboutyang.modules.sys.service;

import com.aboutyang.common.annotation.DataFilter;
import com.aboutyang.common.utils.PageUtils;
import com.aboutyang.common.utils.R;
import com.aboutyang.modules.sys.entity.SysUserEntity;
import com.aboutyang.modules.sys.entity.SysUserTokenEntity;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 用户Token
 * 
 */
public interface SysUserTokenService extends IService<SysUserTokenEntity> {

	/**
	 * 生成token
	 * @param user  用户
	 */
	R createToken(SysUserEntity user);

	/**
	 * 退出，修改token值
	 * @param userId  用户ID
	 */
	void logout(long userId);

	PageUtils queryPage(Map<String, Object> params);

	void forceLogout(String token);

}
