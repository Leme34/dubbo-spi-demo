package com.synda.dubbo.adaptive_method;

import org.apache.dubbo.common.URL;

/**
 * @author synda
 * @date 2021/10/4
 */
public class ChinesePeople implements People {
    @Override
    public void sayHello(URL url) {
        System.out.println("你好~");
    }
}
