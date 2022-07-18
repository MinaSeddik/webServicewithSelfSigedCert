package com.example.webServicewithSelfSigedCert;


import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRestController {

    public static int x = 0;

//    @Counted(value = "my-app.getData2.counted")
    @Timed(value = "my-app.getData.timed")
    @RequestMapping(value = "/data")
    public String getData() {

        x++;
        if (x%3==0){
           throw new RuntimeException("Invalid Parameter");
        }
        return "sample data";
    }


}
