package com.synda.dubbo.adaptive_method;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

/**
 * @author synda
 * @date 2021/10/4
 */
@SPI("Chinese")
public interface People {

    @Adaptive
    void sayHello(URL url);  //adaptive方法规范要求必须有 Duubo 的 URL 参数

}
