package com.example.springbootproject.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/*

CREATE TABLE person2
(
person_id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
first_name VARCHAR(40),
last_name VARCHAR(40),
role VARCHAR(30),
age INTEGER UNSIGNED NOT NULL,
insert_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


 */


@Entity
@Table(name = "person2", uniqueConstraints = {@UniqueConstraint(columnNames = {"person_id"})})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "person2")
@Getter
@Setter
public class Person2 implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private Integer id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "role", length = 20, nullable = true)
    private String role;

    @Column(name = "age")
    private int age;

    @Column(name = "insert_time")
    private Date insertTime;

//    @OneToMany(cascade = CascadeType.ALL)
//    private List<Account2> accounts;

    //    Collections aren't cached by default, and we need to explicitly mark them as cacheable
//    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//    @OneToMany
//    private Collection<Address> addresses;


    @Transient
    private String fullName;

//    @Formula("grossIncome * taxInPercents / 100")
//    private long tax;

}
