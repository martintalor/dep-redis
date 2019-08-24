package com.iflytek.dep.common.utils;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

/**
 * 重写toString方法，通过反射打印出该方法内部属性
 */
public class ToStringModel implements Serializable {
    private static final long serialVersionUID = -6351210629803310701L;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }
}