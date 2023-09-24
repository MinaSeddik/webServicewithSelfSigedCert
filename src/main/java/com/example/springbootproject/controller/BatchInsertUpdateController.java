package com.example.springbootproject.controller;

import com.example.springbootproject.service.BatchInsertUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class BatchInsertUpdateController {

    @Autowired
    private BatchInsertUpdateService batchInsertUpdateService;

    @GetMapping("/batch")
    public String batchInsertTest() throws ExecutionException, InterruptedException {
        log.info("batchInsertTest ... ");

        batchInsertUpdateService.insertBatch(200000);
//        batchInsertUpdateService.insertBatch(500000);
//        batchInsertUpdateService.insertBatch(800000);
//        batchInsertUpdateService.insertBatch(1000000);
//        batchInsertUpdateService.insertBatch(1200000);
//        batchInsertUpdateService.insertBatch(1500000);
//        batchInsertUpdateService.insertBatch(2000000);
//        batchInsertUpdateService.insertBatch(3000000); // error OutOfMemoryError
//        batchInsertUpdateService.insertBatch(5000000);  // error OutOfMemoryError

        return "OK";
    }

    @GetMapping("/batch-update")
    public String batchUpdateTest() throws ExecutionException, InterruptedException {
        log.info("batchUpdateTest ... ");

//        batchInsertUpdateService.updateBatch(200000);
//        batchInsertUpdateService.updateBatch(500000);
//        batchInsertUpdateService.updateBatch(800000);
        batchInsertUpdateService.updateBatch(1000000);
//        batchInsertUpdateService.updateBatch(1200000);
//        batchInsertUpdateService.updateBatch(1500000);
//        batchInsertUpdateService.updateBatch(2000000);
//        batchInsertUpdateService.updateBatch(3000000); // error OutOfMemoryError
//        batchInsertUpdateService.updateBatch(5000000);  // error OutOfMemoryError

        return "OK";
    }


}
