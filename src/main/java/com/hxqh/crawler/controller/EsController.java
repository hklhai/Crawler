package com.hxqh.crawler.controller;

import com.hxqh.crawler.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Ocean lin on 2018/1/31.
 *
 * @author Ocean lin
 */
@Controller
@RequestMapping("/es")
public class EsController {
    @Autowired
    private SystemService systemService;

    @RequestMapping("/export")
    public String export() {
        systemService.export();
        return "crawler/notice";
    }

    @RequestMapping("/migrate")
    public String migrate() {
        systemService.migrate();
        return "crawler/notice";
    }
}
