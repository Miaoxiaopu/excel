package com.fileinfo.controller;

import com.fileinfo.utils.BaseElasticUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class EsController {

    private BaseElasticUtils baseElasticUtils;

    @Autowired
    public EsController(BaseElasticUtils baseElasticUtils) {
        this.baseElasticUtils = baseElasticUtils;
    }

    @RequestMapping("/createEsIndex")
    public void createEsIndex(@RequestParam("idxName") String idxName) {
        baseElasticUtils.createIndex(idxName, "");
    }

}
