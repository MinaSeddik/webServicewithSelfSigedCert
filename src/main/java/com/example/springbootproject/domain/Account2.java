package com.example.springbootproject.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/*

CREATE TABLE account2
(
account_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(40)
);


 */

@Entity
@Table(name = "account2",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"account_id"})})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "account")
@Getter
@Setter
public class Account2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private int id;

    @Column(name = "name")
    private String name;
}
