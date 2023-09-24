package com.example.springbootproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component("dbTables")
@Slf4j
public class SpelDatabaseTables {

    private Map<String, String> userColumns = new HashMap<>();

    @Autowired
    public SpelDatabaseTables() {

        userColumns.put("id", "id");
        userColumns.put("firstname", "first_name");
        userColumns.put("lastname", "last_name");
        userColumns.put("issueDate", "issue_date");

    }


}
