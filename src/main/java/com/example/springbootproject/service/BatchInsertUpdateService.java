package com.example.springbootproject.service;


import com.example.springbootproject.domain.User2;
import com.example.springbootproject.repository.batchinsertupdate.BatchInsertRepository;
import com.example.springbootproject.repository.batchinsertupdate.BatchUpdateRepository;
import com.example.springbootproject.repository.impl.User2Repository;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class BatchInsertUpdateService {

    //    Reference: https://medium.com/@nairgirish100/using-java-faker-for-test-automation-b9a99a7fd3fb
    private Faker faker = new Faker(); // Very expensive object

    @Autowired
    private BatchInsertRepository batchInsertRepository;

    @Autowired
    private BatchUpdateRepository batchUpdateRepository;

    @Autowired
    private User2Repository user2Repository;

    public void insertBatch(int count) throws ExecutionException, InterruptedException {

        log.info("insertBatch called");

        // generate row count
        log.info("Generating {} records START", count);
        List<User2> list = IntStream.range(0, count).
                mapToObj(i -> buildUser())
                .collect(Collectors.toList());
        log.info("Generating {} records DONE", count);

        log.info("START BATCH INSERT SYN ...");
        batchInsertRepository.batchInsert2(list);
        log.info("END BATCH INSERT SYN ...");

        log.info("START BATCH INSERT A-SYN ...");
        batchInsertRepository.batchInsertAsync(list);
        log.info("END BATCH INSERT A-SYN ...");
    }

    public void insertBatch2(int count) {

        log.info("insertBatch called");
        Faker faker = new Faker();
        User2 user2 = new User2();

        String username = faker.name().username();
        log.info("insertBatch username = " + username);
        user2.setUsername(username);

        String password = faker.crypto().sha256();
        log.info("insertBatch password = " + password);
        user2.setPassword(password);

        String firstName = faker.name().firstName();
        log.info("insertBatch firstName = " + firstName);
        user2.setFirstName(firstName);

        String lastName = faker.name().lastName();
        log.info("insertBatch lastName = " + lastName);
        user2.setLastName(lastName);

        String email = faker.internet().safeEmailAddress();
        log.info("insertBatch email = " + email);
        user2.setEmail(email);

        String phoneNumber = faker.phoneNumber().phoneNumber();
        log.info("insertBatch phoneNumber = " + phoneNumber);
        user2.setPhoneNumber(phoneNumber);

        boolean bool = faker.bool().bool();
        log.info("insertBatch bool = " + bool);

        user2.setLocked(bool);
        user2.setActive(bool);


        java.util.Date birthday = faker.date().birthday();
        log.info("insertBatch username = " + birthday);
        user2.setDateOfBirth(new Date(birthday.getTime()));

        batchInsertRepository.batchInsert2(Arrays.asList(user2));
    }

    private User2 buildUser() {
        User2 user2 = new User2();

        user2.setUsername(faker.name().username());
        user2.setPassword(faker.crypto().sha256());
        user2.setFirstName(faker.name().firstName());
        user2.setLastName(faker.name().lastName());
        user2.setEmail(faker.internet().safeEmailAddress());
        user2.setPhoneNumber(faker.phoneNumber().phoneNumber());
        user2.setLocked(faker.bool().bool());
        user2.setActive(faker.bool().bool());
        user2.setDateOfBirth(new Date(faker.date().birthday().getTime()));

        return user2;
    }

    public void updateBatch(int count) throws ExecutionException, InterruptedException {

        log.info("insertBatch called");

        // generate row count
        log.info("Generating {} records START", count);
        List<User2> list = IntStream.range(0, count).
                mapToObj(i -> buildUser())
                .collect(Collectors.toList());
        log.info("Generating {} records DONE", count);


        log.info("START BATCH INSERT A-SYN ...");
        batchInsertRepository.batchInsertAsync(list);
        log.info("END BATCH INSERT A-SYN ...");

        log.info("===============================");

        log.info("START RETRIEVE USERS ...");
        List<User2> allUsers = user2Repository.getAllUsers();
        log.info("END RETRIEVE USERS ...");

        log.info("===============================");

        log.info("START CHANGE SOME USERS ...");
        // change 20% of the records
        int[] indexes = new Random().ints((long) (count * 0.2), 0, count).toArray();
//        int[] indexes =  new Random().ints(0,count).limit((long) (count * 0.2)).toArray();
        Arrays.stream(indexes).forEach(i -> updateUser(allUsers.get(i)));
        log.info("END CHANGE SOME USERS ...");

        log.info("===============================");

        log.info("START BATCH UPDATE A-SYN ...");
        batchUpdateRepository.batchUpdateAsync(allUsers);
        log.info("END BATCH UPDATE A-SYN ...");


    }

    private void updateUser(User2 user2) {

        user2.setUsername(faker.name().username());
        user2.setPassword(faker.crypto().sha256());
        user2.setFirstName(faker.name().firstName());
        user2.setLastName(faker.name().lastName());
        user2.setEmail(faker.internet().safeEmailAddress());
        user2.setPhoneNumber(faker.phoneNumber().phoneNumber());
        user2.setLocked(faker.bool().bool());
        user2.setActive(faker.bool().bool());
        user2.setDateOfBirth(new Date(faker.date().birthday().getTime()));

    }

}
