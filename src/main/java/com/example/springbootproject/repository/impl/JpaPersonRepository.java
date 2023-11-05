package com.example.springbootproject.repository.impl;


import com.example.springbootproject.domain.Person2;
//import com.example.springbootproject.repository.jpa.NamesOnly;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

//Reference: https://www.amitph.com/spring-data-jpa-query-methods/

@Repository
@Profile("spring-data-jpa")
public interface JpaPersonRepository extends CrudRepository<Person2, Integer> {

    /*
        The Spring Data Repository will auto-generate the implementation based on
        the name we provided it.
     */
    Optional<Person2> findByFirstName(String userName);
    List<Person2> findAll();
    Optional<Person2> findById(Long employeeId);
    List<Person2> findByFirstNameAndLastName(String firstName, String lastName);
    List<Person2> findByDepartmentNameAndAgeLessThanOrderByDateOfJoiningDesc(String departmentName, int maxAge);
    Person2 findDistinctFirstByAge(int age);
    List<Person2> findByAgeAndHeight(Integer age, Double height);
    List<Person2> findByAgeAndNameAndColor(Integer age, String name, String color);
    List<Person2> findByNameOrAge(String name, Integer age);
    List<Person2> findByNameIgnoreCaseAndColor(String name, String color);
    Integer countByName(String name);
    Integer countByNameAndLastName(String name, String lastName);
    Person2 findFirstByName(String name);
    Person2 findTopByName(String name);
    List<Person2> findTop10ByColor(String color);
    Person2 findTopByOrderByBirthDateDesc();
    List<Person2> findByNameContaining(String subName);
    List<Person2> findByNameStartingWith(String subName);
    List<Person2> findByHeightLessThan(double height);
    List<Person2> findByAgeLessThanOrHeightGreaterThan(Integer age, double height);
    List<Person2> findByAgeGreaterThanAndAgeLessThan(Integer ageStart, Integer ageEnd);
    List<Person2> findByAgeGreaterThanEqual(Integer age);
    List<Person2> findByDateOfBirthBetween(Date start, Date end);
    List<Person2> findByNameAndAddressZipCode(Integer zipCode);
    List<Person2> findByNameAndAddress_ZipCode(Integer zipCode);

    /*
        https://www.baeldung.com/spring-data-jpa-projections

        selects sub-set of fields - Not all column
     */
//    Collection<NamesOnly> findByLastName(String lastname);
    <T> T findByLastName(String lastName, Class<T> type);



    /*
        when we use the @Query annotation, the query method name doesn’t play any role.
        By default, the query definition uses JPQL.

        We can use also native SQL to define our query. All we have to do is set the value of the
        nativeQuery attribute to true and define the native SQL query in the value attribute
        of the annotation

     */



//    @Query("SELECT MAX(eventId) AS eventId FROM Event ")
    Long lastProcessedEvent();

//    @Query(value = "SELECT * FROM USERS u WHERE u.status = 1", nativeQuery = true)
    Collection<Person2> findAllActiveUsersNative();


//    @Query(" FROM Event WHERE status = :status AND TIME_TO_SEC(TIMEDIFF(:now, lastUpdateTs)) >= :duration")
    List<Person2> findByStatusAndDuration(  @Param("status") String status,
                                            @Param("duration") Integer duration,
                                            @Param("now") Timestamp now);

    /*
        Usage:  findAllUsers(Sort.by(Sort.Direction.ASC, "name"))
                findAllUsers(Sort.by("name"))

        Note:   When the @Query annotation uses native SQL, then it's not possible to define a Sort.

     */
//    @Query(value = "SELECT u FROM User u")
    List<Person2> findAllUsers(Sort sort);

//    @Query(value = "SELECT u FROM User u ORDER BY id")
    Page<Person2> findAllUsersWithPagination(Pageable pageable);

