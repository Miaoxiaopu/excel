package com.fileinfo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("USER_INFO")
public class UserInfo {
    @TableId(type = IdType.ASSIGN_UUID)
    private String userId;
    private String userName;
    private String password;
    private String salt;

}
