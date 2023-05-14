package com.example.springbootproject.mvc.editor;

import com.example.springbootproject.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class CustomLocalDateTimeEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {

        log.info("CustomLocalDateTimeEditor, received: {}", text);
        if (StringUtils.isBlank(text)) {
            setValue(null);
        } else {
            LocalDateTime value;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                value = LocalDateTime.parse(text.trim(), formatter);
            } catch (DateTimeParseException e) {

                // Either way to throw exception for invalid value
                // or set it to null (or a default value)
                // according to the business case

//                throw new MyException("Invalid date format");
//                value = null;
                value = LocalDateTime.now(); // default value
            }
            setValue(value);
        }
    }
}