package com.synda.dubbo.activate;

import org.apache.dubbo.common.extension.SPI;

/**
 * @author synda
 * @date 2021/10/4
 */
@SPI("Chinese")
public interface People {

    void sayHello();

}
