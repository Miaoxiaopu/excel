package com.fileinfo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fileinfo.entity.FileInfo;
import com.fileinfo.mapper.IFileInfoMapper;
import com.fileinfo.service.IFileInfoService;
import org.springframework.stereotype.Service;

@Service
public class FileInfoServiceImpl extends ServiceImpl<IFileInfoMapper, FileInfo> implements IFileInfoService {
    //
}
