package com.example.springbootproject.controller;

import com.example.springbootproject.domain.ConditionMessageRepresentation;
import com.example.springbootproject.domain.MulticastMessageRepresentation;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class FirebasePublisherController {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    @PostMapping("/topics/{topic}")
    public ResponseEntity<String> postToTopic(@RequestBody String message,
                                              @PathVariable("topic") String topic) throws FirebaseMessagingException {

        Message msg = Message.builder()
                .setTopic(topic)
                .putData("body", message)
                .build();

        String id = firebaseMessaging.send(msg);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(id);
    }

    @PostMapping("/condition")
    public ResponseEntity<String> postToCondition(@RequestBody ConditionMessageRepresentation message ) throws FirebaseMessagingException {

        Message msg = Message.builder()
                .setCondition(message.getCondition())
                .putData("body", message.getData())
                .build();

        String id = firebaseMessaging.send(msg);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(id);
    }


    @PostMapping("/clients/{registrationToken}")
    public ResponseEntity<String> postToClient(@RequestBody String message,
                                               @PathVariable("registrationToken") String registrationToken) throws FirebaseMessagingException {

        Message msg = Message.builder()
                .setToken(registrationToken)
                .putData("body", message)
                .build();

        String id = firebaseMessaging.send(msg);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(id);
    }

    @PostMapping("/clients")
    public ResponseEntity<List<String>> postToClients(@RequestBody MulticastMessageRepresentation message) throws FirebaseMessagingException {

        MulticastMessage msg = MulticastMessage.builder()
                .addAllTokens(message.getRegistrationTokens())
                .putData("body", message.getData())
                .build();

        BatchResponse response = firebaseMessaging.sendMulticast(msg);

        List<String> ids = response.getResponses()
                .stream()
                .map(r->r.getMessageId())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(ids);
    }

    @PostMapping("/subscriptions/{topic}")
    public ResponseEntity<Void> createSubscription(@PathVariable("topic") String topic, @RequestBody List<String> registrationTokens) throws FirebaseMessagingException {
        firebaseMessaging.subscribeToTopic(registrationTokens, topic);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/subscriptions/{topic}/{registrationToken}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable String topic, @PathVariable String registrationToken) throws FirebaseMessagingException {
        firebaseMessaging.subscribeToTopic(Arrays.asList(registrationToken), topic);
        return ResponseEntity.ok().build();
    }
}

