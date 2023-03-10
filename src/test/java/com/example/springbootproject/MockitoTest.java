package com.example.springbootproject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class MockitoTest {


    @Mock
    List<String> list;


    @Test
    public void test(){

        Mockito.when(list.add(anyString())).thenReturn(true);

        list.add("something");

        Mockito.verify(list, times(1)).add(any(String.class));
    }

    @Test
    public void test2(){

        Mockito.when(list.add(anyString())).thenReturn(true);

        list.add("something");

        Mockito.verify(list, times(1)).add(any(String.class));
    }

}
