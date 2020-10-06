package com.fileinfo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fileinfo.utils.DatePattern;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class BaseEntity {
    @TableField(value = "create_time")
    private Timestamp createTime;
    @TableField(value = "update_time")
    private Timestamp updateTime;
    public String getCreateTime(){
       return DatePattern.YYYY_MM_DD_HH_MM_SS.format(createTime);
    }
    public String getUpdateTime(){
        return DatePattern.YYYY_MM_DD_HH_MM_SS.format(updateTime);
    }
}
