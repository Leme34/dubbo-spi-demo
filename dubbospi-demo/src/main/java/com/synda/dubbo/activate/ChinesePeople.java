package com.synda.dubbo.activate;

import org.apache.dubbo.common.extension.Activate;

/**
 * @author synda
 * @date 2021/10/4
 */
@Activate(group = {"Chinese", "English"}, value = {"default"}, order = 1)  //当且仅当 group 匹配而且 url 中有传 default 参数的时候才激活
public class ChinesePeople implements People {
    @Override
    public void sayHello() {
        System.out.println("你好~");
    }
}
