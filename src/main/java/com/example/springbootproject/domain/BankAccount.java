package com.example.springbootproject.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@ToString
@Builder
@Table("account")
public class BankAccount {

    @Id
    private int id;

    private String firstName;

    private int balance;

    private int branchId;


}
