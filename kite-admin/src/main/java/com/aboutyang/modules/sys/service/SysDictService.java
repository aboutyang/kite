package com.aboutyang.modules.sys.service;

import com.aboutyang.common.utils.PageUtils;
import com.aboutyang.modules.sys.entity.SysDictEntity;
import com.baomidou.mybatisplus.service.IService;

import java.util.Map;

/**
 * 数据字典
 *
 */
public interface SysDictService extends IService<SysDictEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

