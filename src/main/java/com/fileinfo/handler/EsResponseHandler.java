package com.fileinfo.handler;


public interface EsResponseHandler<T>{
    /**
     * 处理es返回的结果
     * @param
     * @return
     */
    T handlerRes(T t);
}
