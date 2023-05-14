package com.example.springbootproject.mvc.editor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;

@Slf4j
public class CustomIntegerEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {

        log.info("CustomIntegerEditor, received: {}", text);
        if (StringUtils.isBlank(text)) {
            setValue(null);
        } else {
            int value;
            try {
                value = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                value = 0;
            }
            setValue(value);
        }
    }
}