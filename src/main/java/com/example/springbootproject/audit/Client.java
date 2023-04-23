package com.example.springbootproject.audit;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Client {

    private String ip;

    private String browser;

    private String referer;


}
