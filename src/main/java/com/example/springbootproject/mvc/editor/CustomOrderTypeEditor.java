package com.example.springbootproject.mvc.editor;

import com.example.springbootproject.domain.OrderType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;

@Slf4j
public class CustomOrderTypeEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {

        log.info("CustomOrderTypeEditor, received: {}", text);
        if (StringUtils.isBlank(text)) {
            setValue(null);
        } else {
            OrderType value;
            value = OrderType.fromString(text.trim());
            setValue(value);
        }
    }
}