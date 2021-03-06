package com.synda.dubbo.adaptive_method;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * Adaptive机制 能根据 URL 的传参决定使用哪一个具体 SPI 扩展实现
 *
 * @author synda
 * @date 2021/10/4
 */
public class DubboSpiAdaptiveMethodTest {

    public static void main(String[] args) {
        ExtensionLoader<People> extensionLoader = ExtensionLoader.getExtensionLoader(People.class);
        //adaptive method
        People adaptiveExtension = extensionLoader.getAdaptiveExtension();
        //模拟一个URL
        adaptiveExtension.sayHello(URL.valueOf("xxx://localhost/test?people=American"));
    }

}
