package com.edara.edara.global;

import org.springframework.stereotype.Service;

@Service
public interface ServiceLocator {
    <T> T getService(Class<T> serviceClass);
}
