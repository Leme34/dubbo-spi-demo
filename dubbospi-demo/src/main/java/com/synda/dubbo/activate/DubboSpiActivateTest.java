package com.synda.dubbo.activate;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;

import java.util.List;

/**
 * Adaptive机制 能根据 URL 的传参决定使用哪一个具体 SPI 扩展实现
 *
 * @author synda
 * @date 2021/10/4
 */
public class DubboSpiActivateTest {

    public static void main(String[] args) {
        ExtensionLoader<People> extensionLoader = ExtensionLoader.getExtensionLoader(People.class);

        // 虽然 ChinesePeople 匹配到了 group，但是 url 中没有传 default 参数，所以 只激活了 AmericanPeople 而 ChinesePeople 不激活
        //List<People> activateExtension2 = extensionLoader.getActivateExtension(URL.valueOf("xxx://localhost/test"), new String[]{}, "English");
        //activateExtension2.forEach(People::sayHello);

        // 不要求 default 参数参与激活规则，所以 ChinesePeople 和 AmericanPeople 都激活了
        //List<People> activateExtension3 = extensionLoader.getActivateExtension(URL.valueOf("xxx://localhost/test?default=true"), new String[]{}, "English");
        //activateExtension3.forEach(People::sayHello);

        // 要求 default 参数参与激活规则后，因为 AmericanPeople 没有在 @Activate 中指定 value="default" ，所以只激活了 ChinesePeople 而 AmericanPeople 不激活
        List<People> activateExtension4 = extensionLoader.getActivateExtension(URL.valueOf("xxx://localhost/test?default=true"), new String[]{"default"}, "Chinese");
        activateExtension4.forEach(People::sayHello);
    }

}
