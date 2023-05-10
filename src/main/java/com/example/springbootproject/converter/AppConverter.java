package com.example.springbootproject.converter;

import org.modelmapper.ModelMapper;

public abstract class AppConverter {

    public AppConverter(ModelMapper modelMapper) {
        configure(modelMapper);
    }

    abstract void configure(ModelMapper modelMapper);

}
