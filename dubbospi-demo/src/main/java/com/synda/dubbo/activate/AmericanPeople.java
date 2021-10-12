package com.synda.dubbo.activate;

import org.apache.dubbo.common.extension.Activate;

/**
 * @author synda
 * @date 2021/10/4
 */
@Activate(group = {"English"}, order = 2)   //当且仅当 group 匹配才激活
public class AmericanPeople implements People {
    @Override
    public void sayHello() {
        System.out.println("Hello~");
    }
}
