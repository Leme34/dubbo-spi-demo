package com.synda.dubbo;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.ExtensionLoader;

/**
 * 根据 nation 属性自适应扩展
 *
 * @author synda
 * @date 2021/10/10
 */
@Adaptive
public class AdaptivePeople implements People {

    private String nation;

    public void setNation(String nation) {
        this.nation = nation;
    }

    @Override
    public void sayHello() {
        ExtensionLoader<People> extensionLoader = ExtensionLoader.getExtensionLoader(People.class);
        if (StringUtils.isBlank(nation)) {
            extensionLoader.getDefaultExtension().sayHello();
            return;
        }
        extensionLoader.getExtension(nation).sayHello();
    }


}
