package com.synda.service.impl;

import com.synda.service.TestService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lsd
 * 2021-09-28 00:17
 */
// 当且仅当触发了带有该parameters的Service后dubbo-admin才能拉取到metrics信息！！！
// 配合当前仓库的apache-dubbo-admin-0.3.0-bin-release-fixedBySynda.zip使用
@DubboService(parameters = {"metrics.port","20880","metrics.protocol","dubbo"})
public class TestServiceImpl implements TestService {

    private static final Logger log = LoggerFactory.getLogger(TestServiceImpl.class);

    public String invoke() {
        log.info("com.synda.service.impl.TestServiceImpl.invoke...");
        return "success";
    }
}
