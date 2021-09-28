package com.synda.controller;

import com.synda.service.TestService;
import com.synda.traceid.LogTraceWebFilter;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lsd
 * 2021-09-28 02:03
 */
@RestController
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @DubboReference
    private TestService testService;

    @RequestMapping("test1")
    public String test1() {
        log.info("com.synda.controller.TestController.test1() start");
        String result = testService.invoke();
        log.info("com.synda.controller.TestController.test1() return: {}",result);
        return result;
    }

}
