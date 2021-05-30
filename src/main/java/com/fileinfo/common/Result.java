package com.fileinfo.common;

import lombok.Data;

@Data
public class Result {
    private String code;
    private String msg;
    private Object data;


    public Result(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result ok() {
        return new Result("200", "操作成功", null);
    }

    public static Result ok(Object data) {
        return new Result("200", "操作成功", data);
    }

    public static Result fail() {
        return new Result("-1", "操作失败", null);
    }

    public static Result fail(String msg) {
        return new Result("-1", msg, null);
    }
}
