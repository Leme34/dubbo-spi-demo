package com.synda.service.impl;

import com.synda.service.TestService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lsd
 * 2021-09-28 00:17
 */
@DubboService
public class TestServiceImpl implements TestService {

    private static final Logger log = LoggerFactory.getLogger(TestServiceImpl.class);

    public String invoke() {
        log.info("com.synda.service.impl.TestServiceImpl.invoke...");
        return "success";
    }
}
