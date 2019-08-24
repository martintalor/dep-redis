package com.iflytek.dep.common.utils;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * @author zrdang
 * @version V1.0
 * @Package com.iflytek.dep.server.service.scheduled
 * @Description: 获取配置属性工具
 * @date 2019/2/27--19:20
 */
@Component
public class PropertiesUtils implements EmbeddedValueResolverAware {
    private  StringValueResolver valueResolver;

    public  String getPropertiesValue(String key) {
        return valueResolver.resolveStringValue("${" + key + "}");
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        this.valueResolver = stringValueResolver;
    }
}
