package com.example.springbootproject.controller;


import com.example.springbootproject.service.MyEmailService;
import com.example.springbootproject.service.MyService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Timed
public class MyRestController {

    public static int x = 0;

    @Autowired
    public MyService myService;

    @Autowired
    public MyEmailService myEmailService;

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


    @RequestMapping(value = "/data3")
    public void getData3() {

        x++;

        myService.doThis();
        myService.doThat();

        return;
    }

    @RequestMapping(value = "/email")
    public void sendEmail() {

        String to = "test@example.com";
        String subject = "test from spring-boot app";
        String message = "this is a simple text message.";
        String htmlMessage = "This is my <b style='color:red;'>bold-red email</b> using JavaMailer";
        String attachFilePath = "/home/mina/Desktop/FINRA Ten-print Submission.txt";


//        myEmailService.sendSimpleTextMessage(to, subject, message); //pass
//        myEmailService.sendSimpleHtmlMessage(to, subject, htmlMessage); //pass
//        myEmailService.sendMessageWithAttachment(to, subject, message, attachFilePath); //pass
        myEmailService.sendSignedAndEncryptedMessage(to, subject, message, attachFilePath);


    }


}
