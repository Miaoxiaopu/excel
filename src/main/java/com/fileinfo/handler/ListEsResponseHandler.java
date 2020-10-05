package com.fileinfo.handler;

import java.util.HashMap;

public class ListEsResponseHandler implements EsResponseHandler<HashMap>{
    private static final String FILEINFO = "fileInfo";

    @Override
    public HashMap handlerRes(HashMap hashMap) {
        String fileInfo = (String)hashMap.get(FILEINFO);
        fileInfo = fileInfo.substring(0, fileInfo.indexOf("_"));
        HashMap map = new HashMap();
        map.put(FILEINFO,fileInfo);
        return map;
    }
}
