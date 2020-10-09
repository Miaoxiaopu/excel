package com.fileinfo.type;

public enum MessageEnumType {
    SUCCESS("200","操作成功"),
    FAIL("-1","操作失败"),
    FILE_EXIST("200","文件已存在,请勿重复上传"),
    SEARCH_CONDITION_NULL("1","查询条件不能为空!"),
    NOT_SUPPORT_UTF8("2","要查找的文件内容编码不支持转换为utf-8编码!");
    private String code;
    private String message;
    MessageEnumType(String code, String message){
        this.code = code;
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
}
