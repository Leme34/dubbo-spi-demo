package com.synda.javaspi;

import java.util.ServiceLoader;

/**
 * Created by lsd
 * 2021-09-27 23:39
 */
public class JavaSpiTest {

    public static void main(String[] args) {
        // 通过 Java 的 SPI 机制加载在 META-INF/services/{接口权限定名} 中声明的接口实现类集合
        // 从而实现基于接口开发（通过配置实现可插拔的具体实现类替换）
        ServiceLoader<People> serviceLoader = ServiceLoader.load(People.class);
        for (People people : serviceLoader) {
            people.sayHello();
        }
    }

}
