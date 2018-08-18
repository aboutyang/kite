package com.jd.cis.modules.sys.service;


import com.baomidou.mybatisplus.service.IService;
import com.jd.cis.common.utils.PageUtils;
import com.jd.cis.modules.sys.entity.SysLogEntity;

import java.util.Map;


/**
 * 系统日志
 *
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

}
