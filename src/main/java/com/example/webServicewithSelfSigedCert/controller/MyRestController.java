package com.example.webServicewithSelfSigedCert.controller;


import com.example.webServicewithSelfSigedCert.service.MyService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRestController {

    public static int x = 0;

    @Autowired
    public MyService myService;

    @Counted(value = "my-app.getValue.counted")
    @RequestMapping(value = "/value")
    public String getValue() {

        x++;
        if (x%3==0){
            throw new RuntimeException("Invalid Parameter");
        }
        return "sample data";
    }

    @Timed(value = "my-app.getData.timed")
    @RequestMapping(value = "/data")
    public String getData() {

        x++;

        myService.doThis();
        myService.doThat();


        if (x%3==0){
           throw new RuntimeException("Invalid Parameter");
        }
        return "sample data";
    }

    @Timed(value = "my-app.getData2.timed",
            histogram = true,
            extraTags = {"my_var", "my_val"},
            percentiles = {0.95, 0.99})
    @RequestMapping(value = "/data2")
    public String getData2() {
        return "sample data";
    }
}
