package com.fileinfo.type;

public enum MessageEnumType {
    SUCCESS("200","操作成功"),
    FAIL("-1","操作失败"),
    FILE_EXIST("200","文件已存在,请勿重复上传");
    private String code;
    private String message;
    MessageEnumType(String code, String message){
        this.code = code;
        this.message = message;
    }
    public String getMessage(){
        return this.code;
    }
}
