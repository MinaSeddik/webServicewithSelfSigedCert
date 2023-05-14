package com.example.springbootproject.mvc.editor;

import com.example.springbootproject.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class CustomLocalDateEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {

        log.info("CustomLocalDateEditor, received: {}", text);
        if (StringUtils.isBlank(text)) {
            setValue(null);
        } else {
            LocalDate value;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                value = LocalDate.parse(text.trim(), formatter);
            } catch (DateTimeParseException e) {
                throw new MyException("Invalid date format");
//                value = null;
            }
            setValue(value);
        }
    }
}