package com.aboutyang.modules.sys.service.impl;

import com.aboutyang.common.utils.PageUtils;
import com.aboutyang.common.utils.Query;
import com.aboutyang.modules.sys.dao.SysLogDao;
import com.aboutyang.modules.sys.entity.SysLogEntity;
import com.aboutyang.modules.sys.service.SysLogService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("sysLogService")
public class SysLogServiceImpl extends ServiceImpl<SysLogDao, SysLogEntity> implements SysLogService {

    private static final Logger log = LoggerFactory.getLogger(SysLogServiceImpl.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String)params.get("key");

        Page<SysLogEntity> page = this.selectPage(
            new Query<SysLogEntity>(params).getPage(),
            new EntityWrapper<SysLogEntity>().like(StringUtils.isNotBlank(key),"username", key)
        );

        return new PageUtils(page);
    }

    @Async
    public void asyncInsert(SysLogEntity sysLogEntity){
        try{
            Integer count = this.baseMapper.insert(sysLogEntity);
            if(count == 0){
                log.error("insert log error: {}", objectMapper.writeValueAsString(sysLogEntity));
            }
        }catch (Exception e){
            log.error("sync save log error", e);
        }
    }

}
