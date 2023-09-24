package com.example.springbootproject.repository.impl;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Disabled("Disabled until further notice!")
class JpaMultipleEntityRepositoryTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
//    @Autowired
//    private UserRepository userRepository;

    //    @Sql("createUser.sql")
//    @Sql(statements = {
//            "INSERT INTO ITEM (name) VALUES ('Book-1');",
//            "INSERT INTO ITEM (name) VALUES ('Book-2');"
//    })
    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
//        assertThat(userRepository).isNotNull();
    }


    //    @SqlGroup({
//            @Sql({"/sql/items-grocery.sql"}),
//            @Sql({"/sql/items-books.sql"})
//    })
    @Test
    void injectedComponentsAreNotNull2() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
//        assertThat(userRepository).isNotNull();
    }
}