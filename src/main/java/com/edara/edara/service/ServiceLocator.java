package com.edara.edara.service;

import org.springframework.stereotype.Service;

@Service
public interface ServiceLocator {
    <T> T getService(Class<T> serviceClass);
}
