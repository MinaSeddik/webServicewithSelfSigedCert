package com.example.springbootproject.domain;

import lombok.Getter;
import lombok.Setter;
//import org.hibernate.annotations.Cache;
//import org.hibernate.annotations.CacheConcurrencyStrategy;
//import org.hibernate.annotations.Formula;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

//import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

//@Entity
//@Table(name = "tableName", uniqueConstraints = {@UniqueConstraint(columnNames = {"id"})})
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "cache_region")
@Getter
@Setter
public class JpaEntity implements Serializable {


//    @Id
////    GenerationType.IDENTITY : It is responsibility of database to generate unique identifier.
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id", nullable = false, unique = true, length = 11)
    private int id;

//    @Column(name = "name", length = 20, nullable = true)
    private String name;

//    @Column(name = "first_name")
    private String firstName;

//    @Column(name = "last_name")
    private String lastName;

//    @Transient
    private String fullName;

//    @Column(name = "role", length = 20, nullable = true)
    private String role;

//    @Column(name = "insert_time", nullable = true)
    private Date insertTime;

    //    Collections aren't cached by default, and we need to explicitly mark them as cacheable
//    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
//    @OneToMany
    private Collection<Account2> accounts;

//    @Transient
    int age;

    @CreatedBy
    private Person2 creator;

    @LastModifiedBy
    private Person2 modifier;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date modifiedAt;

//    @Temporal(TemporalType.DATE)
    private Date birthDate;

    //    We don't have to specify the @Enumerated annotation at all if we're going to persist the Gender by the enum‘s ordinal.
//    @Enumerated(EnumType.STRING)
    private Gender gender;

    /*
        grossIncome and taxInPercents should be a valid database column name in the table

        When Hibernate fetches this entity from the database, it adds the SQL snippet of the formula
        annotation to its SQL statement.

        example:
        @Formula(value = "date_part(year, age(dateOfBirth))")

        select author0_.id as id1_0_, author0_.dateOfBirth as dateOfBi2_0_, author0_.firstName as firstNam3_0_, author0_.lastName as lastName4_0_,
        author0_.version as version5_0_,
        date_part(‘year’, age(author0_.dateOfBirth)) as formula0_    <------- the formula clause
        from Author author0_ where author0_.id=1

        Also, keep in mind that the value is calculated when the entity is fetched from the database.
     */
//    @Formula("grossIncome * taxInPercents / 100")
//    @Formula(value = "date_part(year, age(dateOfBirth))")
    private long tax;


}
