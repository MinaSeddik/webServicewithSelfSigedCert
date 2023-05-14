package com.example.springbootproject.mvc.converter;

import com.example.springbootproject.domain.BookStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class StringToBookStatusEnumConverter implements Converter<String, BookStatus> {

    @Override
    public BookStatus convert(String value) {
        log.info("inside StringToBookStatusEnumConverter, received input value: {}", value);
        BookStatus bookStatus = null;
        try {
            bookStatus = BookStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            bookStatus = null;
        }

        log.info("bookStatus: {}", bookStatus);

        return bookStatus;
    }
}