package com.aboutyang.modules.sys.service;

import com.aboutyang.common.utils.R;
import com.aboutyang.modules.sys.entity.SysUserTokenEntity;
import com.baomidou.mybatisplus.service.IService;

/**
 * 用户Token
 * 
 */
public interface SysUserTokenService extends IService<SysUserTokenEntity> {

	/**
	 * 生成token
	 * @param userId  用户ID
	 */
	R createToken(long userId);

	/**
	 * 退出，修改token值
	 * @param userId  用户ID
	 */
	void logout(long userId);

}
