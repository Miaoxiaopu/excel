package com.fileinfo.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fileinfo.entity.GovUserEntity;
import com.fileinfo.service.IGovUserService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {
    public static GovUserEntity getUser(String username){
        QueryWrapper<GovUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        IGovUserService govUserService = ApplicationContontextUtils.getBean(IGovUserService.class);
        return govUserService.getOne(queryWrapper);
    }
}
