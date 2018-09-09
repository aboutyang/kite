package com.aboutyang.modules.sys.service.impl;

import com.aboutyang.common.annotation.DataFilter;
import com.aboutyang.common.utils.Constant;
import com.aboutyang.modules.sys.dao.SysDeptDao;
import com.aboutyang.modules.sys.entity.SysDeptEntity;
import com.aboutyang.modules.sys.service.SysDeptService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.aboutyang.config.CacheConfig.DEPT_CACHE;


@Service("sysDeptService")
public class SysDeptServiceImpl extends ServiceImpl<SysDeptDao, SysDeptEntity> implements SysDeptService {

    @Override
    @DataFilter(subDept = true, user = false)
    public List<SysDeptEntity> queryList(Map<String, Object> params) {
        List<SysDeptEntity> deptList =
                this.selectList(new EntityWrapper<SysDeptEntity>()
                        .addFilterIfNeed(params.get(Constant.SQL_FILTER) != null, (String) params.get(Constant.SQL_FILTER)));

        for (SysDeptEntity sysDeptEntity : deptList) {
            SysDeptEntity parentDeptEntity = this.selectById(sysDeptEntity.getParentId());
            if (parentDeptEntity != null) {
                sysDeptEntity.setParentName(parentDeptEntity.getName());
            }
        }
        return deptList;
    }

    @Cacheable(value = DEPT_CACHE, key = "#root.methodName")
    public List<SysDeptEntity> queryTree(Map<String, Object> params) {
        return buildDeptTree(queryList(params), 0L);
    }

    private List<SysDeptEntity> buildDeptTree(List<SysDeptEntity> deptList, Long parentId) {
        return deptList.stream().filter(dept -> dept.getParentId() == parentId).map(dept -> {
            List<SysDeptEntity> subDeptList = buildDeptTree(deptList, dept.getDeptId());
            if (subDeptList != null && subDeptList.size() > 0) {
                dept.setChildren(subDeptList);
            }
            return dept;
        }).sorted(Comparator.comparingInt(SysDeptEntity::getOrderNum)).collect(Collectors.toList());
    }

    @Override
    public List<Long> queryDetpIdList(Long parentId) {
        return baseMapper.queryDetpIdList(parentId);
    }

    @Override
    public List<Long> getSubDeptIdList(Long deptId) {
        //部门及子部门ID列表
        List<Long> deptIdList = new ArrayList<>();

        //获取子部门ID
        List<Long> subIdList = queryDetpIdList(deptId);
        getDeptTreeList(subIdList, deptIdList);

        return deptIdList;
    }

    /**
     * 递归
     */
    private void getDeptTreeList(List<Long> subIdList, List<Long> deptIdList) {
        for (Long deptId : subIdList) {
            List<Long> list = queryDetpIdList(deptId);
            if (list.size() > 0) {
                getDeptTreeList(list, deptIdList);
            }

            deptIdList.add(deptId);
        }
    }
}
