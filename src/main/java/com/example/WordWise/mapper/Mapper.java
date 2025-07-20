package com.example.WordWise.mapper;

import org.springframework.stereotype.Component;

@Component
public interface Mapper {
    public Object map(Object source, Object target);
}
