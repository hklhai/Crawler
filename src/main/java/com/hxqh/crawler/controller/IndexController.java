package com.hxqh.crawler.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author Ocean Lin
 *         Created by Ocean lin on 2017/7/1.
 */
@Controller
public class IndexController {


    @RequestMapping("/")
    public String index() {

        return "index/index";
    }



}


