package com.synda.dubbo;

import org.apache.dubbo.common.extension.SPI;

/**
 * @author synda
 * @date 2021/10/4
 */
@SPI("Chinese")
public interface People {

    void sayHello();

}
