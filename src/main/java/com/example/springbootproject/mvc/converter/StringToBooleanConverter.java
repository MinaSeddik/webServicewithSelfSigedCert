package com.example.springbootproject.mvc.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToBooleanConverter implements Converter<String, Boolean> {

    @Override
    public Boolean convert(String value) {

        log.info("inside StringToBooleanConverter, received input value: {}", value);
        return value.trim().equalsIgnoreCase("true") || value.trim().equals("1") ?
                Boolean.TRUE : Boolean.FALSE;
    }
}