    // enable pagination for native queries by declaring an additional attribute countQuery.
//    @Query(value = "SELECT * FROM Users ORDER BY id", countQuery = "SELECT count(*) FROM Users", nativeQuery = true)
    Page<Person2> findAllUsersWithPagination2(Pageable pageable);

//    @Query("SELECT u FROM User u WHERE u.status = ?1")
    Person2 findUserByStatus(Integer status);

//    @Query("SELECT u FROM User u WHERE u.status = ?1 and u.name = ?2")
    Person2 findUserByStatusAndName(Integer status, String name);

//    @Query(value = "SELECT * FROM Users u WHERE u.status = ?1", nativeQuery = true)
    Person2 findUserByStatusNative(Integer status);

//    @Query("SELECT u FROM User u WHERE u.status = :status and u.name = :name")
    Person2 findUserByStatusAndNameNamedParams(@Param("status") Integer status, @Param("name") String name);


//    @Query(value = "SELECT * FROM Users u WHERE u.status = :status and u.name = :name", nativeQuery = true)
    Person2 findUserByStatusAndNameNamedParamsNative(@Param("status") Integer status, @Param("name") String name);

//    @Query(value = "SELECT u FROM User u WHERE u.name IN :names")
    List<Person2> findUserByNameList(@Param("names") Collection<String> names);

//    @Modifying
    @Transactional
//    @Query("update User u set u.status = :status where u.name = :name")
    int updateUserSetStatusForName(@Param("status") Integer status, @Param("name") String name);

//    @Modifying
    @Transactional
//    @Query(value = "update Users u set u.status = ? where u.name = ?", nativeQuery = true)
    int updateUserSetStatusForNameNative(Integer status, String name);

    /*
        To perform an insert operation, we have to both apply @Modifying and use a native query
        since INSERT is not a part of the JPA interface:
     */
//    @Modifying
    @Transactional
//    @Query(value = "insert into Users (name, age, email, status) values (:name, :age, :email, :status)", nativeQuery = true)
    void insertUser(@Param("name") String name, @Param("age") Integer age,
                    @Param("status") Integer status, @Param("email") String email);

    @Transactional
//    @Modifying
//    @Query(value = "UPDATE USER_USAGE SET DAILY_COUNT = DAILY_COUNT + ?, WEEKLY_COUNT= WEEKLY_COUNT + ?,MONTHLY_COUNT= MONTHLY_COUNT + ?, "
//            + " DAILY_USAGE = DAILY_USAGE + ?, WEEKLY_USAGE = WEEKLY_USAGE + ?, MONTHLY_USAGE = MONTHLY_USAGE + ? WHERE USER_ID = ? AND SERVICE_ID = ?", nativeQuery = true)
    int updateUsage(int dailyc, int weeklyc, int monthlyc, double dailya, double weeklya, double monthlya, String userId, Integer service);

    // call stored procedure
//    @Query(value = "CALL FIND_MY_ENTITY(:proc_param);", nativeQuery = true)
    List<Person2> findEntitesByParam(@Param("proc_param") String procParam);

    /*

        https://www.baeldung.com/spring-data-jpa-stored-procedures

        CREATE PROCEDURE GET_TOTAL_CARS_BY_MODEL(IN model_in VARCHAR(50), OUT count_out INT)
        BEGIN
            SELECT COUNT(*) into count_out from car WHERE model = model_in;
        END
     */
//    @Procedure("GET_TOTAL_CARS_BY_MODEL")
    int getTotalCarsByModel(String model);

//    @Procedure(procedureName = "GET_TOTAL_CARS_BY_MODEL")
    int getTotalCarsByModelProcedureName(String model);


    /*
        CREATE PROCEDURE FIND_CARS_AFTER_YEAR(IN year_in INT)
        BEGIN
            SELECT * FROM car WHERE year >= year_in ORDER BY year;
        END

     */
//    @Query(value = "CALL FIND_CARS_AFTER_YEAR(:year_in);", nativeQuery = true)
    List<Person2> findCarsAfterYear(@Param("year_in") Integer year_in);


}