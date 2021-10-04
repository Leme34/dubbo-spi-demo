package com.synda.dubbo;

import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * @author synda
 * @date 2021/10/4
 */
public class DubboSpiTest {

    public static void main(String[] args) {
        ExtensionLoader<People> extensionLoader = ExtensionLoader.getExtensionLoader(People.class);
        People people = extensionLoader.getDefaultExtension();
        people.sayHello();
        People American = extensionLoader.getExtension("American");
        American.sayHello();
    }

}
