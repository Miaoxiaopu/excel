package com.fileinfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fileinfo.entity.GovUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface IGovUserMapper extends BaseMapper<GovUserEntity> {
}
