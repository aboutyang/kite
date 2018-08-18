package com.jd.cis.modules.sys.service;

import com.baomidou.mybatisplus.service.IService;
import com.jd.cis.common.utils.PageUtils;
import com.jd.cis.modules.sys.entity.SysDictEntity;

import java.util.Map;

/**
 * 数据字典
 *
 */
public interface SysDictService extends IService<SysDictEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

