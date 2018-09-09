package com.aboutyang.modules.sys.service;


import com.aboutyang.common.utils.PageUtils;
import com.aboutyang.modules.sys.entity.SysLogEntity;
import com.baomidou.mybatisplus.service.IService;

import java.util.Map;


/**
 * 系统日志
 *
 */
public interface SysLogService extends IService<SysLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void asyncInsert(SysLogEntity sysLogEntity);

}
