package com.fileinfo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fileinfo.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface IFileInfoMapper extends BaseMapper<FileInfo> {
    //
}
