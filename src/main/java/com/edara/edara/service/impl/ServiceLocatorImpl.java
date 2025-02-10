package com.edara.edara.service.impl;

import com.edara.edara.service.ServiceLocator;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ServiceLocatorImpl implements ServiceLocator {
    private final ApplicationContext context;

    @Override
    public <T> T getService(Class<T> serviceClass) {
        return context.getBean(serviceClass);
    }
}
