package com.edara.edara.global.utils;


import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class NonNullBeanUtils extends BeanUtilsBean {

    @SneakyThrows
    public void copyProperties(Object source, Object target, String... ignoreProperties) {
        Set<String> ignoreSet = new HashSet<>(Arrays.asList(ignoreProperties));

        for (var descriptor : getPropertyUtils().getPropertyDescriptors(source)) {
            String name = descriptor.getName();
            if (!ignoreSet.contains(name) && getPropertyUtils().isReadable(source, name) && getPropertyUtils().isWriteable(target, name)) {
                Object value = getPropertyUtils().getSimpleProperty(source, name);
                if (value != null) {
                    super.copyProperty(target, name, value);
                }
            }
        }
    }
}